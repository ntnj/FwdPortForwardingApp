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
package com.elixsr.portforwarder.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.elixsr.portforwarder.db.RuleContract.RuleEntry

/**
 * Created by Niall McShane on 07/03/2016.
 *
 *
 * Sourced from http://developer.android.com/training/basics/data-storage/databases.html#DbHelper
 */
class RuleDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL(DATABASE_ALTER_RULES_1)
        }
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "Rule.db"
        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val COMMA_SEP = ","
        private const val SQL_CREATE_ENTRIES = "CREATE TABLE " + RuleEntry.TABLE_NAME + " (" +
                RuleEntry.COLUMN_NAME_RULE_ID + " INTEGER PRIMARY KEY," +
                RuleEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_IS_TCP + INTEGER_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_IS_UDP + INTEGER_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_FROM_INTERFACE_NAME + TEXT_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_FROM_PORT + INTEGER_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_TARGET_IP_ADDRESS + TEXT_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_TARGET_PORT + INTEGER_TYPE + COMMA_SEP +
                RuleEntry.COLUMN_NAME_IS_ENABLED + INTEGER_TYPE +
                " )"
        private val DATABASE_ALTER_RULES_1 = String.format("ALTER TABLE %s ADD COLUMN %s int default 1;",
                RuleEntry.TABLE_NAME, RuleEntry.COLUMN_NAME_IS_ENABLED)

        @JvmStatic
        fun generateAllRowsSelection(): Array<String?> {
            return arrayOf(
                    RuleEntry.COLUMN_NAME_RULE_ID,
                    RuleEntry.COLUMN_NAME_NAME,
                    RuleEntry.COLUMN_NAME_IS_TCP,
                    RuleEntry.COLUMN_NAME_IS_UDP,
                    RuleEntry.COLUMN_NAME_FROM_INTERFACE_NAME,
                    RuleEntry.COLUMN_NAME_FROM_PORT + TEXT_TYPE,
                    RuleEntry.COLUMN_NAME_TARGET_IP_ADDRESS,
                    RuleEntry.COLUMN_NAME_TARGET_PORT,
                    RuleEntry.COLUMN_NAME_IS_ENABLED
            )
        }
    }
}