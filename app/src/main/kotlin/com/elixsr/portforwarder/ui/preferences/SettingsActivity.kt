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
package com.elixsr.portforwarder.ui.preferences

import android.os.Bundle
import android.view.View
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.ui.BaseActivity

/**
 * Created by Niall McShane on 29/02/2016.
 */
class SettingsActivity : BaseActivity() {
    private val TAG = "SettingsActivity"
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val toolbar = actionBarToolbar
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        fragmentManager.beginTransaction().replace(R.id.content_frame, SettingsFragment()).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}