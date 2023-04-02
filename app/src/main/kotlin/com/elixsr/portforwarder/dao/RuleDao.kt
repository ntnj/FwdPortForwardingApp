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
package com.elixsr.portforwarder.dao

import android.database.sqlite.SQLiteDatabase
import com.elixsr.portforwarder.db.RuleContract
import com.elixsr.portforwarder.db.RuleDbHelper
import com.elixsr.portforwarder.models.RuleModel
import com.elixsr.portforwarder.util.RuleHelper.cursorToRuleModel
import com.elixsr.portforwarder.util.RuleHelper.ruleModelToContentValues
import java.util.LinkedList

/**
 * The [RuleDao] class provides common functionality for Rule database access.
 *
 *
 * This class provides common database access functions.
 *
 * @author Niall McShane
 * @see [](http://developer.android.com/training/basics/data-storage/databases.html.ReadDbRow)
 */
class RuleDao {
    private lateinit var db: SQLiteDatabase
    private val ruleDbHelper: RuleDbHelper

    constructor(ruleDbHelper: RuleDbHelper) {
        this.ruleDbHelper = ruleDbHelper
    }

    constructor(sqLiteDatabase: SQLiteDatabase, ruleDbHelper: RuleDbHelper) {
        db = sqLiteDatabase
        this.ruleDbHelper = ruleDbHelper
    }

    /**
     * Inserts a valid rule into the SQLite database.
     *
     * @param ruleModel The source [RuleModel].
     * @return the id of the inserted rule.
     */
    fun insertRule(ruleModel: RuleModel?): Long {
        // Gets the data repository in write mode
        db = ruleDbHelper.writableDatabase
        val constantValues = ruleModelToContentValues(ruleModel!!)
        return db.insert(
                RuleContract.RuleEntry.TABLE_NAME,
                null,
                constantValues)
    }

    val allRuleModels: MutableList<RuleModel>
        /**
         * Finds and returns a list of all rules.
         *
         * @return a list of all [RuleModel] objects.
         */
        get() {
            val ruleModels: MutableList<RuleModel> = LinkedList()

            // Gets the data repository in read mode
            db = ruleDbHelper.readableDatabase

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            val projection = RuleDbHelper.generateAllRowsSelection()

            // How you want the results sorted in the resulting Cursor
            val sortOrder = RuleContract.RuleEntry.COLUMN_NAME_RULE_ID + " DESC"
            val cursor = db.query(
                    RuleContract.RuleEntry.TABLE_NAME,  // The table to query
                    projection,  // The columns to return
                    null,  // The columns for the WHERE clause
                    null,  // The values for the WHERE clause
                    null,  // don't group the rows
                    null,  // don't filter by row groups
                    sortOrder // The sort order
            )
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val ruleModel = cursorToRuleModel(cursor)
                ruleModels.add(ruleModel)
                cursor.moveToNext()
            }
            // make sure to close the cursor
            cursor.close()
            return ruleModels
        }
    val allEnabledRuleModels: List<RuleModel>
        get() {
            val enabledRuleModels: MutableList<RuleModel> = LinkedList()
            val ruleModels = allRuleModels
            for (ruleModel in ruleModels) {
                if (ruleModel.isEnabled) {
                    enabledRuleModels.add(ruleModel)
                }
            }
            return enabledRuleModels
        }
}