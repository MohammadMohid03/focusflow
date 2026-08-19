package com.focusflow.app.presentation.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.TaskFilter
import com.focusflow.app.domain.model.TaskSortOrder
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.components.EmptyStateView
import com.focusflow.app.presentation.components.ErrorView
import com.focusflow.app.presentation.components.LoadingView
import com.focusflow.app.presentation.components.TaskCard
import com.focusflow.app.presentation.theme.FocusFlowCorners

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel(),
    onNavigateToCreateTask: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CompactTopBar(
                    title = "Tasks",
                    actions = {
                        Box {
                            Surface(
                                onClick = { sortMenuExpanded = true },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.List,
                                        contentDescription = "Sort",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                TaskSortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = { Text(order.name.replace("_", " ")) },
                                        onClick = {
                                            viewModel.onSortOrderSelect(order)
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
                
                // Compact Search Bar
                Surface(
                    shape = FocusFlowCorners.Input,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { 
                                Text(
                                    "Search tasks...", 
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                ) 
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }
                
                // Filter Chips Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TaskFilter.entries.toTypedArray()) { filter ->
                        val isSelected = uiState.selectedFilter == filter
                        Surface(
                            onClick = { viewModel.onFilterSelect(filter) },
                            shape = FocusFlowCorners.Chip,
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) else null,
                            shadowElevation = if (isSelected) 1.dp else 0.dp
                        ) {
                            Text(
                                text = filter.name.replace("_", " "),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask,
                shape = FocusFlowCorners.FloatingActionButton,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 12.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingView()
                uiState.error != null -> ErrorView(message = uiState.error!!, onRetry = { /* reload */ })
                uiState.tasks.isEmpty() -> EmptyStateView(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = "No tasks found",
                    description = "Try changing your filters or add a new task."
                )

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.tasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onNavigateToTaskDetail(task.id) },
                                onComplete = { isCompleted -> viewModel.toggleTaskCompletion(task, isCompleted) },
                                onLongClick = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }
}
