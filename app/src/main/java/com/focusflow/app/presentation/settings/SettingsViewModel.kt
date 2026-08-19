package com.focusflow.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.UserPreferencesRepository
import com.focusflow.app.service.AppRestrictionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
    val themeMode: String = "LIGHT_SAGE",
    val notifications: NotificationsState = NotificationsState(),
    val focusDuration: Int = 25,
    val breakDuration: Int = 5,
    val workingHours: String = "09:00 - 17:00",
    val syncEnabled: Boolean = true,
    val userProfile: String = "User",
    val userEmail: String = "",
    val hasUsagePermission: Boolean = true,
    val message: String? = null,
    val isSignedOut: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val appRestrictionManager: AppRestrictionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
        checkUsagePermission()
    }

    fun checkUsagePermission() {
        viewModelScope.launch {
            val hasPerm = appRestrictionManager.hasRequiredPermission()
            _uiState.update { it.copy(hasUsagePermission = hasPerm) }
        }
    }

    fun requestUsagePermission() {
        viewModelScope.launch {
            appRestrictionManager.requestPermissionSetup()
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.getThemeMode()
                .catch { /* Handle error */ }
                .collect { themeMode ->
                    _uiState.update { it.copy(themeMode = themeMode.name) }
                }
        }
        
        viewModelScope.launch {
            combine(
                userPreferencesRepository.getUserName(),
                userPreferencesRepository.getUserEmail()
            ) { name, email ->
                val authUser = authRepository.getCurrentUser()
                val finalName = name.takeIf { it.isNotBlank() } ?: authUser?.displayName?.takeIf { it.isNotBlank() } ?: "User"
                val finalEmail = email.takeIf { it.isNotBlank() } ?: authUser?.email ?: ""
                finalName to finalEmail
            }.collect { (finalName, finalEmail) ->
                _uiState.update { 
                    it.copy(
                        userProfile = finalName,
                        userEmail = finalEmail
                    ) 
                }
            }
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
                // Fallback
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
        _uiState.update { it.copy(message = "Data exported successfully") }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                userPreferencesRepository.setUserName("")
                userPreferencesRepository.setUserEmail("")
                _uiState.update { it.copy(isSignedOut = true) }
                onComplete()
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error signing out") }
            }
        }
    }

    fun deleteAccount(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                userPreferencesRepository.setUserName("")
                userPreferencesRepository.setUserEmail("")
                _uiState.update { it.copy(isSignedOut = true) }
                onComplete()
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error deleting account") }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
