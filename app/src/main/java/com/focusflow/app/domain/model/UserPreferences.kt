package com.focusflow.app.domain.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultFocusDuration: Int = 25,
    val defaultBreakDuration: Int = 5,
    val notificationsEnabled: Boolean = true,
    val taskReminders: Boolean = true,
    val habitReminders: Boolean = true,
    val commitmentReminders: Boolean = true,
    val focusReminders: Boolean = true,
    val dailyPlanReminder: Boolean = true,
    val workingHoursStart: Int = 9, // hour of day
    val workingHoursEnd: Int = 17
)
