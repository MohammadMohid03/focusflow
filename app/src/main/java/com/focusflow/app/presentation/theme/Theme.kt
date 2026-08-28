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

// 1. Primary Light Theme (Refined Botanical Forest Pine + Clean Warm Neutrals)
val PrimaryLightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = ForestContainer,
    onPrimaryContainer = ForestOnContainer,
    secondary = WarmSand,
    onSecondary = Color.White,
    secondaryContainer = WarmSandContainer,
    onSecondaryContainer = WarmSandOnContainer,
    tertiary = BronzeAmber,
    onTertiary = Color.White,
    tertiaryContainer = BronzeContainer,
    onTertiaryContainer = BronzeOnContainer,
    error = SemanticError,
    errorContainer = SemanticErrorContainer,
    onError = Color.White,
    onErrorContainer = SemanticError,
    background = CanvasBackground,
    onBackground = TextPrimary,
    surface = SurfacePure,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderMedium
)

// 2. Off-White Minimalist Theme (Architectural Neutral Charcoal)
val MinimalistLightColorScheme = lightColorScheme(
    primary = Color(0xFF262626),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFEFEF),
    onPrimaryContainer = Color(0xFF171717),
    secondary = Color(0xFF525252),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F5F5),
    onSecondaryContainer = Color(0xFF262626),
    tertiary = BronzeAmber,
    onTertiary = Color.White,
    tertiaryContainer = BronzeContainer,
    onTertiaryContainer = BronzeOnContainer,
    error = SemanticError,
    errorContainer = SemanticErrorContainer,
    onError = Color.White,
    onErrorContainer = SemanticError,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF171717),
    surface = Color.White,
    onSurface = Color(0xFF171717),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF525252),
    outline = Color(0xFFE5E5E5),
    outlineVariant = Color(0xFFD4D4D4)
)

// 3. Clean Slate & Navy Theme
val SlateLightColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A5F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6EEF8),
    onPrimaryContainer = Color(0xFF0F1E33),
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = BronzeAmber,
    onTertiary = Color.White,
    tertiaryContainer = BronzeContainer,
    onTertiaryContainer = BronzeOnContainer,
    error = SemanticError,
    errorContainer = SemanticErrorContainer,
    onError = Color.White,
    onErrorContainer = SemanticError,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1)
)

// 4. Dark Theme
val PrimaryDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkCanvas,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,
    secondary = Color(0xFFA89F91),
    onSecondary = DarkCanvas,
    secondaryContainer = DarkSurfaceSubtle,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = BronzeAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF33200C),
    onTertiaryContainer = Color(0xFFF6D4A4),
    error = Color(0xFFEF4444),
    errorContainer = Color(0xFF450A0A),
    onError = Color.White,
    onErrorContainer = Color(0xFFFCA5A5),
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceSubtle,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkSurfaceSubtle
)

@Composable
fun FocusFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        else -> false
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> PrimaryDarkColorScheme
        themeMode == ThemeMode.LIGHT_OFFWHITE -> MinimalistLightColorScheme
        themeMode == ThemeMode.LIGHT_SLATE -> SlateLightColorScheme
        else -> PrimaryLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
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
