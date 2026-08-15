package com.focusflow.app.presentation.commitment

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
