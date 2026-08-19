package com.focusflow.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.data.local.datastore.UserPreferencesDataStore
import com.focusflow.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<NavigationTarget?>(null)
    val navigationTarget: StateFlow<NavigationTarget?> = _navigationTarget.asStateFlow()

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            delay(1200) // Splash animation duration
            
            val hasCompletedOnboarding = userPreferencesDataStore.isOnboardingCompleted().first()
            val hasLocalUser = userPreferencesDataStore.getUserName().first().isNotBlank()
            val isUserLoggedIn = authRepository.isUserLoggedIn() || hasLocalUser

            _navigationTarget.value = when {
                hasCompletedOnboarding && isUserLoggedIn -> NavigationTarget.Home
                hasCompletedOnboarding -> NavigationTarget.Login
                isUserLoggedIn -> NavigationTarget.Home
                else -> NavigationTarget.Onboarding
            }
        }
    }
}

sealed class NavigationTarget {
    object Onboarding : NavigationTarget()
    object Login : NavigationTarget()
    object Home : NavigationTarget()
}
