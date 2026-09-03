package com.focusflow.app.presentation.habits

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.presentation.theme.FocusFlowCorners
import com.focusflow.app.presentation.theme.glass3D

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: String,
    viewModel: HabitsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val habitFlow = remember(habitId) { viewModel.getHabitById(habitId) }
    val habit by habitFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Details", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            habit?.let { h ->
                Text(h.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Text(
                    h.description.ifBlank { "No description" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text("Consistency Heatmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .glass3D(shape = FocusFlowCorners.Card, elevation = 2.dp)
                        .padding(16.dp)
                ) {
                    Text("${h.frequency.name} completion active", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glass3D(shape = FocusFlowCorners.Card, elevation = 4.dp)
                        .padding(vertical = 14.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HabitStatItem3D("Current Streak", "${h.currentStreak} d")
                        HabitStatItem3D("Longest Streak", "${h.longestStreak} d")
                        HabitStatItem3D("Completions", "${h.totalCompletions}")
                    }
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
