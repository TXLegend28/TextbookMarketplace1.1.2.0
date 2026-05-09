package com.example.textbookmarketplace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.textbookmarketplace.ui.theme.GoldPrimary
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.textbookmarketplace.ui.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(
    onFinish: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = GoldPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to TM Marketplace",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your all-in-one platform to buy, sell, and read digital textbooks. " +
                        "Connect with sellers, access PDF/DOCX materials instantly, and manage your listings with ease.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    viewModel.setFirstLaunchFalse()
                    onFinish()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Get Started", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}