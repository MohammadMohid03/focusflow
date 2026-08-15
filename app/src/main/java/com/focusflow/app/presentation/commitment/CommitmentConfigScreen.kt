package com.focusflow.app.presentation.commitment

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
