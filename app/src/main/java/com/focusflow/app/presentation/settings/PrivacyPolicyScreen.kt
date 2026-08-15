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
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
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
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Last updated: August 2026\n\n" +
                        "1. Data Collection\n" +
                        "We collect information you provide directly to us when you create an account, create tasks, goals, and commitments.\n\n" +
                        "2. Usage of Data\n" +
                        "Your data is used to provide the FocusFlow services, sync across devices, and offer AI-powered insights.\n\n" +
                        "3. Commitment Data\n" +
                        "Commitment locks and associated data are strictly stored securely. We do not share your private commitments with third parties without consent.\n\n" +
                        "4. Data Storage\n" +
                        "Data is stored on secure cloud servers using standard encryption protocols.\n\n" +
                        "5. Contact Us\n" +
                        "For any privacy-related questions, please contact support@focusflow.app.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
