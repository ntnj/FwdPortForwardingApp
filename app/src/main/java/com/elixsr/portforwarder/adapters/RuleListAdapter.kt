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
package com.elixsr.portforwarder.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.forwarding.ForwardingManager
import com.elixsr.portforwarder.models.RuleModel
import com.elixsr.portforwarder.ui.rules.EditRuleActivity

/**
 * Created by Niall McShane on 01/03/2016.
 */
class RuleListAdapter(ruleModels: List<RuleModel>, private val forwardingManager: ForwardingManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listItems: MutableList<ListItem<*>>

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class RuleViewHolder(v: View, private val forwardingManager: ForwardingManager) : RecyclerView.ViewHolder(v), View.OnClickListener {
        // each data item is just a string in this case
        var ruleProtocolText: TextView
        var ruleNameText: TextView
        var ruleFromPortText: TextView
        var ruleTargetPortText: TextView
        var ruleId: Long = 0

        init {
            ruleProtocolText = v.findViewById(R.id.rule_item_protocol)
            ruleNameText = v.findViewById(R.id.rule_item_name)
            ruleFromPortText = v.findViewById(R.id.rule_item_from_port)
            ruleTargetPortText = v.findViewById(R.id.rule_item_target_port)
            v.setOnClickListener(this)
        }

        override fun onClick(view: View) {

            //ensure that editing cannot happen while tethering is enabled
            if (!forwardingManager.isEnabled) {
                val editRuleIntent = Intent(view.context, EditRuleActivity::class.java)
                editRuleIntent.putExtra("RuleModelLocation", adapterPosition)
                editRuleIntent.putExtra("RuleModelId", ruleId)
                view.context.startActivity(editRuleIntent)
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    init {
        listItems = ArrayList()
        for (rule in ruleModels) {
            val ruleListItem = ListItem<RuleModel>(RULE_VIEW)
            ruleListItem.setPayload(rule)
            listItems.add(ruleListItem)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rule_item_view, parent, false)
        return RuleViewHolder(view, forwardingManager)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val ruleHolder = holder as RuleViewHolder
        val ruleModel = listItems[position].payload as RuleModel

        //TODO: potentially should add some validation :/ / exception handling
        ruleHolder.ruleId = ruleModel.id
        ruleHolder.ruleProtocolText.text = ruleModel.protocolToString()
        if (!ruleModel.isEnabled) {
            ruleHolder.ruleNameText.alpha = 0.4f
            ruleHolder.ruleProtocolText.setBackgroundResource(R.drawable.bg_grey)
        } else {
            ruleHolder.ruleNameText.alpha = 1f
            ruleHolder.ruleProtocolText.setBackgroundResource(R.drawable.bg_red)
        }
        ruleHolder.ruleNameText.text = ruleModel.name
        ruleHolder.ruleFromPortText.text = ruleModel.fromPort.toString()
        ruleHolder.ruleTargetPortText.text = ruleModel.target!!.port.toString()
    }

    override fun getItemViewType(position: Int): Int {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return listItems[position].viewType
    }

    // Return the size of your dataset (invoked by the layout manager)
    // Total amount of rules + 1 for an advertisement
    override fun getItemCount(): Int {
        return listItems.size
    }

    private class ListItem<T>(val viewType: Int) {
        var payload: T? = null
            private set

        fun setPayload(payload: T) {
            this.payload = payload
        }
    }

    companion object {
        private const val TAG = "RuleListAdapter"
        protected const val RULE_VIEW = 0
    }
}