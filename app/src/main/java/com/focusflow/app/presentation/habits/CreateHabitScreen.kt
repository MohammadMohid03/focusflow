package com.focusflow.app.presentation.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    viewModel: HabitsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Habit") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Select Color & Icon", style = MaterialTheme.typography.titleMedium)
            // Color picker and icon picker placeholders
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Color dots
            }

            Text("Frequency", style = MaterialTheme.typography.titleMedium)
            // Frequency chips placeholder
            
            Text("Goal Target", style = MaterialTheme.typography.titleMedium)
            // Target selector placeholder

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    // Save
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Save Habit")
            }
        }
    }
}
