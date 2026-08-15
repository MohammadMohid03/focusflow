package com.focusflow.app.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AiChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                content = "Hi! I'm your AI study assistant. I can help you with homework, explain concepts, create study plans, and answer questions. How can I help you today?",
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
            delay(1500) // Fake network delay
            _isLoading.value = false
            
            val aiResponse = if (text.contains("calculus", ignoreCase = true)) {
                "Hey, I'm pretty comfortable with calculus. If you send me your problem, I can help you solve it step by step."
            } else {
                "I can definitely help with that! Let's break it down into smaller, manageable steps."
            }
            
            _messages.update { it + ChatMessage(content = aiResponse, isUser = false) }
        }
    }
}
