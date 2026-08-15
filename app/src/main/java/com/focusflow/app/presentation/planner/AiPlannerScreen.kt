package com.focusflow.app.presentation.planner

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
