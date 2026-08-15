package com.focusflow.app.presentation.commitment

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
