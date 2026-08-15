package com.focusflow.app.presentation.commitment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommitmentViewModel @Inject constructor(
    // private val createCommitmentUseCase: CreateCommitmentUseCase,
    // private val activateCommitmentUseCase: ActivateCommitmentUseCase,
    // private val cancelCommitmentUseCase: CancelCommitmentUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CommitmentUiState())
    val uiState: StateFlow<CommitmentUiState> = _uiState
    
    fun createCommitment() {}
    fun activateCommitment() {}
    fun cancelCommitment() {}
}

data class CommitmentUiState(
    val isLoading: Boolean = false
)
