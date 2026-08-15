package com.focusflow.app.presentation.commitment

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
