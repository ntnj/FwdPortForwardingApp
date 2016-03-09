package com.elixsr.portforwarder.ui.preferences;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.ui.BaseActivity;

/**
 * Created by Niall McShane on 29/02/2016.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        Toolbar toolbar = getActionBarToolbar();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();


    }

}
