package com.focusflow.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val userProfile: String = "User",
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.getThemeMode()
                .catch { /* Handle error */ }
                .collect { themeMode ->
                    _uiState.update { it.copy(themeMode = themeMode.name) }
                }
        }
        val user = authRepository.getCurrentUser()
        if (user != null) {
            _uiState.update { it.copy(userProfile = user.displayName.ifEmpty { "User" }) }
        }
    }

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
        viewModelScope.launch {
            try {
                val mode = ThemeMode.valueOf(theme)
                userPreferencesRepository.setThemeMode(mode)
                _uiState.update { it.copy(themeMode = theme) }
            } catch (e: Exception) {
                // Fallback or error handling
            }
        }
    }

    fun updateFocusDuration(duration: Int) {
        _uiState.update { it.copy(focusDuration = duration) }
    }

    fun updateBreakDuration(duration: Int) {
        _uiState.update { it.copy(breakDuration = duration) }
    }

    fun exportData() {
        // TODO: Implement export logic
        _uiState.update { it.copy(message = "Data exported successfully") }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error signing out") }
            }
        }
    }

    fun deleteAccount() {
        // TODO: Implement delete account logic
        _uiState.update { it.copy(message = "Account deletion not supported yet") }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
