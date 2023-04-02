package com.elixsr.portforwarder.ui.preferences;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.ui.BaseActivity;

public class AdvancedSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);
        Toolbar toolbar = getActionBarToolbar();
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getFragmentManager().beginTransaction().replace(R.id.advanced_settings_container, new AdvancedSettingsFragment()).commit();
    }

}
