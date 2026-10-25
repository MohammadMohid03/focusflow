package com.focusflow.app.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.*
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.FocusSessionRepository
import com.focusflow.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

data class FocusUiState(
    val currentTask: Task? = null,
    val sessionType: FocusSessionType? = FocusSessionType.CUSTOM,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val timeRemaining: Int = 25 * 60,
    val totalDuration: Int = 25 * 60,
    val customDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val isBreak: Boolean = false,
    val currentRound: Int = 1,
    val totalFocusToday: Int = 0,
    val focusStreak: Int = 0,
    val recentSessions: List<FocusSession> = emptyList(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FocusViewModel @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val authRepository: AuthRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val clock: MonotonicClock
) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()
    private var timerJob: Job? = null
    private var sessionStartTime = 0L
    private var activeUserId = ""
    private var accumulatedMillis = 0L
    private var resumedAt = 0L
    private var selectionChanged = false
    private var lastDefaultFocusDuration: Int? = null

    init {
        viewModelScope.launch {
            preferencesRepository.getUserPreferences().collect { preferences ->
                val updateDuration = !selectionChanged || (lastDefaultFocusDuration != null && lastDefaultFocusDuration != preferences.defaultFocusDuration)
                lastDefaultFocusDuration = preferences.defaultFocusDuration
                if (!_uiState.value.isRunning) {
                    _uiState.update { state -> state.copy(
                        breakDurationMinutes = preferences.defaultBreakDuration,
                        customDurationMinutes = if (updateDuration) preferences.defaultFocusDuration else state.customDurationMinutes,
                        sessionType = if (selectionChanged) state.sessionType else FocusSessionType.CUSTOM,
                        timeRemaining = if (updateDuration && state.sessionType == FocusSessionType.CUSTOM) preferences.defaultFocusDuration * 60 else state.timeRemaining,
                        totalDuration = if (updateDuration && state.sessionType == FocusSessionType.CUSTOM) preferences.defaultFocusDuration * 60 else state.totalDuration
                    ) }
                }
            }
        }
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { user ->
                combine(focusSessionRepository.getAllSessions(user?.id ?: ""), observeToday()) { sessions, today ->
                    val dates = sessions.filter { (it.actualDurationMinutes ?: 0) > 0 }
                        .map { java.time.Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate() }.toSet()
                    var date = if (today in dates) today else today.minusDays(1)
                    var streak = 0
                    while (date in dates) { streak++; date = date.minusDays(1) }
                    Triple(sessions, sessions.filter {
                        java.time.Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate() == today
                    }.sumOf { it.actualDurationMinutes ?: 0 }, streak)
                }
            }.catch { error -> _uiState.update { it.copy(error = error.message) } }
                .collect { (sessions, minutes, streak) ->
                    _uiState.update { it.copy(recentSessions = sessions, totalFocusToday = minutes, focusStreak = streak) }
                }
        }
    }

    fun setCustomDuration(minutes: Int) {
        if (_uiState.value.isRunning) return
        selectionChanged = true
        val duration = minutes.coerceIn(1, 240)
        _uiState.update { it.copy(customDurationMinutes = duration, sessionType = FocusSessionType.CUSTOM,
            totalDuration = duration * 60, timeRemaining = duration * 60) }
    }

    fun selectSessionType(type: FocusSessionType) {
        if (_uiState.value.isRunning) return
        selectionChanged = true
        val minutes = when (type) {
            FocusSessionType.POMODORO_50_10 -> 50
            FocusSessionType.CUSTOM -> _uiState.value.customDurationMinutes
            else -> 25
        }
        _uiState.update { it.copy(sessionType = type, timeRemaining = minutes * 60, totalDuration = minutes * 60) }
    }

    fun selectTask(task: Task?) { if (!_uiState.value.isRunning) _uiState.update { it.copy(currentTask = task) } }

    fun startSession() {
        if (_uiState.value.isRunning) return
        sessionStartTime = System.currentTimeMillis()
        activeUserId = authRepository.getCurrentUser()?.id ?: ""
        accumulatedMillis = 0
        resumedAt = clock.nowMillis()
        val duration = when (_uiState.value.sessionType) {
            FocusSessionType.POMODORO_50_10 -> 50
            FocusSessionType.POMODORO_25_5 -> 25
            else -> _uiState.value.customDurationMinutes
        } * 60
        _uiState.update { it.copy(isRunning = true, isPaused = false, isBreak = false,
            totalDuration = duration, timeRemaining = duration, error = null) }
        startTimer()
    }

    fun pauseSession() {
        if (!_uiState.value.isRunning || _uiState.value.isPaused) return
        tick()
        if (!_uiState.value.isRunning) return
        accumulatedMillis += (clock.nowMillis() - resumedAt).coerceAtLeast(0)
        _uiState.update { it.copy(isPaused = true) }
        timerJob?.cancel()
    }

    fun resumeSession() {
        if (!_uiState.value.isRunning || !_uiState.value.isPaused) return
        resumedAt = clock.nowMillis()
        _uiState.update { it.copy(isPaused = false) }
        startTimer()
    }

    private fun elapsedMillis(): Long = accumulatedMillis +
        if (_uiState.value.isPaused) 0 else (clock.nowMillis() - resumedAt).coerceAtLeast(0)

    fun endSession() {
        val state = _uiState.value
        if (!state.isRunning) return
        val elapsed = elapsedMillis().coerceAtMost(state.totalDuration * 1000L)
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = false, isBreak = false, timeRemaining = 0) }
        if (!state.isBreak) saveSession(state, elapsed)
    }

    private fun saveSession(state: FocusUiState, elapsed: Long) {
        val record = FocusSession(
            id = UUID.randomUUID().toString(), userId = activeUserId, taskId = state.currentTask?.id,
            startTime = sessionStartTime, endTime = System.currentTimeMillis(),
            plannedDurationMinutes = state.totalDuration / 60,
            actualDurationMinutes = (elapsed / 60000).toInt(),
            breakDurationMinutes = breakMinutes(state),
            sessionType = state.sessionType ?: FocusSessionType.CUSTOM,
            isCompleted = elapsed >= state.totalDuration * 1000L
        )
        viewModelScope.launch {
            withContext(NonCancellable) {
                try { focusSessionRepository.insertSession(record)
                } catch (e: Exception) { _uiState.update { it.copy(error = "Unable to save session: ${e.message}") } }
            }
        }
    }

    private fun breakMinutes(state: FocusUiState) = when (state.sessionType) {
        FocusSessionType.POMODORO_25_5 -> 5
        FocusSessionType.POMODORO_50_10 -> 10
        else -> state.breakDurationMinutes
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.isRunning) { delay(250); tick() }
        }
    }

    fun tick() {
        val state = _uiState.value
        if (!state.isRunning || state.isPaused) return
        val elapsed = elapsedMillis()
        val remaining = ((state.totalDuration * 1000L - elapsed).coerceAtLeast(0) + 999) / 1000
        if (remaining > 0) {
            _uiState.update { it.copy(timeRemaining = remaining.toInt()) }
        } else if (!state.isBreak) {
            saveSession(state, state.totalDuration * 1000L)
            accumulatedMillis = 0
            resumedAt = clock.nowMillis()
            val breakSeconds = breakMinutes(state) * 60
            _uiState.update { it.copy(isBreak = true, totalDuration = breakSeconds, timeRemaining = breakSeconds) }
        } else {
            endSession()
        }
    }
}
