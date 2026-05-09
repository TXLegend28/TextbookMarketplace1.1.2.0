package com.example.textbookmarketplace.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.textbookmarketplace.ui.navigation.NavGraph
import com.example.textbookmarketplace.ui.navigation.Screen
import com.example.textbookmarketplace.ui.theme.TextbookMarketplaceTheme
import com.example.textbookmarketplace.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            val themeMode by authViewModel.themeMode.collectAsState()

            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            var startDestination by remember { mutableStateOf(Screen.Login.route) }

            // Update start destination based on auth state
            if (isLoggedIn) {
                startDestination = Screen.Home.route
            }

            TextbookMarketplaceTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    onLoginSuccess = { /* handled by navigation */ },
                    onLogout = { /* handled by navigation */ }
                )
            }
        }
    }
}