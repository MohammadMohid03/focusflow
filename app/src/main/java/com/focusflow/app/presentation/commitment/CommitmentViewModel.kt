package com.focusflow.app.presentation.commitment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.CommitmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommitmentUiState(
    val isLoading: Boolean = false,
    val activeCommitments: List<Commitment> = emptyList(),
    val allCommitments: List<Commitment> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CommitmentViewModel @Inject constructor(
    private val commitmentRepository: CommitmentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CommitmentUiState())
    val uiState: StateFlow<CommitmentUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadCommitments()
    }

    private fun loadCommitments() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            commitmentRepository.getActiveCommitments(userId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { commitments ->
                    _uiState.update { it.copy(activeCommitments = commitments, isLoading = false) }
                }
        }
        viewModelScope.launch {
            commitmentRepository.getAllCommitments(userId)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { commitments ->
                    _uiState.update { it.copy(allCommitments = commitments) }
                }
        }
    }

    fun createCommitment(commitment: Commitment) {
        viewModelScope.launch {
            try {
                commitmentRepository.insertCommitment(commitment.copy(userId = userId))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun activateCommitment(commitment: Commitment) {
        viewModelScope.launch {
            try {
                // Assuming updating an existing commitment to activate it
                commitmentRepository.updateCommitment(commitment)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun cancelCommitment(commitmentId: String) {
        viewModelScope.launch {
            try {
                commitmentRepository.deleteCommitment(commitmentId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
