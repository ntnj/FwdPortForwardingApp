/*
 * Fwd: the port forwarding app
 * Copyright (C) 2016  Elixsr Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elixsr.portforwarder.forwarding;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.dao.RuleDao;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.exceptions.ObjectNotFoundException;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.ui.MainActivity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The {@link ForwardingService} class acts as a controller of all all forwarding.
 * <p>
 * The class is responsible for starting forwarding for all rules found within the SQLite database.
 * <p>
 * The class creates a new thread for each Forwarding rule.
 */
public class ForwardingService extends IntentService {

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
            "com.elixsr.portforwarder.forwarding.ForwardingService.BROADCAST";

    public static final String PORT_FORWARD_SERVICE_STATE =
            "com.elixsr.portforwarder.forwarding.ForwardingService.PORT_FORWARD_STATE";

    public static final String PORT_FORWARD_SERVICE_ERROR_MESSAGE =
            "com.elixsr.portforwarder.forwarding.ForwardingService.PORT_FORWARD_ERROR_MESSAGE";

    private static final String PORT_FORWARD_SERVICE_WAKE_LOCK_TAG = "PortForwardServiceWakeLockTag:";

    private static final String TAG = "ForwardingService";

    private static final int NOTIFICATION_ID = 1;

    private boolean runService = false;

    //change the magic number
    private ExecutorService executorService;

    //wake lock
    private PowerManager.WakeLock wakeLock;

    /**
     * Default constructor for {@link ForwardingService}.#
     * <p>
     * Creates a new instance of ForwardingService and initialises an {@link ExecutorService}
     * with a fixed thread pool of 30 threads.
     */
    public ForwardingService() {
        super(TAG);
        executorService = Executors.newFixedThreadPool(30);
    }

    public ForwardingService(ExecutorService executorService) {
        super(TAG);
        this.executorService = executorService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        Sourced from: https://developer.android.com/intl/ja/training/scheduling/wakelock.html
         */
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                PORT_FORWARD_SERVICE_WAKE_LOCK_TAG);
        wakeLock.acquire();
    }

    /**
     * Starts forwarding based on rules found in database.
     * <p>
     * Acquires an instance of the Forwarding Manager to turn forwarding flag on.
     * <p>
     * Creates a list off callbacks for each forward thread, and handle exceptions as they come.
     * <p>
     * If an exception is thrown, the service immediately stops, and the #onDestroy method is
     * called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Gets data from the incoming Intent
//        String dataString = intent.getDataString();

        Log.i(TAG, "Ran the service");

        ForwardingManager.getInstance().enableForwarding();


        runService = true;

        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(PORT_FORWARD_SERVICE_STATE, ForwardingManager.getInstance().isEnabled());
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        showForwardingEnabledNotification();

        //load the rules from the datastore
        //TODO: inject the rules as extras
        RuleDao ruleDao = new RuleDao(new RuleDbHelper(this));
        List<RuleModel> ruleModels = ruleDao.getAllEnabledRuleModels();

        List<Forwarder> ruleModelForwarders = new ArrayList<>();

        InetSocketAddress from;

        // how many futures there are to check
        int remainingFutures = 0;

        for (RuleModel ruleModel : ruleModels) {

            // Something has killed the runService, no point in looping anymore
            if (!runService) {
                break;
            }

            try {
                from = generateFromIpUsingInterface(ruleModel.getFromInterfaceName(), ruleModel.getFromPort());

                if (ruleModel.isTcp() && runService) {
                    ruleModelForwarders.add(new TcpForwarder(from, ruleModel.getTarget(), ruleModel.getName()));
                    remainingFutures++;
                }

                if (ruleModel.isUdp() && runService) {
                    ruleModelForwarders.add(new UdpForwarder(from, ruleModel.getTarget(), ruleModel.getName()));
                    remainingFutures++;
                }

            } catch (SocketException | ObjectNotFoundException e) {
                Log.e(TAG, "Error generating IP Address for FROM interface with rule '" + ruleModel.getName() + "'", e);

                // graceful UI Exception handling - broadcast this to ui - it will deal with display something to the user e.g. a Toast
                localIntent =
                        new Intent(BROADCAST_ACTION)
                                // Puts the status into the Intent
                                .putExtra(PORT_FORWARD_SERVICE_ERROR_MESSAGE, getString(R.string.start_rule_error_message) + " '" + ruleModel.getName() + "'");
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }

        executorService = Executors.newFixedThreadPool(ruleModelForwarders.size());

        /*
         Sourced from: http://stackoverflow.com/questions/19348248/waiting-on-a-list-of-future
         */
        CompletionService<Void> completionService =
                new ExecutorCompletionService<>(executorService);

        for (Forwarder ruleForwarder : ruleModelForwarders) {
            completionService.submit(ruleForwarder);
        }

        Future<?> completedFuture;

        // loop through each callback, and handle an exception
        while (remainingFutures > 0 && runService) {

            // block until a callable completes
            try {
                completedFuture = completionService.take();
                remainingFutures--;

                completedFuture.get();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error when forwarding port.", e);
                localIntent =
                        new Intent(BROADCAST_ACTION)
                                // Puts the status into the Intent
                                .putExtra(PORT_FORWARD_SERVICE_ERROR_MESSAGE, e.getCause().getMessage());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private InetSocketAddress generateFromIpUsingInterface(String interfaceName, int port) throws SocketException, ObjectNotFoundException {

        String address;
        InetSocketAddress inetSocketAddress;

        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();

            Log.d(TAG, intf.getDisplayName() + " vs " + interfaceName);
            if (intf.getDisplayName().equals(interfaceName)) {

                Log.i(TAG, "Found the relevant Interface. Will attempt to fetch IP Address");

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    address = inetAddress.getHostAddress();
                    if (address != null && address.length() > 0 && inetAddress instanceof Inet4Address) {
                        inetSocketAddress = new InetSocketAddress(address, port);
                        return inetSocketAddress;
                    }
                }
            }
        }

        //Failed to find the relevant interface
        //TODO: complete
//        Toast.makeText(this, "Could not find relevant network interface.",
//                Toast.LENGTH_LONG).show();
        throw new ObjectNotFoundException("Could not find IP Address for Interface " + interfaceName);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved: called");

        this.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runService = false;

        // Reject any new tasks
        executorService.shutdown();

        try {
            // Shutdown any existing tasks
            executorService.shutdownNow();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                Log.e(TAG, "onDestroy: Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        ForwardingManager.getInstance().disableForwarding();

        hideForwardingEnabledNotification();

        //update the main activity
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(PORT_FORWARD_SERVICE_STATE, ForwardingManager.getInstance().isEnabled());
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        wakeLock.release();
        Log.i(TAG, "Ended the ForwardingService. Cleanup finished.");
    }

    private void hideForwardingEnabledNotification() {
        NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);
        notifManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Construct a notification
     */
    @SuppressLint("MissingPermission")
    private void showForwardingEnabledNotification() {
        NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);

        String channelId = "persistent";
        NotificationChannel channel = notifManager.getNotificationChannel(channelId);
        if (channel == null) {
            channel = new NotificationChannel(channelId, "Background",
                    NotificationManager.IMPORTANCE_HIGH);
            notifManager.createNotificationChannel(channel);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_fwd_24dp)
                        .setContentTitle(getString(R.string.notification_forwarding_active_title))
                        .setContentText(getString(R.string.notification_forwarding_touch_disable_text))
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                        .setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.DEFAULT_LIGHTS;

        notifManager.notify(NOTIFICATION_ID, notification);
    }
}
