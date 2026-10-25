package com.focusflow.app.service

import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.focusflow.app.data.local.FocusFlowDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class UserDataManager @Inject constructor(
    private val database: FocusFlowDatabase,
    @ApplicationContext private val context: Context
) {
    private val ownedTables = listOf("tasks", "habits", "goals", "focus_sessions", "commitments", "planner_sessions")

    suspend fun deleteLocalData(userId: String) = withContext(Dispatchers.IO) {
        database.withTransaction {
            // Foreign keys cascade from tasks and habits to their child records.
            ownedTables.forEach { table ->
                database.openHelper.writableDatabase.execSQL("DELETE FROM `$table` WHERE userId = ?", arrayOf(userId))
            }
        }
    }

    suspend fun exportData(userId: String, destination: Uri) = withContext(Dispatchers.IO) {
        val result = database.withTransaction {
            val root = JSONObject().put("formatVersion", 1).put("exportedAt", java.time.Instant.now().toString())
            val selections = ownedTables.associateWith { "userId = ?" } + mapOf(
                "subtasks" to "taskId IN (SELECT id FROM tasks WHERE userId = ?)",
                "habit_completions" to "habitId IN (SELECT id FROM habits WHERE userId = ?)"
            )
            selections.forEach { (table, selection) ->
                val rows = JSONArray()
                database.query(SimpleSQLiteQuery("SELECT * FROM `$table` WHERE $selection", arrayOf(userId))).use { cursor ->
                    while (cursor.moveToNext()) {
                        val row = JSONObject()
                        cursor.columnNames.forEachIndexed { index, name ->
                            val value: Any = when (cursor.getType(index)) {
                                Cursor.FIELD_TYPE_NULL -> JSONObject.NULL
                                Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(index)
                                Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(index)
                                else -> cursor.getString(index)
                            }
                            row.put(name, value)
                        }
                        rows.put(row)
                    }
                }
                root.put(table, rows)
            }
            root.toString(2)
        }
        val stream = context.contentResolver.openOutputStream(destination, "wt")
            ?: error("Unable to open the selected file")
        stream.bufferedWriter().use { it.write(result) }
    }
}
