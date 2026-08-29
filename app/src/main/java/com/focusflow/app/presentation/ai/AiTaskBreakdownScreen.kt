package com.focusflow.app.presentation.ai

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.repository.SuggestedSubtask
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.components.PrimaryButton
import com.focusflow.app.presentation.components.PriorityIndicator
import com.focusflow.app.presentation.theme.FocusFlowCorners
import com.focusflow.app.presentation.theme.pressSpring3D

@Composable
fun AiTaskBreakdownScreen(
    viewModel: AiViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CompactTopBar(
                title = "AI Task Deconstruction",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                Surface(
                    shape = FocusFlowCorners.Card,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Describe any complex exam, project, or goal. Groq AI will deconstruct it into structured, time-boxed milestones.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.breakdownPrompt,
                    onValueChange = viewModel::onBreakdownPromptChanged,
                    label = { Text("What is your goal or project?") },
                    placeholder = { Text("e.g. Master Organic Chemistry Reaction Mechanisms for Midterm") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = FocusFlowCorners.Input,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    ),
                    maxLines = 3
                )
            }

            item {
                PrimaryButton(
                    text = if (uiState.isGenerating) "Analyzing & Deconstructing..." else "Generate Action Plan",
                    onClick = { viewModel.generateTaskBreakdown() },
                    icon = Icons.Default.AutoAwesome,
                    enabled = !uiState.isGenerating && uiState.breakdownPrompt.isNotBlank()
                )
            }

            if (uiState.suggestedSubtasks.isNotEmpty()) {
                item {
                    Text(
                        text = "Suggested Action Milestones (${uiState.suggestedSubtasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(uiState.suggestedSubtasks) { subtask ->
                    SubtaskCard(subtask = subtask)
                }

                item {
                    if (uiState.isTaskSaved) {
                        Surface(
                            shape = FocusFlowCorners.Card,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Plan saved to your Tasks list!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        PrimaryButton(
                            text = "Save Action Plan to My Tasks",
                            onClick = { viewModel.saveGeneratedTasksAsParentTask(uiState.breakdownPrompt) },
                            icon = Icons.Default.Check
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubtaskCard(subtask: SuggestedSubtask) {
    Surface(
        shape = FocusFlowCorners.Card,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    PriorityIndicator(priority = subtask.priority)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = subtask.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${subtask.estimatedMinutes}m",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (subtask.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtask.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
