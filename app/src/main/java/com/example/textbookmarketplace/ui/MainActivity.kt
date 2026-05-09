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
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            val isFirstLaunch by authViewModel.isFirstLaunch.collectAsState()
            val themeMode by authViewModel.themeMode.collectAsState()

            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            TextbookMarketplaceTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                // Auto-redirect on auth change
                LaunchedEffect(isLoggedIn) {
                    if (!isLoggedIn) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                val startDest = when {
                    isFirstLaunch -> "welcome"
                    isLoggedIn -> Screen.Home.route
                    else -> Screen.Login.route
                }

                NavGraph(
                    navController = navController,
                    startDestination = startDest,
                    onLoginSuccess = {
                        authViewModel.setFirstLaunchFalse()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}