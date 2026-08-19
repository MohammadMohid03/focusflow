package com.focusflow.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.FocusSessionRepository
import com.focusflow.app.domain.repository.HabitRepository
import com.focusflow.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val habitRepository: HabitRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val uId = userId
            combine(
                focusSessionRepository.getTotalFocusMinutes(uId),
                taskRepository.getAllTasks(uId),
                habitRepository.getAllHabits(uId)
            ) { totalFocusMinutes, tasks, habits ->
                val completedTasks = tasks.count { it.isCompleted }
                val totalTasks = tasks.size
                val completionRate = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
                val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0

                AnalyticsUiState(
                    totalFocusTime = totalFocusMinutes.toInt(),
                    tasksCompleted = completedTasks,
                    completionRate = completionRate,
                    currentStreak = maxStreak,
                    commitmentScore = 85
                )
            }.collect { newState ->
                _uiState.update { state ->
                    newState.copy(period = state.period)
                }
            }
        }
    }

    fun selectPeriod(period: AnalyticsPeriod) {
        _uiState.update { it.copy(period = period) }
    }
}

data class AnalyticsUiState(
    val period: AnalyticsPeriod = AnalyticsPeriod.WEEK,
    val totalFocusTime: Int = 0,
    val tasksCompleted: Int = 0,
    val completionRate: Float = 0f,
    val currentStreak: Int = 0,
    val commitmentScore: Int = 85
)

enum class AnalyticsPeriod { WEEK, MONTH, YEAR }
