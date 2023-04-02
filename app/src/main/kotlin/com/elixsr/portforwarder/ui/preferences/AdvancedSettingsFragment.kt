package com.elixsr.portforwarder.ui.preferences

import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment
import com.elixsr.portforwarder.R

/**
 * Created by Cathan on 05/03/2017.
 */
class AdvancedSettingsFragment : PreferenceFragment() {
    private val sharedPreferencesListener: OnSharedPreferenceChangeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.advanced_preferences)
        val ipChecker = findPreference(getString(R.string.pref_ip_checker))
        ipChecker.onPreferenceClickListener = OnPreferenceClickListener { preference: Preference? ->
            val ipCheckerActivity = Intent(activity, IpAddressCheckerActivity::class.java)
            startActivity(ipCheckerActivity)
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Ensure we unregister our previous listener - as it now points to a null activity
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }
}