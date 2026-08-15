package com.focusflow.app.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.FocusSessionType
import com.focusflow.app.domain.model.FocusSession

@HiltViewModel
class FocusViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    fun selectSessionType(type: FocusSessionType) {
        _uiState.update { it.copy(sessionType = type) }
    }

    fun selectTask(task: Task?) {
        _uiState.update { it.copy(currentTask = task) }
    }

    fun startSession() {
        _uiState.update { it.copy(isRunning = true, isPaused = false, timeRemaining = 25 * 60) }
    }

    fun pauseSession() {
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeSession() {
        _uiState.update { it.copy(isPaused = false) }
    }

    fun endSession() {
        _uiState.update { it.copy(isRunning = false, isPaused = false, timeRemaining = 0) }
    }
    
    fun tick() {
        if (_uiState.value.isRunning && !_uiState.value.isPaused && _uiState.value.timeRemaining > 0) {
            _uiState.update { it.copy(timeRemaining = it.timeRemaining - 1) }
        }
    }
}

data class FocusUiState(
    val currentTask: Task? = null,
    val sessionType: FocusSessionType? = null,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val timeRemaining: Int = 0,
    val isBreak: Boolean = false,
    val currentRound: Int = 1,
    val totalFocusToday: Int = 0,
    val recentSessions: List<FocusSession> = emptyList()
)
