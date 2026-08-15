package com.focusflow.app.presentation.habits

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: String,
    viewModel: HabitsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Habit Details") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Read Books", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Read 10 pages daily.", style = MaterialTheme.typography.bodyLarge)

            Divider()

            Text("Calendar", style = MaterialTheme.typography.titleMedium)
            // Heatmap calendar placeholder
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Heatmap drawing
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HabitStatItem("Current Streak", "3")
                HabitStatItem("Longest Streak", "12")
                HabitStatItem("Completions", "45")
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { /* Edit */ }, modifier = Modifier.weight(1f)) {
                    Text("Edit")
                }
                Button(
                    onClick = { 
                        viewModel.deleteHabit(habitId)
                        onNavigateBack()
                    }, 
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
