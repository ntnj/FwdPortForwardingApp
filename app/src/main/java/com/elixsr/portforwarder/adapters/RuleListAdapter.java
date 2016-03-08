package com.elixsr.portforwarder.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.elixsr.portforwarder.forwarding.ForwardingManager;
import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.ui.EditRuleActivity;

/**
 * Created by Niall McShane on 01/03/2016.
 */
public class RuleListAdapter extends RecyclerView.Adapter<RuleListAdapter.ViewHolder> {
    private List<RuleModel> ruleModels;
    private ForwardingManager forwardingManager;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView ruleProtocolText;
        public TextView ruleNameText;
        public TextView ruleFromPortText;
        public TextView ruleTargetPortText;
        private ForwardingManager forwardingManager;
        private long ruleId;


        public ViewHolder(View v, ForwardingManager forwardingManager) {
            super(v);
            this.forwardingManager = forwardingManager;
            this.ruleId = ruleId;
            ruleProtocolText = (TextView) v.findViewById(R.id.rule_item_protocol);
            ruleNameText = (TextView) v.findViewById(R.id.rule_item_name);
            ruleFromPortText = (TextView) v.findViewById(R.id.rule_item_from_port);
            ruleTargetPortText = (TextView) v.findViewById(R.id.rule_item_target_port);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){

            //ensure that editing cannot happen while tethering is enabled
            if(!forwardingManager.isEnabled()) {
                Intent editRuleIntent = new Intent(view.getContext(), EditRuleActivity.class);
                editRuleIntent.putExtra("RuleModelLocation", getAdapterPosition());
                editRuleIntent.putExtra("RuleModelId", this.ruleId);
                view.getContext().startActivity(editRuleIntent);
            }
        }
    }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RuleListAdapter(List<RuleModel> ruleModels, ForwardingManager forwardingManager) {
            this.ruleModels = ruleModels;
            this.forwardingManager = forwardingManager;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RuleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rule_item_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            // ...

            ViewHolder vh = new ViewHolder(v, forwardingManager);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            RuleModel ruleModel = ruleModels.get(position);

            //TODO: potentially should add some validation :/ / exception handling
            holder.ruleId = ruleModel.getId();
            holder.ruleProtocolText.setText(ruleModel.protocolToString());

            holder.ruleNameText.setText(ruleModel.getName());
            holder.ruleFromPortText.setText(String.valueOf(ruleModel.getFromPort()));
            holder.ruleTargetPortText.setText(String.valueOf(ruleModel.getTarget().getPort()));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return ruleModels.size();
        }
}