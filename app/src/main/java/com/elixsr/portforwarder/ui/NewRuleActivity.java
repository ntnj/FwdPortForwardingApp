package com.elixsr.portforwarder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.SocketException;
import java.util.List;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.dao.RuleDao;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.models.RuleModel;

/**
 * Created by Niall McShane on 29/02/2016.
 */
public class NewRuleActivity extends BaseRuleActivity {

    private static final String TAG = "NewRuleActivity";

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

        //set up protocol spinner/dropdown
        Spinner protocolSpinner = (Spinner) findViewById(R.id.protocol_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rule_protocol_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        protocolSpinner.setAdapter(adapter);



        //generate interfaces
        List<String> interfaces = null;
        try {
            interfaces = generateInterfaceList();
        } catch (SocketException e) {
            Log.i(TAG, "Error generating Interface list", e);

            //TODO: add better exception handling
        }

        //set up protocol spinner/dropdown
        Spinner fromInterfaceSpinner = (Spinner) findViewById(R.id.from_interface_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> fromSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, interfaces);

        // Specify the layout to use when the list of choices appears
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        fromInterfaceSpinner.setAdapter(fromSpinnerAdapter);
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
            saveNewRule();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void saveNewRule(){

        RuleModel ruleModel = generateNewRule();

        if(ruleModel.isValid()){
            Log.i(TAG, "Rule " + ruleModel.getName() + " is valid, time to save.");

            //create a DAO and save the object
            RuleDao ruleDao = new RuleDao(new RuleDbHelper(this));
            long newRowId = ruleDao.insertRule(ruleModel);

            Log.i(TAG, "Rule #" + newRowId + " " + ruleModel.getName() + " has been saved.");

            // move to main activity
            Intent mainActivityIntent = new Intent(this, com.elixsr.portforwarder.MainActivity.class);
            startActivity(mainActivityIntent);
        }else{
            Toast.makeText(this, "Rule is not valid. Please check your input.",
                    Toast.LENGTH_LONG).show();
        }


    }
}
