package com.elixsr.portforwarder.forwarding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Niall McShane on 07/06/2016.
 *
 * @see <a href="http://stackoverflow.com/questions/6391902/how-to-start-an-application-on-startup">How to start an Application on startup?</a>
 */
public class StartForwardingServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, ForwardingService.class);
            context.startService(serviceIntent);
        }
    }
}