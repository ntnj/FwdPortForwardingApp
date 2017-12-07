package com.elixsr.portforwarder.ui.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.elixsr.portforwarder.R;

/**
 * Created by Cathan on 05/03/2017.
 */

public class AdvancedSettingsFragment extends PreferenceFragment {

    private Preference advertisementsEnabled;
    private Preference ipChecker;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferencesListener;

    public AdvancedSettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.advanced_preferences);
        advertisementsEnabled = (Preference) findPreference(getString(R.string.pref_enable_ads));

        ipChecker = (Preference) findPreference(getString(R.string.pref_ip_checker));

        ipChecker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent ipCheckerActivity = new Intent(getActivity(), IpAddressCheckerActivity.class);
                startActivity(ipCheckerActivity);
                return true;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Ensure we unregister our previous listener - as it now points to a null activity
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener);
    }


}
