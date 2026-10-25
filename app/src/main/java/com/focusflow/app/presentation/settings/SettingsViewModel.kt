package com.focusflow.app.presentation.settings

import android.net.Uri
import com.focusflow.app.service.UserDataManager
import com.focusflow.app.domain.model.UserPreferences
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.CancellationException
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
    val userProfile: String = "User",
    val userEmail: String = "",
    val hasUsagePermission: Boolean = true,
    val hasOverlayPermission: Boolean = true,
    val message: String? = null,
    val isBusy: Boolean = false,
    val isSignedOut: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val appRestrictionManager: AppRestrictionManager,
    private val userDataManager: UserDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
        checkPermissions()
    }

    fun checkPermissions() {
        viewModelScope.launch {
            val hasUsage = appRestrictionManager.hasUsagePermission()
            val hasOverlay = appRestrictionManager.hasOverlayPermission()
            _uiState.update { 
                it.copy(
                    hasUsagePermission = hasUsage,
                    hasOverlayPermission = hasOverlay
                ) 
            }
        }
    }

    fun requestUsagePermission() {
        viewModelScope.launch {
            appRestrictionManager.requestPermissionSetup()
        }
    }

    fun requestOverlayPermission() {
        viewModelScope.launch {
            appRestrictionManager.requestOverlayPermission()
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.getUserPreferences()
                .catch { error -> _uiState.update { it.copy(message = "Unable to load settings: ${error.message}") } }
                .collect { preferences ->
                    _uiState.update { it.copy(
                        themeMode = preferences.themeMode.name,
                        focusDuration = preferences.defaultFocusDuration,
                        breakDuration = preferences.defaultBreakDuration,
                        notifications = NotificationsState(
                            preferences.notificationsEnabled, preferences.taskReminders,
                            preferences.habitReminders, preferences.commitmentReminders,
                            preferences.focusReminders, preferences.dailyPlanReminder
                        )
                    ) }
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

    private fun updatePreferences(update: (UserPreferences) -> UserPreferences) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.updateUserPreferences(update)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Unable to save settings: ${e.message}") }
            }
        }
    }

    fun toggleMasterNotification(enabled: Boolean) = updatePreferences { it.copy(notificationsEnabled = enabled) }
    fun toggleTaskNotification(enabled: Boolean) = updatePreferences { it.copy(taskReminders = enabled) }
    fun toggleHabitNotification(enabled: Boolean) = updatePreferences { it.copy(habitReminders = enabled) }
    fun toggleCommitmentNotification(enabled: Boolean) = updatePreferences { it.copy(commitmentReminders = enabled) }
    fun toggleFocusNotification(enabled: Boolean) = updatePreferences { it.copy(focusReminders = enabled) }
    fun toggleDailyPlanNotification(enabled: Boolean) = updatePreferences { it.copy(dailyPlanReminder = enabled) }

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

    fun updateFocusDuration(duration: Int) = updatePreferences { it.copy(defaultFocusDuration = duration.coerceIn(1, 240)) }
    fun updateBreakDuration(duration: Int) = updatePreferences { it.copy(defaultBreakDuration = duration.coerceIn(1, 60)) }

    fun exportData(destination: Uri) {
        if (_uiState.value.isBusy) return
        val userId = authRepository.getCurrentUser()?.id ?: ""
        _uiState.update { it.copy(isBusy = true) }
        viewModelScope.launch {
            try {
                userDataManager.exportData(userId, destination)
                _uiState.update { it.copy(message = "Data exported successfully") }
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Export failed: ${e.message}") }
            } finally { _uiState.update { it.copy(isBusy = false) } }
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                appRestrictionManager.disableRestriction(emptyList())
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
        if (_uiState.value.isBusy) return
        val userId = authRepository.getCurrentUser()?.id ?: ""
        _uiState.update { it.copy(isBusy = true) }
        viewModelScope.launch {
            try {
                // Firebase deletion can finish after a screen is closed; always finish local cleanup too.
                kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                    authRepository.deleteAccount().getOrThrow()
                    userDataManager.deleteLocalData(userId)
                    authRepository.signOut()
                    appRestrictionManager.disableRestriction(emptyList())
                    userPreferencesRepository.setUserName("")
                    userPreferencesRepository.setUserEmail("")
                }
                _uiState.update { it.copy(isSignedOut = true) }
                onComplete()
            } catch (e: CancellationException) { throw e
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                _uiState.update { it.copy(message = "Please sign out and sign in again before deleting your account.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Account deletion failed: ${e.message}") }
            } finally { _uiState.update { it.copy(isBusy = false) } }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
