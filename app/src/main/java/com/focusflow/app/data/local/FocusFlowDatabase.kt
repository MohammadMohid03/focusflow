package com.focusflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.focusflow.app.data.local.dao.*
import com.focusflow.app.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        SubtaskEntity::class,
        GoalEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        FocusSessionEntity::class,
        CommitmentEntity::class,
        PlannerSessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FocusFlowDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun goalDao(): GoalDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun commitmentDao(): CommitmentDao
    abstract fun plannerSessionDao(): PlannerSessionDao
}
