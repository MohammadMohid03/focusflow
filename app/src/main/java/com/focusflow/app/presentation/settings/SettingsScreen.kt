package com.focusflow.app.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Appearance Section
            item {
                SettingsSection("Appearance") {
                    SettingsRow(
                        icon = Icons.Default.Build,
                        title = "Theme",
                        subtitle = uiState.themeMode,
                        onClick = { }
                    )
                }
            }

            // Notifications Section
            item {
                SettingsSection("Notifications") {
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Master Toggle",
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
                        icon = Icons.Default.CheckCircle,
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
                    SettingsToggleRow(
                        icon = Icons.Default.DateRange,
                        title = "Daily Plan",
                        checked = uiState.notifications.dailyPlan,
                        onCheckedChange = { viewModel.toggleDailyPlanNotification(it) },
                        enabled = uiState.notifications.master
                    )
                }
            }

            // Productivity Section
            item {
                SettingsSection("Productivity") {
                    SettingsRow(
                        icon = Icons.Default.PlayArrow,
                        title = "Default Focus Duration",
                        subtitle = "${uiState.focusDuration} min",
                        onClick = { }
                    )
                    SettingsRow(
                        icon = Icons.Default.Refresh,
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
                        title = "Profile",
                        onClick = onNavigateToProfile
                    )
                    SettingsToggleRow(
                        icon = Icons.Default.Refresh,
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
                        icon = Icons.Default.ExitToApp,
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
                        icon = Icons.Default.Lock,
                        title = "Privacy Policy",
                        onClick = onNavigateToPrivacy
                    )
                    SettingsRow(
                        icon = Icons.Default.List,
                        title = "Terms of Service",
                        onClick = onNavigateToTerms
                    )
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = "About FocusFlow",
                        onClick = onNavigateToAbout
                    )
                    ListItem(
                        headlineContent = { Text("Version") },
                        trailingContent = { Text("1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant) }
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
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
        leadingContent = { Icon(icon, contentDescription = null, tint = iconColor) },
        headlineContent = { Text(title, color = titleColor) },
        supportingContent = subtitle?.let { { Text(it) } }
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
        leadingContent = { Icon(icon, contentDescription = null, tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)) },
        headlineContent = { Text(title, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    )
}
