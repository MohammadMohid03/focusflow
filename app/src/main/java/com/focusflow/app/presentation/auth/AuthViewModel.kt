package com.focusflow.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, pass: String) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()
        
        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter both email and password")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signInWithEmail(cleanEmail, cleanPass)
            result.onSuccess { user ->
                userPreferencesRepository.setOnboardingCompleted(true)
                val displayName = user.displayName.ifBlank {
                    cleanEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
                }
                userPreferencesRepository.setUserName(displayName)
                userPreferencesRepository.setUserEmail(cleanEmail)
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, user = user, error = null)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    error = error.localizedMessage ?: "Invalid email or password. Please try again."
                )
            }
        }
    }

    fun signUp(name: String, email: String, pass: String) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all required fields")
            return
        }

        if (cleanPass.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signUpWithEmail(cleanEmail, cleanPass, cleanName)
            
            result.onSuccess { user ->
                val finalName = cleanName.ifBlank {
                    cleanEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
                }
                userPreferencesRepository.setOnboardingCompleted(true)
                userPreferencesRepository.setUserName(finalName)
                userPreferencesRepository.setUserEmail(cleanEmail)
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, user = user, error = null)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    error = error.localizedMessage ?: "Failed to create account. Please check your credentials."
                )
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
            userPreferencesRepository.setUserName("Guest User")
            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter your email address")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.resetPassword(email.trim())
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Password reset link sent to $email")
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = error.localizedMessage ?: "Failed to send reset email")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val user: Any? = null
)
