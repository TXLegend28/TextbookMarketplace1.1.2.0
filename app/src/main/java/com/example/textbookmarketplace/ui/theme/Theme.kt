package com.example.textbookmarketplace.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = OnGold,
    primaryContainer = GoldLight,
    onPrimaryContainer = OnGold,
    secondary = GoldDark,
    onSecondary = OnGold,
    tertiary = VerifiedBlue,
    background = CreamBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF79747E)
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = OnGold,
    primaryContainer = GoldDark,
    onPrimaryContainer = OnGold,
    secondary = GoldLight,
    onSecondary = OnGold,
    tertiary = VerifiedBlue,
    background = DarkBackground,
    onBackground = Color(0xFFE6E1E5),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E1E5),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = Color(0xFF938F99)
)

@Composable
fun TextbookMarketplaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}