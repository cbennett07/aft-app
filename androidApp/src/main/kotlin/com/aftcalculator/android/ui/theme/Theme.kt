package com.aftcalculator.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ArmyGold,
    onPrimary = ArmyBlack,
    primaryContainer = ArmyGoldDark,
    onPrimaryContainer = ArmyBlack,
    secondary = ArmyNavyLight,
    onSecondary = Color.White,
    secondaryContainer = ArmyNavy,
    onSecondaryContainer = Color.White,
    tertiary = ArmyGold,
    onTertiary = ArmyBlack,
    background = ArmyBlack,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = CardDark,
    onSurfaceVariant = ArmyLightGray,
    error = FailRed,
    onError = Color.White,
    outline = ArmyMediumGray,
    outlineVariant = ArmyDarkGray
)

private val LightColorScheme = lightColorScheme(
    primary = ArmyNavy,
    onPrimary = Color.White,
    primaryContainer = ArmyNavyLight,
    onPrimaryContainer = Color.White,
    secondary = ArmyGold,
    onSecondary = ArmyBlack,
    secondaryContainer = ArmyGold.copy(alpha = 0.2f),
    onSecondaryContainer = ArmyBlack,
    tertiary = ArmyGold,
    onTertiary = ArmyBlack,
    background = SurfaceLight,
    onBackground = ArmyBlack,
    surface = CardLight,
    onSurface = ArmyBlack,
    surfaceVariant = ArmyLightGray,
    onSurfaceVariant = ArmyDarkGray,
    error = FailRed,
    onError = Color.White,
    outline = ArmyMediumGray,
    outlineVariant = ArmyLightGray
)

@Composable
fun AFTCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) ArmyBlack.toArgb() else ArmyNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
