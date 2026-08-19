package com.focusflow.app.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.Habit
import com.focusflow.app.domain.model.HabitCompletion
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitsUiState(
    val todayHabits: List<Habit> = emptyList(),
    val allHabits: List<Habit> = emptyList(),
    val completions: List<HabitCompletion> = emptyList()
)

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            habitRepository.getAllHabits(userId)
                .catch { /* Handle error */ }
                .collect { habits ->
                    _uiState.update { it.copy(allHabits = habits) }
                }
        }
        viewModelScope.launch {
            habitRepository.getHabitsForToday(userId)
                .catch { /* Handle error */ }
                .collect { todayHabits ->
                    _uiState.update { it.copy(todayHabits = todayHabits) }
                }
        }
    }

    fun completeHabit(habitId: String) {
        viewModelScope.launch {
            try {
                habitRepository.completeHabit(habitId, System.currentTimeMillis())
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun createHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                habitRepository.insertHabit(habit.copy(userId = userId))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            try {
                habitRepository.deleteHabit(habitId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
