package com.elixsr.portforwarder.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.elixsr.portforwarder.R;

/**
 * Created by Niall McShane on 29/02/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
