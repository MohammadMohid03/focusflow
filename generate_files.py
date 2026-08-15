import os
from pathlib import Path

base_dir = Path(r"C:\Users\moham\Desktop\focusflow")

files = {
    r"app\src\main\java\com\focusflow\app\presentation\commitment\CommitmentConfigScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentConfigScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Commitment") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Task: Complete Project")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateNext, modifier = Modifier.fillMaxWidth()) {
                Text("Next")
            }
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\AppSelectionScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Distractions") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Select apps to block")
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onNavigateNext, modifier = Modifier.fillMaxWidth()) {
                Text("Next")
            }
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\UnlockConditionScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockConditionScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Unlock Condition") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Select unlock condition")
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onNavigateNext, modifier = Modifier.fillMaxWidth()) {
                Text("Next")
            }
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\CommitmentReviewScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommitmentReviewScreen(
    onNavigateBack: () -> Unit,
    onActivate: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("FOCUS CONTRACT", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onActivate, modifier = Modifier.fillMaxWidth()) {
            Text("Activate Commitment")
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\CommitmentMissedScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommitmentMissedScreen(
    onStartRecovery: () -> Unit,
    onCancel: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("COMMITMENT MISSED", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onStartRecovery, modifier = Modifier.fillMaxWidth()) {
            Text("Start Recovery Plan")
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\CommitmentRecoveryScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommitmentRecoveryScreen(
    onStartFocus: () -> Unit,
    onCancel: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("RECOVERY PLAN")
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onStartFocus, modifier = Modifier.fillMaxWidth()) {
            Text("Start Recovery")
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\EarnBackScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EarnBackScreen(
    onComplete: () -> Unit,
    viewModel: CommitmentViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("ACCESS RECOVERY")
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
            Text("Continue Focus")
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\CommitmentHistoryScreen.kt": """package com.focusflow.app.presentation.commitment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommitmentHistoryScreen(
    viewModel: CommitmentViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Commitment History")
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\components\CommitmentActiveCard.kt": """package com.focusflow.app.presentation.commitment.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommitmentActiveCard(
    taskName: String,
    onContinue: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(taskName)
            Button(onClick = onContinue) {
                Text("Continue")
            }
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\commitment\CommitmentViewModel.kt": """package com.focusflow.app.presentation.commitment

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
""",
    r"app\src\main\java\com\focusflow\app\domain\usecase\commitment\CreateCommitmentUseCase.kt": """package com.focusflow.app.domain.usecase.commitment

import javax.inject.Inject

class CreateCommitmentUseCase @Inject constructor() {
    operator fun invoke() {
        // Create draft commitment
    }
}
""",
    r"app\src\main\java\com\focusflow\app\domain\usecase\commitment\ActivateCommitmentUseCase.kt": """package com.focusflow.app.domain.usecase.commitment

import javax.inject.Inject

class ActivateCommitmentUseCase @Inject constructor() {
    operator fun invoke() {
        // Change status DRAFT -> ACTIVE
    }
}
""",
    r"app\src\main\java\com\focusflow\app\domain\usecase\commitment\EvaluateCommitmentDeadlineUseCase.kt": """package com.focusflow.app.domain.usecase.commitment

import javax.inject.Inject

class EvaluateCommitmentDeadlineUseCase @Inject constructor() {
    operator fun invoke() {
        // Check if task completed
    }
}
""",
    r"app\src\main\java\com\focusflow\app\domain\usecase\commitment\CancelCommitmentUseCase.kt": """package com.focusflow.app.domain.usecase.commitment

import javax.inject.Inject

class CancelCommitmentUseCase @Inject constructor() {
    operator fun invoke() {
        // ACTIVE -> CANCELLED
    }
}
""",
    r"app\src\main\java\com\focusflow\app\domain\usecase\commitment\CalculateCommitmentScoreUseCase.kt": """package com.focusflow.app.domain.usecase.commitment

import javax.inject.Inject

class CalculateCommitmentScoreUseCase @Inject constructor() {
    operator fun invoke(): Int {
        return 100
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\ai\AiTaskBreakdownScreen.kt": """package com.focusflow.app.presentation.ai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AiTaskBreakdownScreen(
    viewModel: AiViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Describe your goal") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Generate Tasks")
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\planner\AiPlannerScreen.kt": """package com.focusflow.app.presentation.planner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.focusflow.app.presentation.ai.AiViewModel

@Composable
fun AiPlannerScreen(
    viewModel: AiViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Goal") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Generate Plan")
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\planner\PlannerScreen.kt": """package com.focusflow.app.presentation.planner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlannerScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text("AI Task Breakdown", modifier = Modifier.padding(16.dp))
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("AI Study Planner", modifier = Modifier.padding(16.dp))
        }
    }
}
""",
    r"app\src\main\java\com\focusflow\app\presentation\ai\AiViewModel.kt": """package com.focusflow.app.presentation.ai

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState
}

data class AiUiState(
    val isGenerating: Boolean = false
)
"""
}

for rel_path, content in files.items():
    p = base_dir / rel_path
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(content, encoding='utf-8')
    print(f"Created {p}")
