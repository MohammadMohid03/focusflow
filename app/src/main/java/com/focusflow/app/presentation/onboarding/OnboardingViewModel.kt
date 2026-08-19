package com.focusflow.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _selectedGoal = MutableStateFlow<String?>(null)
    val selectedGoal: StateFlow<String?> = _selectedGoal.asStateFlow()

    private val _selectedAreas = MutableStateFlow<Set<String>>(emptySet())
    val selectedAreas: StateFlow<Set<String>> = _selectedAreas.asStateFlow()

    private val _selectedProductivityTime = MutableStateFlow<String?>(null)
    val selectedProductivityTime: StateFlow<String?> = _selectedProductivityTime.asStateFlow()

    fun setGoal(goal: String) {
        _selectedGoal.value = goal
    }

    fun toggleArea(area: String) {
        val current = _selectedAreas.value.toMutableSet()
        if (current.contains(area)) {
            current.remove(area)
        } else {
            current.add(area)
        }
        _selectedAreas.value = current
    }

    fun setProductivityTime(time: String) {
        _selectedProductivityTime.value = time
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
            _onboardingCompleted.value = true
        }
    }
}
