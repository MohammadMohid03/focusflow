package com.focusflow.app.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class AiChatViewModel @Inject constructor() : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                content = "Hello! I'm your FocusFlow AI study partner. I can help you break down complex topics, solve problems, summarize study notes, and schedule effective Pomodoro sessions. What are you studying today?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        val userMessage = ChatMessage(content = text, isUser = true)
        _messages.update { it + userMessage }
        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            delay(500)
            _isLoading.value = false
            
            val lower = text.lowercase()
            val aiResponse = when {
                lower.contains("calculus") || lower.contains("derivative") || lower.contains("integral") || lower.contains("math") -> {
                    "Here is a structured breakdown for Calculus:\n\n1. Identify the core rule (Power, Product, Chain, or Quotient rule).\n2. Differentiate step-by-step without skipping algebra.\n3. Verify critical points by checking where f'(x) = 0.\n\nWould you like me to walk through a specific equation?"
                }
                lower.contains("tip") || lower.contains("exam") || lower.contains("memoriz") -> {
                    "Top 3 Active Recall & Spaced Repetition Tips:\n\n• The Feynman Technique: Explain the concept in simple terms without looking at notes.\n• 25/5 Pomodoro Cycle: Focus deeply for 25 min, then take 5 min to test your memory.\n• Interleaved Practice: Alternate between related subjects to build flexible problem-solving skills."
                }
                lower.contains("plan") || lower.contains("schedule") || lower.contains("hour") -> {
                    "Here is an optimized 2-hour Study Plan:\n\n• Block 1 (45 min): Deep focus on your highest-priority or hardest topic.\n• Break (10 min): Step away, hydrate, and stretch.\n• Block 2 (40 min): Practice problems and application.\n• Review (25 min): Summarize key takeaways and log completed tasks."
                }
                lower.contains("summarize") || lower.contains("note") -> {
                    "Paste your notes or key terms here! I'll generate concise bullet points, key definitions, and 3 self-test flash questions."
                }
                else -> {
                    "Great question! To tackle this efficiently, let's break it into 2-3 focused milestones. What is the most critical concept or assignment you want to complete first?"
                }
            }
            
            _messages.update { it + ChatMessage(content = aiResponse, isUser = false) }
        }
    }
}
