package com.elixsr.portforwarder.ui.preferences

import android.os.Bundle
import android.view.View
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.ui.BaseActivity

class AdvancedSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_settings)
        val toolbar = actionBarToolbar
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        fragmentManager.beginTransaction().replace(R.id.advanced_settings_container, AdvancedSettingsFragment()).commit()
    }
}