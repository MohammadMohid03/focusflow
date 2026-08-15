package com.focusflow.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DEFAULT_FOCUS_DURATION = intPreferencesKey("default_focus_duration")
        val DEFAULT_BREAK_DURATION = intPreferencesKey("default_break_duration")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val TASK_REMINDERS = booleanPreferencesKey("task_reminders")
        val HABIT_REMINDERS = booleanPreferencesKey("habit_reminders")
        val COMMITMENT_REMINDERS = booleanPreferencesKey("commitment_reminders")
        val FOCUS_REMINDERS = booleanPreferencesKey("focus_reminders")
        val DAILY_PLAN_REMINDER = booleanPreferencesKey("daily_plan_reminder")
        val WORKING_HOURS_START = intPreferencesKey("working_hours_start")
        val WORKING_HOURS_END = intPreferencesKey("working_hours_end")
        val PRIMARY_GOAL = stringPreferencesKey("primary_goal")
        val PRODUCTIVITY_AREA = stringPreferencesKey("productivity_area")
        val PREFERRED_WORKING_TIME = stringPreferencesKey("preferred_working_time")
    }

    fun getThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            val modeString = preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name
            try { ThemeMode.valueOf(modeString) } catch (e: Exception) { ThemeMode.SYSTEM }
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }

    fun isOnboardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.data.map { preferences ->
            val modeString = preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try { ThemeMode.valueOf(modeString) } catch (e: Exception) { ThemeMode.SYSTEM }
            UserPreferences(
                themeMode = themeMode,
                defaultFocusDuration = preferences[DEFAULT_FOCUS_DURATION] ?: 25,
                defaultBreakDuration = preferences[DEFAULT_BREAK_DURATION] ?: 5,
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                taskReminders = preferences[TASK_REMINDERS] ?: true,
                habitReminders = preferences[HABIT_REMINDERS] ?: true,
                commitmentReminders = preferences[COMMITMENT_REMINDERS] ?: true,
                focusReminders = preferences[FOCUS_REMINDERS] ?: true,
                dailyPlanReminder = preferences[DAILY_PLAN_REMINDER] ?: true,
                workingHoursStart = preferences[WORKING_HOURS_START] ?: 9,
                workingHoursEnd = preferences[WORKING_HOURS_END] ?: 17
            )
        }
    }

    suspend fun <T> setUserPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}
