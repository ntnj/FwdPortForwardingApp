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

package com.elixsr.portforwarder.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.elixsr.portforwarder.forwarding.ForwardingManager;
import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.ui.rules.EditRuleActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Created by Niall McShane on 01/03/2016.
 */
public class RuleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RuleListAdapter";
    private List<RuleModel> ruleModels;
    private List<ListItem> listItems;
    private ForwardingManager forwardingManager;

    protected static final int AD_VIEW = 1;
    protected static final int RULE_VIEW = 0;


    public static class AdViewHolder extends RecyclerView.ViewHolder {

        public static final String DARK_AD_ID = "ca-app-pub-9546697987163387/1828769767";
        public static final String LIGHT_AD_ID = "ca-app-pub-9546697987163387/1828769767";
        public static final String PREF_DARK_THEME = "pref_dark_theme";

        public AdViewHolder(View v) {
            super(v);

            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            AdView adView = new AdView(v.getContext());
            adView.setAdSize(AdSize.SMART_BANNER);

            // Load ad type based on theme - dark or light
            if (PreferenceManager.getDefaultSharedPreferences(v.getContext())
                    .getBoolean(PREF_DARK_THEME, false)) {
                adView.setAdUnitId(DARK_AD_ID);
            } else {
                adView.setAdUnitId(LIGHT_AD_ID);
            }
            ((LinearLayout) v).addView(adView, 1);
            adView.loadAd(request);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RuleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView ruleProtocolText;
        public TextView ruleNameText;
        public TextView ruleFromPortText;
        public TextView ruleTargetPortText;
        private ForwardingManager forwardingManager;
        private long ruleId;


        public RuleViewHolder(View v, ForwardingManager forwardingManager) {
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
        public void onClick(View view) {

            //ensure that editing cannot happen while tethering is enabled
            if (!forwardingManager.isEnabled()) {
                Intent editRuleIntent = new Intent(view.getContext(), EditRuleActivity.class);
                editRuleIntent.putExtra("RuleModelLocation", getAdapterPosition());
                editRuleIntent.putExtra("RuleModelId", this.ruleId);
                view.getContext().startActivity(editRuleIntent);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RuleListAdapter(List<RuleModel> ruleModels, ForwardingManager forwardingManager, Context context) {
        this.ruleModels = ruleModels;
        this.forwardingManager = forwardingManager;
        this.listItems = new ArrayList<>();

        for (RuleModel rule : ruleModels) {
            ListItem<RuleModel> ruleListItem = new ListItem<>(RULE_VIEW);
            ruleListItem.setPayload(rule);
            this.listItems.add(ruleListItem);
        }

        if (!(PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("disable_ads_key", false))) {
            if (ruleModels.size() > 3) {
                this.listItems.add(3, new ListItem(AD_VIEW));
            } else {
                this.listItems.add(new ListItem(AD_VIEW));
            }
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rule_item_view, parent, false);
        View adView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advertisement_item, parent, false);

        switch (viewType) {
            case 0:
                return new RuleViewHolder(view, forwardingManager);
            case 1:
                return new AdViewHolder(adView);
        }

        return new RuleViewHolder(view, forwardingManager);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        switch (holder.getItemViewType()) {
            case RULE_VIEW:
                RuleViewHolder ruleHolder = (RuleViewHolder) holder;
                RuleModel ruleModel = (RuleModel) listItems.get(position).getPayload();

                //TODO: potentially should add some validation :/ / exception handling
                ruleHolder.ruleId = ruleModel.getId();
                ruleHolder.ruleProtocolText.setText(ruleModel.protocolToString());

                if (!ruleModel.isEnabled()) {
                    ruleHolder.ruleNameText.setAlpha(0.4f);
                    ruleHolder.ruleProtocolText.setBackgroundResource(R.drawable.bg_grey);
                } else {
                    ruleHolder.ruleNameText.setAlpha(1f);
                    ruleHolder.ruleProtocolText.setBackgroundResource(R.drawable.bg_red);
                }
                ruleHolder.ruleNameText.setText(ruleModel.getName());
                ruleHolder.ruleFromPortText.setText(String.valueOf(ruleModel.getFromPort()));
                ruleHolder.ruleTargetPortText.setText(String.valueOf(ruleModel.getTarget().getPort()));
                break;
            case AD_VIEW:
                AdViewHolder adHolder = (AdViewHolder) holder;
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return this.listItems.get(position).getViewType();
    }

    // Return the size of your dataset (invoked by the layout manager)
    // Total amount of rules + 1 for an advertisement
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private class ListItem<T> {
        private int viewType;
        private T payload;

        public ListItem(int viewType) {
            this.viewType = viewType;
        }

        public void setPayload(T payload) {
            this.payload = payload;
        }

        public T getPayload() {
            return payload;
        }

        public int getViewType() {
            return viewType;
        }
    }
}