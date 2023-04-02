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
package com.elixsr.portforwarder.ui.rules

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.dao.RuleDao
import com.elixsr.portforwarder.db.RuleDbHelper
import com.elixsr.portforwarder.ui.MainActivity

/**
 * Created by Niall McShane on 29/02/2016.
 */
class NewRuleActivity : BaseRuleActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_rule_activity)

        // Set up toolbar
        val toolbar = actionBarToolbar
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_close_24dp)
        toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        constructDetailUi()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.new_rule_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_save_rule) {
            Log.i(TAG, "Save Menu Button Clicked")

            // Set the item to disabled while saving
            item.isEnabled = false
            saveNewRule()
            item.isEnabled = true
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNewRule() {
        val ruleModel = generateNewRule()
        if (ruleModel.isValid) {
            Log.i(TAG, "Rule '" + ruleModel.name + "' is valid, time to save.")

            // Create a DAO and save the object
            val ruleDao = RuleDao(RuleDbHelper(this))
            val newRowId = ruleDao.insertRule(ruleModel)
            Log.i(TAG, "Rule #" + newRowId + " '" + ruleModel.name + "' has been saved.")

            // Move to main activity
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(mainActivityIntent)
            finish()
        } else {
            Toast.makeText(this, "Rule is not valid. Please check your input.",
                    Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "NewRuleActivity"
    }
}