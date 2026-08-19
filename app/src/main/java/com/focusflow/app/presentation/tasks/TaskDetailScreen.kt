package com.focusflow.app.presentation.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.domain.model.TaskPriority
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.theme.FocusFlowCorners

@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val task = uiState.tasks.firstOrNull { it.id == taskId }

    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember(task) { mutableStateOf(task?.title ?: "") }
    var editDescription by remember(task) { mutableStateOf(task?.description ?: "") }
    var editCategory by remember(task) { mutableStateOf(task?.category ?: TaskCategory.STUDY) }
    var editPriority by remember(task) { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    var editDuration by remember(task) { mutableStateOf(task?.estimatedDurationMinutes?.toString() ?: "45") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${task.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(task)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = FocusFlowCorners.Dialog,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CompactTopBar(
                title = if (isEditing) "Edit Task" else "Task Details",
                onBackClick = onNavigateBack,
                actions = {
                    if (task != null) {
                        if (isEditing) {
                            IconButton(onClick = { isEditing = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel Edit", tint = MaterialTheme.colorScheme.onSurface)
                            }
                            IconButton(
                                onClick = {
                                    val duration = editDuration.toIntOrNull() ?: 45
                                    val updated = task.copy(
                                        title = editTitle.trim().ifBlank { task.title },
                                        description = editDescription.trim(),
                                        category = editCategory,
                                        priority = editPriority,
                                        estimatedDurationMinutes = duration,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    viewModel.updateTask(updated)
                                    isEditing = false
                                }
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (task == null) {
                Box(modifier = Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("Task not found or was deleted.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (isEditing) {
                // EDIT MODE
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = FocusFlowCorners.Card,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("Task Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = FocusFlowCorners.Input,
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Description / Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = FocusFlowCorners.Input,
                            minLines = 3
                        )
                        OutlinedTextField(
                            value = editDuration,
                            onValueChange = { editDuration = it },
                            label = { Text("Duration (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = FocusFlowCorners.Input,
                            singleLine = true
                        )
                    }
                }

                Text("Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(TaskCategory.STUDY, TaskCategory.WORK, TaskCategory.PERSONAL, TaskCategory.HEALTH).forEach { cat ->
                        FilterChip(
                            selected = editCategory == cat,
                            onClick = { editCategory = cat },
                            label = { Text(cat.name) }
                        )
                    }
                }

                Text("Priority", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.entries.forEach { p ->
                        FilterChip(
                            selected = editPriority == p,
                            onClick = { editPriority = p },
                            label = { Text(p.name) }
                        )
                    }
                }

                Button(
                    onClick = {
                        val duration = editDuration.toIntOrNull() ?: 45
                        val updated = task.copy(
                            title = editTitle.trim().ifBlank { task.title },
                            description = editDescription.trim(),
                            category = editCategory,
                            priority = editPriority,
                            estimatedDurationMinutes = duration,
                            updatedAt = System.currentTimeMillis()
                        )
                        viewModel.updateTask(updated)
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = FocusFlowCorners.Button
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            } else {
                // VIEW MODE
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = FocusFlowCorners.Card,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (task.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = FocusFlowCorners.Chip,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Priority: ${task.priority.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = FocusFlowCorners.Chip,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = task.category.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            
                            Surface(
                                shape = FocusFlowCorners.Chip,
                                color = if (task.isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (task.isCompleted) "Completed" else "In Progress",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        viewModel.toggleTaskCompletion(task, !task.isCompleted)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = FocusFlowCorners.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (task.isCompleted) "Mark as Incomplete" else "Mark as Complete",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { isEditing = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = FocusFlowCorners.Button,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Task Details", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
