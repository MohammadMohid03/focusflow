package com.focusflow.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "User",
    val greeting: String = "Good Morning",
    val todayTasks: List<Task> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val focusMinutesToday: Int = 0,
    val currentStreak: Int = 0,
    val activeCommitment: Commitment? = null,
    val aiRecommendation: String = "You tend to be most productive in the morning. Schedule your hardest task then.",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val commitmentRepository: CommitmentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val userId = authRepository.getCurrentUser()?.id ?: ""
        val userName = authRepository.getCurrentUser()?.displayName?.ifEmpty { "User" } ?: "User"

        viewModelScope.launch {
            combine(
                taskRepository.getAllTasks(userId),
                focusSessionRepository.getAllSessions(userId),
                commitmentRepository.getActiveCommitments(userId)
            ) { tasks, sessions, commitments ->
                val todayTasks = tasks.take(5)
                val completed = todayTasks.count { it.isCompleted }
                val focusMinutes = sessions.sumOf { it.actualDurationMinutes ?: 0 }
                val activeComm = commitments.firstOrNull()

                HomeUiState(
                    userName = userName,
                    greeting = getGreeting(),
                    todayTasks = todayTasks,
                    completedCount = completed,
                    totalCount = todayTasks.size,
                    focusMinutesToday = focusMinutes,
                    currentStreak = 3,
                    activeCommitment = activeComm,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error loading data") }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun getGreeting(): String {
        return "Good Morning"
    }
}
