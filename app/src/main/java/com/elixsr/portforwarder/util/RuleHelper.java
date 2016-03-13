package com.elixsr.portforwarder.util;

import android.content.ContentValues;
import android.database.Cursor;

import java.net.InetSocketAddress;

import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.db.RuleContract;

/**
 * Created by Niall McShane on 07/03/2016.
 */
public class RuleHelper {

    public static final String RULE_MODEL_ID = "RuleModelId";

    public static final int MIN_PORT_VALUE = 1024;
    public static final int TARGET_MIN_PORT = 1;
    public static final int MAX_PORT_VALUE = 65535;

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

        return contentValues;
    }

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

        return ruleModel;
    }

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
