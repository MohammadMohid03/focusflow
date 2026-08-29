package com.focusflow.app.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
class AiChatViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                content = "Hello! I'm your FocusFlow AI study mentor powered by Groq. I can help you solve complex problems, break down coursework, plan study blocks, and test your active recall. What are you working on today?",
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
            val history = _messages.value.map { it.content to it.isUser }
            val result = aiRepository.sendMessage(history, text)
            
            _isLoading.value = false
            val aiContent = result.getOrDefault("I'm here to help! Could you provide a bit more detail on what you'd like to work through?")
            _messages.update { it + ChatMessage(content = aiContent, isUser = false) }
        }
    }
}
