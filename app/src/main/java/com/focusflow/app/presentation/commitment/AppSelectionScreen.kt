package com.focusflow.app.presentation.commitment

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
