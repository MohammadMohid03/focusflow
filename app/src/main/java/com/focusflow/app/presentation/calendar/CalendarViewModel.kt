package com.focusflow.app.presentation.calendar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import java.time.LocalDate

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }
}

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val events: List<CalendarEvent> = emptyList()
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val date: LocalDate,
    val type: EventType
)

enum class EventType { TASK, FOCUS, HABIT }
