package com.focusflow.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
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
    val aiRecommendation: String = "Choose one task and give it your full attention for your next focus session.",
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val commitmentRepository: CommitmentRepository,
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        fetchAiTip()
    }

    private fun loadData() {
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { user ->
                val userId = user?.id ?: ""
                combine(
                    combine(taskRepository.getAllTasks(userId), com.focusflow.app.domain.model.observeToday()) { tasks, today ->
                        val zone = java.time.ZoneId.systemDefault()
                        fun onToday(timestamp: Long?) = timestamp != null && java.time.Instant.ofEpochMilli(timestamp).atZone(zone).toLocalDate() == today
                        tasks.filter { onToday(it.dueDate) || onToday(it.completedAt) || (it.dueDate == null && !it.isCompleted) }
                    },
                    combine(focusSessionRepository.getAllSessions(userId), com.focusflow.app.domain.model.observeToday()) { sessions, today ->
                        sessions.filter { java.time.Instant.ofEpochMilli(it.startTime).atZone(java.time.ZoneId.systemDefault()).toLocalDate() == today }
                    },
                    commitmentRepository.getActiveCommitments(userId),
                    userPreferencesRepository.getUserName(),
                    habitRepository.getAllHabits(userId)
                ) { tasks, sessions, commitments, savedName, habits ->
                    val authName = user?.displayName?.takeIf { it.isNotBlank() }
                    val finalName = savedName.takeIf { it.isNotBlank() } ?: authName ?: "User"

                    val todayTasks = tasks.take(5)
                    val completed = tasks.count { it.isCompleted }
                    val focusMinutes = sessions.sumOf { it.actualDurationMinutes ?: 0 }
                    val activeComm = commitments.firstOrNull()

                    HomeUiState(
                        userName = finalName,
                        greeting = getGreeting(),
                        todayTasks = todayTasks,
                        completedCount = completed,
                        totalCount = tasks.size,
                        focusMinutesToday = focusMinutes,
                        currentStreak = habits.maxOfOrNull { it.currentStreak } ?: 0,
                        activeCommitment = activeComm,
                        aiRecommendation = _uiState.value.aiRecommendation,
                        isLoading = false
                    )
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error loading data") }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun fetchAiTip() {
        viewModelScope.launch {
            val tipResult = aiRepository.generateFocusTip()
            tipResult.onSuccess { tip ->
                _uiState.update { it.copy(aiRecommendation = tip) }
            }
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }
}
