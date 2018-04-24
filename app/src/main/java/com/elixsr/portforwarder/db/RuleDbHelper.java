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

package com.elixsr.portforwarder.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Niall McShane on 07/03/2016.
 * <p>
 * Sourced from http://developer.android.com/training/basics/data-storage/databases.html#DbHelper
 */
public class RuleDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
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
                    RuleContract.RuleEntry.COLUMN_NAME_TARGET_PORT + INTEGER_TYPE + COMMA_SEP +
                    RuleContract.RuleEntry.COLUMN_NAME_IS_ENABLED + INTEGER_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RuleContract.RuleEntry.TABLE_NAME;

    private static final String DATABASE_ALTER_RULES_1 = String.format("ALTER TABLE %s ADD COLUMN %s int default 1;",
            RuleContract.RuleEntry.TABLE_NAME, RuleContract.RuleEntry.COLUMN_NAME_IS_ENABLED);

    public RuleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 3) {
            db.execSQL(DATABASE_ALTER_RULES_1);
        }

    }

    public static String[] generateAllRowsSelection() {
        String[] projection = {
                RuleContract.RuleEntry.COLUMN_NAME_RULE_ID,
                RuleContract.RuleEntry.COLUMN_NAME_NAME,
                RuleContract.RuleEntry.COLUMN_NAME_IS_TCP,
                RuleContract.RuleEntry.COLUMN_NAME_IS_UDP,
                RuleContract.RuleEntry.COLUMN_NAME_FROM_INTERFACE_NAME,
                RuleContract.RuleEntry.COLUMN_NAME_FROM_PORT + TEXT_TYPE,
                RuleContract.RuleEntry.COLUMN_NAME_TARGET_IP_ADDRESS,
                RuleContract.RuleEntry.COLUMN_NAME_TARGET_PORT,
                RuleContract.RuleEntry.COLUMN_NAME_IS_ENABLED
        };

        return projection;
    }
}