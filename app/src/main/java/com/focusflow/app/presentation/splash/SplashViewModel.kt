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
            delay(1500) // Splash animation display duration
            
            val hasCompletedOnboarding = userPreferencesDataStore.isOnboardingCompleted().first()
            val isUserLoggedIn = authRepository.isUserLoggedIn()

            _navigationTarget.value = when {
                !hasCompletedOnboarding -> NavigationTarget.Onboarding
                !isUserLoggedIn -> NavigationTarget.Login
                else -> NavigationTarget.Home
            }
        }
    }
}

sealed class NavigationTarget {
    object Onboarding : NavigationTarget()
    object Login : NavigationTarget()
    object Home : NavigationTarget()
}
