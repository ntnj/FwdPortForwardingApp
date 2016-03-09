package com.elixsr.portforwarder.ui.preferences;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.db.RuleContract;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.forwarding.ForwardingManager;

/**
 * Created by Niall McShane on 29/02/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = "SettingsFragment";

    private static final String CLEAR_RULES_COMPLETE_MESSAGE = "All rules have been removed";
    private ForwardingManager forwardingManager;
    private Preference clearRulesButton;
    private Preference versionNamePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        forwardingManager = ForwardingManager.getInstance();

        clearRulesButton = (Preference)findPreference(getString(R.string.pref_clear_rules));

        clearRulesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete all Rules")
                        .setMessage("Are you sure you want to delete all rules?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // set up the database
                                SQLiteDatabase db = new RuleDbHelper(getActivity()).getReadableDatabase();


                                db.delete(RuleContract.RuleEntry.TABLE_NAME, null, null);

                                db.close();

                                clearRulesButton.setEnabled(false);

                                Toast.makeText(getActivity(), CLEAR_RULES_COMPLETE_MESSAGE,
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            }
        });

        versionNamePreference = (Preference)findPreference(getString(R.string.pref_version));

        // set up click of help button - show webview
        Preference helpButton = (Preference) findPreference(getString(R.string.pref_help_link));
        helpButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                Intent helpActivityIntent = new Intent(getActivity(), HelpActivity.class);
                startActivity(helpActivityIntent);
                return true;
            }
        });

        // set up click of about elixsr button - show webview
        Preference aboutElixsrButton = (Preference) findPreference(getString(R.string.pref_about_link));
        aboutElixsrButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                Intent aboutActivityIntent = new Intent(getActivity(), AboutElixsrActivity.class);
                startActivity(aboutActivityIntent);
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(forwardingManager.isEnabled()){
            clearRulesButton.setEnabled(false);
        }else{
            clearRulesButton.setEnabled(true);
        }

        String versionName = "Version ";
        try {
            versionName = versionName + getActivity().getBaseContext().getPackageManager()
                    .getPackageInfo(getActivity().getBaseContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Application Version could not be found.", e);
            versionName = versionName + "not found";
        }
        versionNamePreference.setTitle(versionName);
    }
}
