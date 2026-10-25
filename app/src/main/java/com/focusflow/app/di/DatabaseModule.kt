package com.focusflow.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.focusflow.app.data.local.FocusFlowDatabase
import com.focusflow.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // The exported v1 and v2 schemas are identical. Preserve all existing rows.
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) = Unit
    }

    @Provides
    @Singleton
    fun provideFocusFlowDatabase(@ApplicationContext context: Context): FocusFlowDatabase {
        return Room.databaseBuilder(
            context,
            FocusFlowDatabase::class.java,
            "focus_flow_database"
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    fun provideTaskDao(database: FocusFlowDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideSubtaskDao(database: FocusFlowDatabase): SubtaskDao = database.subtaskDao()

    @Provides
    fun provideGoalDao(database: FocusFlowDatabase): GoalDao = database.goalDao()

    @Provides
    fun provideHabitDao(database: FocusFlowDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHabitCompletionDao(database: FocusFlowDatabase): HabitCompletionDao = database.habitCompletionDao()

    @Provides
    fun provideFocusSessionDao(database: FocusFlowDatabase): FocusSessionDao = database.focusSessionDao()

    @Provides
    fun provideCommitmentDao(database: FocusFlowDatabase): CommitmentDao = database.commitmentDao()

    @Provides
    fun providePlannerSessionDao(database: FocusFlowDatabase): PlannerSessionDao = database.plannerSessionDao()
}
