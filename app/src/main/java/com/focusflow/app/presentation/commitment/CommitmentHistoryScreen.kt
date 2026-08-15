package com.focusflow.app.presentation.commitment

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
