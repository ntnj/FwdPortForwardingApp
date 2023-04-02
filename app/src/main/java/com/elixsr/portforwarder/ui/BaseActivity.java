/*
 * Fwd: the port forwarding app
 * Copyright (C) 2016  Elixsr Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elixsr.portforwarder.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.ui.preferences.SettingsFragment;

/**
 * Created by Niall McShane on 28/02/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private ThemeChangeReceiver themeChangeReceiver;

    @Override
    protected void onCreate(Bundle ofJoy) {

        // Handle intents
        IntentFilter themeChangeIntentFilter = new IntentFilter(
                SettingsFragment.DARK_MODE_BROADCAST);

        themeChangeReceiver =
                new ThemeChangeReceiver();

        // Registers the ForwardingServiceResponseReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                themeChangeReceiver,
                themeChangeIntentFilter);

        // Check preferences to determine which theme is requested
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.DarkTheme_NoActionBar);
        }
        super.onCreate(ofJoy);
    }

    @Override
    protected void onResume() {
        // Check preferences to determine which theme is requested
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.DarkTheme_NoActionBar);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy: CALLED");

        // Ensure that it is no longer looking out for broadcasts
        LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver);
    }

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    protected Toolbar getActionBarToolbar() {

        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ThemeChangeReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private ThemeChangeReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: style changed");
            recreate();
        }
    }


}
