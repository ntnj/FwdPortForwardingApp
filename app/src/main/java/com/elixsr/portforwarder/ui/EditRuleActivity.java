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
import com.elixsr.portforwarder.forwarding.ForwardingService;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Niall McShane on 02/03/2016.
 */
public class EditRuleActivity extends BaseRuleActivity {

    private static final String TAG = "EditRuleActivity";

    private static final String NO_RULE_ID_FOUND_LOG_MESSAGE = "No ID was supplied to EditRuleActivity";
    private static final String NO_RULE_ID_FOUND_TOAST_MESSAGE = "Could not locate rule";

    private RuleModel ruleModel;

    private long ruleModelId;

    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //if we can't locate the id, then we can't continue
        if(!getIntent().getExtras().containsKey(RuleHelper.RULE_MODEL_ID)){

            /// show toast containing message to the user
            Toast.makeText(this, NO_RULE_ID_FOUND_TOAST_MESSAGE,
                    Toast.LENGTH_SHORT).show();

            Log.e(TAG, NO_RULE_ID_FOUND_LOG_MESSAGE);

            onBackPressed();

            //return from the method - ensure we don't continue
            return;
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


        //use the base class to construct the common UI
        constructDetailUi();

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

        //close the DB
        cursor.close();
        db.close();

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
        Log.i(TAG, "FROM SPINNER : " + fromInterfaceSpinner.toString());
        Log.i(TAG, "FROM INTERFACE : " + this.ruleModel.getFromInterfaceName());
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


                //set the item to disabled while saving
                item.setEnabled(false);
                saveEditedRule();
                item.setEnabled(true);
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

            this.db = new RuleDbHelper(this).getReadableDatabase();

            int count = db.update(
                    RuleContract.RuleEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            //close db
            db.close();


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
//                        MainActivity.ruleListAdapter.notifyItemRemoved(ruleModelLocation);

                        //construct the db
                        db = new RuleDbHelper(getBaseContext()).getReadableDatabase();

                        // Define 'where' part of query.
                        String selection = RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + "=?";
                        // Specify arguments in placeholder order.
                        String[] selectionArgs = { String.valueOf(ruleModelId) };
                        // Issue SQL statement.
                        db.delete(RuleContract.RuleEntry.TABLE_NAME, selection, selectionArgs);

                        //close the db
                        db.close();

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
