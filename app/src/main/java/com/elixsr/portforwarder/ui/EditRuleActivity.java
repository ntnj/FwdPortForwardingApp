package com.elixsr.portforwarder.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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
import com.elixsr.portforwarder.db.RuleContract;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Niall McShane on 02/03/2016.
 */
public class EditRuleActivity extends BaseRuleActivity {

    private static final String TAG = "EditRuleActivity";

    private RuleModel ruleModel;

    private long ruleModelId;

    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(!getIntent().getExtras().containsKey(RuleHelper.RULE_MODEL_ID)){
            //TODO: add a more relevant exception
//            throw new ItemNot("Could not find the relevenat Id");
        }
        ruleModelId = getIntent().getExtras().getLong(RuleHelper.RULE_MODEL_ID);

        setContentView(R.layout.edit_rule_activity);

        //set up toolbar
        Toolbar toolbar = getActionBarToolbar();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Rule");

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
        ArrayAdapter<CharSequence> protocolAdapter = ArrayAdapter.createFromResource(this,
                R.array.rule_protocol_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        protocolSpinner.setAdapter(protocolAdapter);


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



        //TODO: move this
        this.db = new RuleDbHelper(this).getReadableDatabase();

        Cursor cursor = db.query(
                RuleContract.RuleEntry.TABLE_NAME,
                RuleDbHelper.generateAllRowsSelection(),
                RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + "=?",
                new String[]{String.valueOf(ruleModelId)},
                null,
                null,
                null
        );

        cursor.moveToFirst();

        this.ruleModel = RuleHelper.cursorToRuleModel(cursor);

        /*
        Set the text fields content
         */
        TextInputEditText newRuleNameEditText = (TextInputEditText) findViewById(R.id.new_rule_name);
        newRuleNameEditText.setText(ruleModel.getName());

        TextInputEditText newRuleFromPortEditText = (TextInputEditText) findViewById(R.id.new_rule_from_port);
        newRuleFromPortEditText.setText(String.valueOf(ruleModel.getFromPort()));

        TextInputEditText newRuleTargetIpAddressEditText = (TextInputEditText) findViewById(R.id.new_rule_target_ip_address);
        newRuleTargetIpAddressEditText.setText(ruleModel.getTargetIpAddress());

        TextInputEditText newRuleTargetPortEditText = (TextInputEditText) findViewById(R.id.new_rule_target_port);
        newRuleTargetPortEditText.setText(String.valueOf(ruleModel.getTargetPort()));

        /*
        Set the spinners content
         */
        //from interface spinner
        fromInterfaceSpinner.setSelection(fromSpinnerAdapter.getPosition(this.ruleModel.getFromInterfaceName()));

        //protocol spinner
        protocolSpinner.setSelection(protocolAdapter.getPosition(RuleHelper.getRuleProtocolFromModel(this.ruleModel)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_save_rule:
                Log.i(TAG, "Save Menu Button Clicked");
                saveEditedRule();
                break;
            case R.id.action_delete_rule:
                deleteRule();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveEditedRule(){
        this.ruleModel = generateNewRule();

        if(ruleModel.isValid()) {
            Log.i(TAG, "Rule " + ruleModel.getName() + " is valid, time to update.");
            SQLiteDatabase db = new RuleDbHelper(this).getReadableDatabase();

            // New model to store
            ContentValues values = RuleHelper.ruleModelToContentValues(this.ruleModel);

            // Which row to update, based on the ID
            String selection = RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + "=?";
            String[] selectionArgs = {String.valueOf(this.ruleModelId)};

            int count = db.update(
                    RuleContract.RuleEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);


            // move to main activity
            Intent mainActivityIntent = new Intent(this, com.elixsr.portforwarder.MainActivity.class);
            startActivity(mainActivityIntent);
        }else{
            Toast.makeText(this, "Rule is not valid. Please check your input.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void deleteRule(){

        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        //TODO: add db delete
//                        MainActivity.RULE_MODELS.remove(ruleModelLocation);
//                        MainActivity.RULE_LIST_ADAPTER.notifyItemRemoved(ruleModelLocation);



                        // Define 'where' part of query.
                        String selection = RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + "=?";
                        // Specify arguments in placeholder order.
                        String[] selectionArgs = { String.valueOf(ruleModelId) };
                        // Issue SQL statement.
                        db.delete(RuleContract.RuleEntry.TABLE_NAME, selection, selectionArgs);

                        // move to main activity
                        Intent mainActivityIntent = new Intent(getBaseContext(), com.elixsr.portforwarder.MainActivity.class);
                        startActivity(mainActivityIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();


    }

}
