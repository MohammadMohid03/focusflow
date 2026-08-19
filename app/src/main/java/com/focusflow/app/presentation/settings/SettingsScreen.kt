package com.focusflow.app.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.theme.FocusFlowCorners

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToAppSelection: () -> Unit = {},
    onNavigateToCommitmentHistory: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Color Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        "LIGHT_SAGE" to "Warm Sage Green (Default)",
                        "LIGHT_OFFWHITE" to "Minimalist Off-White & Charcoal",
                        "LIGHT_SLATE" to "Modern Slate Indigo",
                        "DARK" to "Dark Minimalist",
                        "SYSTEM" to "System Default"
                    ).forEach { (mode, label) ->
                        Surface(
                            onClick = {
                                viewModel.updateTheme(mode)
                                showThemeDialog = false
                            },
                            shape = FocusFlowCorners.CardSmall,
                            color = if (uiState.themeMode.equals(mode, ignoreCase = true) || (mode == "LIGHT_SAGE" && uiState.themeMode == "LIGHT")) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (uiState.themeMode.equals(mode, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.themeMode.equals(mode, ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (uiState.themeMode.equals(mode, ignoreCase = true) || (mode == "LIGHT_SAGE" && uiState.themeMode == "LIGHT")) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
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

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out of FocusFlow?", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.signOut {
                            onSignOut()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = FocusFlowCorners.Button
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = FocusFlowCorners.Dialog,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = { Text("This action cannot be undone. All your tasks, habits, and progress will be deleted.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccount {
                            onSignOut()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = FocusFlowCorners.Button
                ) {
                    Text("Delete Forever")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // Commitment Lock & Focus Section
            item {
                SettingsSection("Commitment Lock & Distractions") {
                    SettingsRow(
                        icon = Icons.Default.Apps,
                        title = "Configure Locked Apps",
                        subtitle = "Select distracting apps to restrict during focus",
                        onClick = onNavigateToAppSelection
                    )
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = "Focus Contracts & History",
                        subtitle = "View active lock contracts and completion log",
                        onClick = onNavigateToCommitmentHistory
                    )
                    SettingsRow(
                        icon = if (uiState.hasUsagePermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                        title = "Usage Access Permission",
                        subtitle = if (uiState.hasUsagePermission) "Granted · App detection active" else "Not Granted · Tap to allow app detection",
                        iconColor = if (uiState.hasUsagePermission) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        onClick = { viewModel.requestUsagePermission() }
                    )
                    SettingsRow(
                        icon = if (uiState.hasOverlayPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                        title = "Display Over Other Apps",
                        subtitle = if (uiState.hasOverlayPermission) "Granted · Lock overlay active" else "Not Granted · Tap to allow lock screen",
                        iconColor = if (uiState.hasOverlayPermission) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        onClick = { viewModel.requestOverlayPermission() }
                    )
                }
            }

            // Appearance Section
            item {
                SettingsSection("Appearance") {
                    SettingsRow(
                        icon = Icons.Default.Palette,
                        title = "Color Theme",
                        subtitle = when (uiState.themeMode) {
                            "LIGHT_OFFWHITE" -> "Minimalist Off-White & Charcoal"
                            "LIGHT_SLATE" -> "Modern Slate Indigo"
                            "DARK" -> "Dark Minimalist"
                            "SYSTEM" -> "System Default"
                            else -> "Warm Sage Green"
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
                        title = "Commitment Alerts",
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
                }
            }

            // Account & Data Section
            item {
                SettingsSection("Account & Data") {
                    SettingsRow(
                        icon = Icons.Default.Person,
                        title = "User Profile",
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
                        titleColor = MaterialTheme.colorScheme.error,
                        iconColor = MaterialTheme.colorScheme.error,
                        onClick = { showSignOutDialog = true }
                    )
                    SettingsRow(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        onClick = { showDeleteAccountDialog = true },
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
            shape = FocusFlowCorners.Card,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
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
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 0.1f else 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
