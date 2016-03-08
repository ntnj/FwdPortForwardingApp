package com.elixsr.portforwarder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Niall McShane on 07/03/2016.
 *
 * Sourced from http://developer.android.com/training/basics/data-storage/databases.html#DbHelper
 */
public class RuleDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Rule.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RuleContract.RuleEntry.TABLE_NAME + " (" +
                    RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + " INTEGER PRIMARY KEY," +
                    RuleContract.RuleEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_IS_TCP + INTEGER_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_IS_UDP + INTEGER_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_FROM_INTERFACE_NAME + TEXT_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_FROM_PORT + INTEGER_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_TARGET_IP_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_TARGET_PORT + INTEGER_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RuleContract.RuleEntry.TABLE_NAME;

    public RuleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static String[] generateAllRowsSelection(){
        String[] projection = {
            RuleContract.RuleEntry.COLUMN_NAME_RULE_ID,
            RuleContract.RuleEntry.COLUMN_NAME_NAME,
            RuleContract.RuleEntry.COLUMN_NAME_IS_TCP,
            RuleContract.RuleEntry.COLUMN_NAME_IS_UDP,
            RuleContract.RuleEntry.COLUMN_NAME_FROM_INTERFACE_NAME,
            RuleContract.RuleEntry.COLUMN_NAME_FROM_PORT + TEXT_TYPE,
            RuleContract.RuleEntry.COLUMN_NAME_TARGET_IP_ADDRESS,
            RuleContract.RuleEntry.COLUMN_NAME_TARGET_PORT
        };

        return projection;
    }
}