package com.focusflow.app.presentation.commitment.components

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
