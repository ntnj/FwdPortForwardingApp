package com.elixsr.portforwarder.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import com.elixsr.portforwarder.db.RuleContract;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Niall McShane on 07/03/2016.
 *
 * http://developer.android.com/training/basics/data-storage/databases.html#ReadDbRow
 */
public class RuleDao {

    private SQLiteDatabase db;
    private RuleDbHelper ruleDbHelper;

    public RuleDao(RuleDbHelper ruleDbHelper) {
        this.ruleDbHelper = ruleDbHelper;
    }

    public RuleDao(SQLiteDatabase sqLiteDatabase, RuleDbHelper ruleDbHelper) {
        this.db = sqLiteDatabase;
        this.ruleDbHelper = ruleDbHelper;
    }

    public long insertRule(RuleModel ruleModel){
        // Gets the data repository in write mode
        this.db = ruleDbHelper.getWritableDatabase();

        ContentValues constantValues = RuleHelper.ruleModelToContentValues(ruleModel);

        long newRowId = db.insert(
                RuleContract.RuleEntry.TABLE_NAME,
                null,
                constantValues);

        return newRowId;
    }

    public List<RuleModel> getAllRuleModels(){

        List<RuleModel> ruleModels = new LinkedList<RuleModel>();

        // Gets the data repository in read mode
        this.db = ruleDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = RuleDbHelper.generateAllRowsSelection();

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + " DESC";

        Cursor cursor = db.query(
                RuleContract.RuleEntry.TABLE_NAME,          // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortOrder                                   // The sort order
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RuleModel ruleModel = RuleHelper.cursorToRuleModel(cursor);
            ruleModels.add(ruleModel);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return ruleModels;
    }
}
