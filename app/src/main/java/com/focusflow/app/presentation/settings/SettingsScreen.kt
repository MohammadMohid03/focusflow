package com.focusflow.app.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.theme.FocusFlowCorners

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("SYSTEM", "LIGHT", "DARK").forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateTheme(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (mode) {
                                    "SYSTEM" -> "System Default"
                                    "LIGHT" -> "Light (Recommended)"
                                    else -> "Dark"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (uiState.themeMode.equals(mode, ignoreCase = true)) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
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
                title = "Settings",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Appearance Section
            item {
                SettingsSection("Appearance") {
                    SettingsRow(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = when (uiState.themeMode) {
                            "LIGHT" -> "Light"
                            "DARK" -> "Dark"
                            else -> "System Default"
                        },
                        onClick = { showThemeDialog = true }
                    )
                }
            }

            // Notifications Section
            item {
                SettingsSection("Notifications") {
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Master Notifications",
                        checked = uiState.notifications.master,
                        onCheckedChange = { viewModel.toggleMasterNotification(it) }
                    )
                    SettingsToggleRow(
                        icon = Icons.Default.CheckCircle,
                        title = "Task Reminders",
                        checked = uiState.notifications.tasks,
                        onCheckedChange = { viewModel.toggleTaskNotification(it) },
                        enabled = uiState.notifications.master
                    )
                    SettingsToggleRow(
                        icon = Icons.Default.Refresh,
                        title = "Habit Reminders",
                        checked = uiState.notifications.habits,
                        onCheckedChange = { viewModel.toggleHabitNotification(it) },
                        enabled = uiState.notifications.master
                    )
                    SettingsToggleRow(
                        icon = Icons.Default.Lock,
                        title = "Commitment Reminders",
                        checked = uiState.notifications.commitments,
                        onCheckedChange = { viewModel.toggleCommitmentNotification(it) },
                        enabled = uiState.notifications.master
                    )
                    SettingsToggleRow(
                        icon = Icons.Default.PlayArrow,
                        title = "Focus Reminders",
                        checked = uiState.notifications.focus,
                        onCheckedChange = { viewModel.toggleFocusNotification(it) },
                        enabled = uiState.notifications.master
                    )
                }
            }

            // Productivity Section
            item {
                SettingsSection("Productivity") {
                    SettingsRow(
                        icon = Icons.Default.Timer,
                        title = "Default Focus Duration",
                        subtitle = "${uiState.focusDuration} min",
                        onClick = { }
                    )
                    SettingsRow(
                        icon = Icons.Default.FreeBreakfast,
                        title = "Default Break Duration",
                        subtitle = "${uiState.breakDuration} min",
                        onClick = { }
                    )
                    SettingsRow(
                        icon = Icons.Default.DateRange,
                        title = "Working Hours",
                        subtitle = uiState.workingHours,
                        onClick = { }
                    )
                }
            }

            // Account Section
            item {
                SettingsSection("Account") {
                    SettingsRow(
                        icon = Icons.Default.Person,
                        title = "Profile Information",
                        subtitle = uiState.userProfile,
                        onClick = onNavigateToProfile
                    )
                    SettingsToggleRow(
                        icon = Icons.Default.CloudSync,
                        title = "Sync Data",
                        checked = uiState.syncEnabled,
                        onCheckedChange = { viewModel.toggleSync(it) }
                    )
                    SettingsRow(
                        icon = Icons.Default.Share,
                        title = "Export Data",
                        onClick = { viewModel.exportData() }
                    )
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Sign Out",
                        onClick = { viewModel.signOut() }
                    )
                    SettingsRow(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        onClick = { viewModel.deleteAccount() },
                        titleColor = MaterialTheme.colorScheme.error,
                        iconColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            // About Section
            item {
                SettingsSection("About") {
                    SettingsRow(
                        icon = Icons.Default.Shield,
                        title = "Privacy Policy",
                        onClick = onNavigateToPrivacy
                    )
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.List,
                        title = "Terms of Service",
                        onClick = onNavigateToTerms
                    )
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = "About FocusFlow",
                        subtitle = "Version 1.0.0",
                        onClick = onNavigateToAbout
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = FocusFlowCorners.Card,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp)) },
        headlineContent = { Text(title, color = titleColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium) },
        supportingContent = subtitle?.let { { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    ListItem(
        leadingContent = { 
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(20.dp)
            ) 
        },
        headlineContent = { 
            Text(
                title, 
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            ) 
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
