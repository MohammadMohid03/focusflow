package com.focusflow.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Service") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Last updated: August 2026\n\n" +
                        "1. Acceptance of Terms\n" +
                        "By using FocusFlow, you agree to these Terms of Service.\n\n" +
                        "2. User Responsibilities\n" +
                        "You are responsible for maintaining the confidentiality of your account.\n\n" +
                        "3. Commitments and Financials\n" +
                        "If you use financial commitments, you agree that penalties are binding and non-refundable as per the commitment contract logic.\n\n" +
                        "4. Termination\n" +
                        "We reserve the right to terminate accounts that violate our terms.\n\n" +
                        "5. Changes to Terms\n" +
                        "We may modify these terms at any time.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
