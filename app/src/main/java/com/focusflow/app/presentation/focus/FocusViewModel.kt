package com.focusflow.app.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.FocusSession
import com.focusflow.app.domain.model.FocusSessionType
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.FocusSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FocusUiState(
    val currentTask: Task? = null,
    val sessionType: FocusSessionType? = null,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val timeRemaining: Int = 25 * 60,
    val totalDuration: Int = 25 * 60,
    val isBreak: Boolean = false,
    val currentRound: Int = 1,
    val totalFocusToday: Int = 0,
    val recentSessions: List<FocusSession> = emptyList()
)

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadData()
    }

    private fun loadData() {
        val uId = userId
        viewModelScope.launch {
            focusSessionRepository.getAllSessions(uId)
                .catch { /* Handle error */ }
                .collect { sessions ->
                    _uiState.update { it.copy(recentSessions = sessions) }
                }
        }
        viewModelScope.launch {
            focusSessionRepository.getTotalFocusMinutes(uId)
                .catch { /* Handle error */ }
                .collect { totalMinutes ->
                    _uiState.update { it.copy(totalFocusToday = totalMinutes.toInt()) }
                }
        }
    }

    private fun getDurationMinutes(type: FocusSessionType?): Int {
        return when (type) {
            FocusSessionType.POMODORO_50_10 -> 50
            FocusSessionType.CUSTOM -> 25
            FocusSessionType.POMODORO_25_5 -> 25
            null -> 25
            else -> 25
        }
    }

    fun selectSessionType(type: FocusSessionType) {
        val durationMinutes = getDurationMinutes(type)
        val durationSeconds = durationMinutes * 60
        _uiState.update { it.copy(sessionType = type, timeRemaining = durationSeconds, totalDuration = durationSeconds) }
    }

    fun selectTask(task: Task?) {
        _uiState.update { it.copy(currentTask = task) }
    }

    fun startSession() {
        val durationMinutes = getDurationMinutes(_uiState.value.sessionType)
        val durationSeconds = durationMinutes * 60
        sessionStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRunning = true, isPaused = false, timeRemaining = durationSeconds, totalDuration = durationSeconds) }
        startTimer()
    }

    fun pauseSession() {
        _uiState.update { it.copy(isPaused = true) }
        timerJob?.cancel()
    }

    fun resumeSession() {
        _uiState.update { it.copy(isPaused = false) }
        startTimer()
    }

    fun endSession() {
        timerJob?.cancel()
        val plannedMinutes = getDurationMinutes(_uiState.value.sessionType)
        val plannedSeconds = plannedMinutes * 60
        val durationMinutes = ((plannedSeconds - _uiState.value.timeRemaining) / 60).coerceAtLeast(1)
        _uiState.update { it.copy(isRunning = false, isPaused = false, timeRemaining = 0) }
        
        viewModelScope.launch {
            try {
                val session = FocusSession(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    taskId = _uiState.value.currentTask?.id,
                    startTime = sessionStartTime,
                    endTime = System.currentTimeMillis(),
                    plannedDurationMinutes = plannedMinutes,
                    actualDurationMinutes = durationMinutes,
                    sessionType = _uiState.value.sessionType ?: FocusSessionType.POMODORO_25_5,
                    isCompleted = true
                )
                focusSessionRepository.insertSession(session)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                tick()
            }
        }
    }

    fun tick() {
        if (_uiState.value.isRunning && !_uiState.value.isPaused && _uiState.value.timeRemaining > 0) {
            _uiState.update { it.copy(timeRemaining = it.timeRemaining - 1) }
        } else if (_uiState.value.timeRemaining <= 0) {
            endSession()
        }
    }
}
