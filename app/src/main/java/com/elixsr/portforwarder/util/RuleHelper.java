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

package com.elixsr.portforwarder.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.net.InetSocketAddress;

import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.db.RuleContract;

import static com.facebook.GraphRequest.TAG;

/**
 * The {@link RuleModel} class provides static objects and methods related to rules.
 *
 * The class provides functions to convert {@link RuleModel} object to and from other common object
 * types.
 */
public class RuleHelper {

    public static final String RULE_MODEL_ID = "RuleModelId";

    /**
     * The minimum from port value.
     *
     * This is a result of not having root permissions.
     */
    public static final int MIN_PORT_VALUE = 1024;

    /**
     * The minimum target port value.
     */
    public static final int TARGET_MIN_PORT = 1;

    /**
     * The maximum from and target port value.
     */
    public static final int MAX_PORT_VALUE = 65535;

    /**
     * Convert a {@link RuleModel} object to a {@link ContentValues} object.
     * @param ruleModel The {@link RuleModel} object to be converted.
     * @return a {@link ContentValues} object based off the input {@link RuleModel} object.
     */
    public static ContentValues ruleModelToContentValues(RuleModel ruleModel){

        // Create a new map of values, where column names are the keys
        ContentValues contentValues = new ContentValues();
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_NAME, ruleModel.getName());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_IS_TCP, ruleModel.isTcp());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_IS_UDP, ruleModel.isUdp());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_FROM_INTERFACE_NAME, ruleModel.getFromInterfaceName());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_FROM_PORT, ruleModel.getFromPort());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_TARGET_IP_ADDRESS, ruleModel.getTargetIpAddress());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_TARGET_PORT, ruleModel.getTargetPort());
        contentValues.put(RuleContract.RuleEntry.COLUMN_NAME_IS_ENABLED, ruleModel.isEnabled());

        return contentValues;
    }

    /**
     * Convert a {@link Cursor} object to a {@link RuleModel} object.
     * @param cursor The {@link Cursor} object to be converted.
     * @return a {@link RuleModel} based off the input {@link Cursor}
     */
    public static RuleModel cursorToRuleModel(Cursor cursor){

        RuleModel ruleModel = new RuleModel();
        ruleModel.setId(cursor.getLong(0));
        ruleModel.setName(cursor.getString(1));

        //dirty conversion hack
        ruleModel.setIsTcp(cursor.getInt(2) != 0);
        ruleModel.setIsUdp(cursor.getInt(3) != 0);
        ruleModel.setFromInterfaceName(cursor.getString(4));
        ruleModel.setFromPort(cursor.getInt(5));
        ruleModel.setTarget(new InetSocketAddress(cursor.getString(6), cursor.getInt(7)));
        ruleModel.setEnabled(cursor.getInt(8) != 0);

        return ruleModel;
    }

    /**
     * Function to find the relevant Protocol based of a {@link RuleModel} object.
     * @param ruleModel The source {@link RuleModel} object.
     * @return A String describing the protocol. Can be; "TCP", "UDP" or "BOTH".
     */
    public static String getRuleProtocolFromModel(RuleModel ruleModel){

        String result = "";

        if(ruleModel.isTcp()){
            result = NetworkHelper.TCP;
        }

        if(ruleModel.isUdp()){
            result = NetworkHelper.UDP;
        }

        if(ruleModel.isTcp() && ruleModel.isUdp()){
            result = NetworkHelper.BOTH;
        }

        return result;
    }
}
