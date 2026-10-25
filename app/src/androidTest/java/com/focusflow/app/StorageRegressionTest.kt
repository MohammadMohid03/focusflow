package com.focusflow.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.focusflow.app.data.local.FocusFlowDatabase
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.DatabaseModule
import com.focusflow.app.domain.model.*
import com.focusflow.app.service.UserDataManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class StorageRegressionTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test fun exportAndDeleteRespectOwnerAndCascadeChildren() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, FocusFlowDatabase::class.java).build()
        val export = File(context.cacheDir, "storage-regression-export.json")
        try {
            for (owner in listOf("alice", "bob", "")) {
                val task = Task(id = "task-$owner", title = "Task", userId = owner)
                database.taskDao().insertTask(task.toEntity())
                database.subtaskDao().insertSubtask(Subtask(title = "Child").toEntity(task.id))
                val habit = Habit(id = "habit-$owner", name = "Read", userId = owner)
                database.habitDao().insert(habit.toEntity())
                database.habitCompletionDao().insertCompletion(HabitCompletion(habitId = habit.id, date = 0).toEntity())
                database.commitmentDao().insert(Commitment(taskId = task.id, deadline = 0,
                    estimatedDurationMinutes = 25, status = CommitmentStatus.MISSED, userId = owner).toEntity())
            }
            assertEquals(listOf("task-"), database.taskDao().getAllTasks("").first().map { it.id })
            assertEquals(listOf("task-alice"), database.taskDao().getAllTasks("alice").first().map { it.id })
            assertEquals(1, database.commitmentDao().getActiveCommitments("alice").first().size)
            val manager = UserDataManager(database, context)
            manager.exportData("alice", Uri.fromFile(export))
            val json = JSONObject(export.readText())
            assertEquals(1, json.getJSONArray("tasks").length())
            assertEquals("alice", json.getJSONArray("tasks").getJSONObject(0).getString("userId"))
            assertEquals(1, json.getJSONArray("subtasks").length())
            assertEquals(1, json.getJSONArray("habit_completions").length())
            manager.deleteLocalData("alice")
            assertTrue(database.taskDao().getAllTasks("alice").first().isEmpty())
            assertTrue(database.subtaskDao().getSubtasksForTask("task-alice").first().isEmpty())
            assertTrue(database.habitCompletionDao().getCompletionsForHabit("habit-alice").first().isEmpty())
            assertEquals(1, database.taskDao().getAllTasks("bob").first().size)
            assertEquals(1, database.taskDao().getAllTasks("").first().size)
        } finally { database.close(); export.delete() }
    }

    @Test fun versionOneUpgradePreservesExistingRows() = runBlocking {
        val name = "migration-regression.db"
        context.deleteDatabase(name)
        val schema = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().context.assets
            .open("com.focusflow.app.data.local.FocusFlowDatabase/1.json")
            .bufferedReader().use { JSONObject(it.readText()).getJSONObject("database") }
        val helper = FrameworkSQLiteOpenHelperFactory().create(SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(name).callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val entities = schema.getJSONArray("entities")
                    for (index in 0 until entities.length()) {
                        val entity = entities.getJSONObject(index)
                        val table = entity.getString("tableName")
                        db.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", table))
                        val indices = entity.getJSONArray("indices")
                        for (i in 0 until indices.length()) db.execSQL(indices.getJSONObject(i).getString("createSql").replace("\${TABLE_NAME}", table))
                        if (table == "tasks") {
                            val row = ContentValues()
                            val fields = entity.getJSONArray("fields")
                            for (i in 0 until fields.length()) {
                                val field = fields.getJSONObject(i)
                                val column = field.getString("columnName")
                                if (!field.getBoolean("notNull")) row.putNull(column)
                                else if (field.getString("affinity") == "TEXT") row.put(column, when (column) {
                                    "id" -> "existing-task"; "userId" -> "alice"; else -> "existing"
                                }) else row.put(column, 0)
                            }
                            db.insert(table, SQLiteDatabase.CONFLICT_ABORT, row)
                        }
                    }
                    val setup = schema.getJSONArray("setupQueries")
                    for (i in 0 until setup.length()) db.execSQL(setup.getString(i))
                }
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            }).build())
        helper.writableDatabase
        helper.close()
        val database = Room.databaseBuilder(context, FocusFlowDatabase::class.java, name)
            .addMigrations(DatabaseModule.MIGRATION_1_2).build()
        try {
            assertEquals("existing-task", database.taskDao().getAllTasks("alice").first().single().id)
        } finally { database.close(); context.deleteDatabase(name) }
    }
}
