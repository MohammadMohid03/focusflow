package com.focusflow.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.focusflow.app.domain.model.ThemeMode

val LocalSpacing = staticCompositionLocalOf { Spacing }

private val LightColorScheme = lightColorScheme(
    primary = Sage50, // #5E8C61 (Soft sage green)
    onPrimary = Color.White,
    primaryContainer = Sage95, // #E7F0E5 (Accent light)
    onPrimaryContainer = Sage20, // #1D3B20
    secondary = Sand50, // #92806D (Warm earthy tone)
    onSecondary = Color.White,
    secondaryContainer = Sand90, // #F3EDE3 (Warm beige / soft sand)
    onSecondaryContainer = Sand20,
    tertiary = Amber50, // #D99A3D (Warm gold/amber)
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber20,
    error = Red50, // #D65C5C (Soft red)
    errorContainer = Red90,
    onError = Color.White,
    onErrorContainer = Red20,
    background = Neutral98, // #F7F7F5 (Soft warm white)
    onBackground = Neutral10, // #171717 (Primary text)
    surface = Neutral100, // #FFFFFF (Clean white surface)
    onSurface = Neutral10,
    surfaceVariant = Neutral95, // #F1F2EF (Secondary surface)
    onSurfaceVariant = Neutral40, // #6B6B6B (Secondary text)
    outline = Neutral90, // #E6E6E2 (Border)
    outlineVariant = NeutralVariant90,
    inverseOnSurface = Neutral95,
    inverseSurface = Neutral20,
    inversePrimary = Sage80,
    surfaceTint = Sage50,
)

private val DarkColorScheme = darkColorScheme(
    primary = Sage70,
    onPrimary = Sage10,
    primaryContainer = Sage30,
    onPrimaryContainer = Sage90,
    secondary = Sand70,
    onSecondary = Sand10,
    secondaryContainer = Sand30,
    onSecondaryContainer = Sand90,
    tertiary = Amber70,
    onTertiary = Amber10,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = Red70,
    errorContainer = Red20,
    onError = Red10,
    onErrorContainer = Red90,
    background = Neutral10,
    onBackground = Neutral95,
    surface = Neutral20,
    onSurface = Neutral95,
    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral70,
    outline = Neutral40,
    outlineVariant = Neutral30,
    inverseOnSurface = Neutral10,
    inverseSurface = Neutral90,
    inversePrimary = Sage40,
    surfaceTint = Sage70,
)

@Composable
fun FocusFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
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
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FocusFlowTypography,
            shapes = FocusFlowShapes,
            content = content
        )
    }
}
