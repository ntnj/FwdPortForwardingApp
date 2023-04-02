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
package com.elixsr.portforwarder.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.ui.preferences.SettingsFragment

/**
 * Created by Niall McShane on 28/02/2016.
 */
abstract class BaseActivity : AppCompatActivity() {
    private var themeChangeReceiver: ThemeChangeReceiver? = null
    override fun onCreate(ofJoy: Bundle?) {

        // Handle intents
        val themeChangeIntentFilter = IntentFilter(
                SettingsFragment.DARK_MODE_BROADCAST)
        themeChangeReceiver = ThemeChangeReceiver()

        // Registers the ForwardingServiceResponseReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                themeChangeReceiver!!,
                themeChangeIntentFilter)

        // Check preferences to determine which theme is requested
        if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.DarkTheme_NoActionBar)
        }
        super.onCreate(ofJoy)
    }

    override fun onResume() {
        // Check preferences to determine which theme is requested
        if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.DarkTheme_NoActionBar)
        }
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: CALLED")

        // Ensure that it is no longer looking out for broadcasts
        LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver!!)
    }

    // Primary toolbar and drawer toggle
    private var mActionBarToolbar: Toolbar? = null
    protected val actionBarToolbar: Toolbar?
        protected get() {
            if (mActionBarToolbar == null) {
                mActionBarToolbar = findViewById<View>(R.id.toolbar_actionbar) as Toolbar
                if (mActionBarToolbar != null) {
                    // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                    // active so the a11y description is set here.
                    setSupportActionBar(mActionBarToolbar)
                }
            }
            return mActionBarToolbar
        }

    // Broadcast receiver for receiving status updates from the IntentService
    private inner class ThemeChangeReceiver  // Prevents instantiation
        : BroadcastReceiver() {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "onReceive: style changed")
            recreate()
        }
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}