package com.focusflow.app.presentation.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NotificationsState(
    val master: Boolean = true,
    val tasks: Boolean = true,
    val habits: Boolean = true,
    val commitments: Boolean = true,
    val focus: Boolean = true,
    val dailyPlan: Boolean = true
)

data class SettingsUiState(
    val themeMode: String = "System Default",
    val notifications: NotificationsState = NotificationsState(),
    val focusDuration: Int = 25,
    val breakDuration: Int = 5,
    val workingHours: String = "09:00 - 17:00",
    val syncEnabled: Boolean = true,
    val userProfile: String = "User"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleMasterNotification(enabled: Boolean) {
        _uiState.update { it.copy(notifications = it.notifications.copy(master = enabled)) }
    }
    fun toggleTaskNotification(enabled: Boolean) {
        _uiState.update { it.copy(notifications = it.notifications.copy(tasks = enabled)) }
    }
    fun toggleHabitNotification(enabled: Boolean) {
        _uiState.update { it.copy(notifications = it.notifications.copy(habits = enabled)) }
    }
    fun toggleCommitmentNotification(enabled: Boolean) {
        _uiState.update { it.copy(notifications = it.notifications.copy(commitments = enabled)) }
    }
    fun toggleFocusNotification(enabled: Boolean) {
        _uiState.update { it.copy(notifications = it.notifications.copy(focus = enabled)) }
    }
    fun toggleDailyPlanNotification(enabled: Boolean) {
        _uiState.update { it.copy(notifications = it.notifications.copy(dailyPlan = enabled)) }
    }
    
    fun toggleSync(enabled: Boolean) {
        _uiState.update { it.copy(syncEnabled = enabled) }
    }

    fun updateTheme(theme: String) {
        _uiState.update { it.copy(themeMode = theme) }
    }

    fun updateFocusDuration(duration: Int) {
        _uiState.update { it.copy(focusDuration = duration) }
    }

    fun updateBreakDuration(duration: Int) {
        _uiState.update { it.copy(breakDuration = duration) }
    }

    fun exportData() {
        // Implement export logic
    }

    fun signOut() {
        // Implement sign out logic
    }

    fun deleteAccount() {
        // Implement delete account logic
    }
}
