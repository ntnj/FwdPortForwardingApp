package com.elixsr.portforwarder.ui.rules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elixsr.portforwarder.FwdApplication;
import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.dao.RuleDao;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.ui.MainActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Niall McShane on 29/02/2016.
 */
public class NewRuleActivity extends BaseRuleActivity {

    private static final String TAG = "NewRuleActivity";
    private static final String LABEL_SAVE_RULE = "Rule Saved";
    private Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_rule_activity);

        //set up toolbar
        Toolbar toolbar = getActionBarToolbar();
        setSupportActionBar(toolbar);
        toolbar.setTitle("New Rule");

        toolbar.setNavigationIcon(R.drawable.ic_close_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        constructDetailUi();

        // Get tracker.
        tracker = ((FwdApplication) this.getApplication()).getDefaultTracker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_rule_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_save_rule){
            Log.i(TAG, "Save Menu Button Clicked");

            //set the item to disabled while saving
            item.setEnabled(false);
            saveNewRule();
            item.setEnabled(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void saveNewRule(){

        RuleModel ruleModel = generateNewRule();

        if(ruleModel.isValid()){
            Log.i(TAG, "Rule '" + ruleModel.getName() + "' is valid, time to save.");

            //create a DAO and save the object
            RuleDao ruleDao = new RuleDao(new RuleDbHelper(this));
            long newRowId = ruleDao.insertRule(ruleModel);

            Log.i(TAG, "Rule #" + newRowId + " '" + ruleModel.getName() + "' has been saved.");


            // Build and send an Event.
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(CATEGORY_RULES)
                    .setAction(ACTION_SAVE)
                    .setLabel(LABEL_SAVE_RULE)
                    .build());

            // move to main activity
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);

        }else{
            Toast.makeText(this, "Rule is not valid. Please check your input.",
                    Toast.LENGTH_LONG).show();
        }


    }
}
