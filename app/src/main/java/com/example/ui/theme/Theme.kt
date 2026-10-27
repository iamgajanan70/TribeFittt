package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF111111),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1B1B1B),
    onPrimaryContainer = Color(0xFF848484),
    background = Color(0xFFF9F9F9),
    onBackground = Color(0xFF1A1C1C),
    surface = Color(0xFFF9F9F9),
    onSurface = Color(0xFF1A1C1C),
    surfaceVariant = Color(0xFFE2E2E2),
    onSurfaceVariant = Color(0xFF4C4546),
    outline = Color(0xFF7E7576),
    outlineVariant = Color(0xFFCFC4C5),
    surfaceContainer = Color(0xFFEEEEEE),
    surfaceContainerLow = Color(0xFFF3F3F4),
    surfaceContainerLowest = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF101012),
    primaryContainer = Color(0xFF2E2E38),
    onPrimaryContainer = Color(0xFFD4D4D8),
    background = Color(0xFF101012),
    onBackground = Color(0xFFF4F4F5),
    surface = Color(0xFF18181B),
    onSurface = Color(0xFFF4F4F5),
    surfaceVariant = Color(0xFF2C2C34),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF71717A),
    outlineVariant = Color(0xFF3F3F46),
    surfaceContainer = Color(0xFF2C2C34),
    surfaceContainerLow = Color(0xFF25252C),
    surfaceContainerLowest = Color(0xFF18181B)
)

@Composable
fun FitnessOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val fitnessColors = if (darkTheme) DarkFitnessColors else LightFitnessColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalFitnessColors provides fitnessColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
