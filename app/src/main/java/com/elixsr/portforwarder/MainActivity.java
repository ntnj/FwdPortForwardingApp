package com.elixsr.portforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import com.elixsr.portforwarder.dao.RuleDao;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.forwarding.ForwardingManager;
import com.elixsr.portforwarder.forwarding.ForwardingService;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.ui.BaseActivity;
import com.elixsr.portforwarder.ui.NewRuleActivity;
import com.elixsr.portforwarder.adapters.RuleListAdapter;
import com.elixsr.portforwarder.ui.SettingsActivity;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final String FORWARDING_SERVICE_TAG = "ForwardingManager";

    //TODO: remove when no longer developing/prototyping
    public List<RuleModel> ruleModels;
    public static RuleListAdapter RULE_LIST_ADAPTER;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private ForwardingManager forwardingManager;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;

    private Intent forwardingServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get an instance of the forwarding manager
        this.forwardingManager = ForwardingManager.getInstance();

        setContentView(R.layout.activity_main);
        setSupportActionBar(getActionBarToolbar());

        final Intent newRuleIntent = new Intent(this, NewRuleActivity.class);

        // move to the new rule activity
        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(newRuleIntent);
            }
        });

        // hide the fab if forwarding is enabled
        // the user should not be able to add/delete rules
        if (this.forwardingManager.isEnabled()) {
            //if the forwarding service is enabled, then we should ensure not to show the fab
            fab.hide();
        } else {
            fab.show();
        }

        //get all models from the data store
        RuleDao ruleDao = new RuleDao(new RuleDbHelper(this));
        ruleModels = ruleDao.getAllRuleModels();

        //set up rule list
        mRecyclerView = (RecyclerView) findViewById(R.id.rule_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RULE_LIST_ADAPTER = new RuleListAdapter(ruleModels, forwardingManager);
        mRecyclerView.setAdapter(RULE_LIST_ADAPTER);

        //store the coordinator layout for snackbar
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);

        forwardingServiceIntent = new Intent(this, ForwardingService.class);

        /*
            Service stuff
         */

        //handle intents
        IntentFilter mStatusIntentFilter = new IntentFilter(
                ForwardingService.BROADCAST_ACTION);

        // Instantiates a new ForwardingServiceResponseReceiver
        ForwardingServiceResponseReceiver forwardingServiceResponseReceiver =
                new ForwardingServiceResponseReceiver();

        // Registers the ForwardingServiceResponseReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                forwardingServiceResponseReceiver,
                mStatusIntentFilter);

        Log.i(TAG, "Finished onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_toggle_forwarding).setTitle(generateForwardingActionMenuText(forwardingManager.isEnabled()));

        //setup the start forwarding button
        MenuItem toggleForwarding = menu.findItem(R.id.action_toggle_forwarding);

        //it should not be able to start if there are no rules
        if(ruleModels.size() <= 0){
            toggleForwarding.setVisible(false);
        }else{
            toggleForwarding.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent prefIntent = new Intent(this, SettingsActivity.class);
                startActivity(prefIntent);
                break;
            case R.id.action_toggle_forwarding:

                handleForwardingButton(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleForwardingButton(MenuItem item) {


        if (!forwardingManager.isEnabled()) {
//                    startPortForwarding();

            Snackbar.make(this.coordinatorLayout, "Port Forwarding Started", Snackbar.LENGTH_LONG)
                    .setAction("Stop", null).show();

            fab.hide();

            startService(forwardingServiceIntent);
        } else {
            //stop forwarding
            fab.show();

            Snackbar.make(this.coordinatorLayout, "Port Forwarding Stopped", Snackbar.LENGTH_LONG).show();

            stopService(forwardingServiceIntent);
        }

        Log.i(TAG, "Forwarding Enabled: " + forwardingManager.isEnabled());
        item.setTitle(generateForwardingActionMenuText(forwardingManager.isEnabled()));
    }



    private static String generateForwardingActionMenuText(boolean forwardingFlag) {
        if (forwardingFlag) {
            return "Stop";
        }
        return "Start";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(FORWARDING_SERVICE_TAG, this.forwardingManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //when this activity is destroyed, destroy the forwarding service
        stopService(forwardingServiceIntent);
        Log.i(TAG, "Destroyed");
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ForwardingServiceResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ForwardingServiceResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Handle Intents here.
             */

            if(intent.getExtras().containsKey(ForwardingService.PORT_FORWARD_SERVICE_STATE)){
                Log.i(TAG, "Response from ForwardingService, Forwarding status has changed.");
                Log.i(TAG, "Forwarding status has changed to " + String.valueOf(intent.getExtras().getBoolean(ForwardingService.PORT_FORWARD_SERVICE_STATE)));
                invalidateOptionsMenu();
            }

            if(intent.getExtras().containsKey(ForwardingService.PORT_FORWARD_SERVICE_ERROR_MESSAGE)){

                Toast.makeText(context, intent.getExtras().getString(ForwardingService.PORT_FORWARD_SERVICE_ERROR_MESSAGE),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
