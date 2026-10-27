package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class FitnessColors(
    val background: Color,
    val surface: Color,
    val surfaceLowest: Color,
    val surfaceLow: Color,
    val surfaceContainerLow: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val isDark: Boolean
)

val LightFitnessColors = FitnessColors(
    background = Color(0xFFF9F9F9),
    surface = Color(0xFFF9F9F9),
    surfaceLowest = Color(0xFFFFFFFF),
    surfaceLow = Color(0xFFF3F3F4),
    surfaceContainerLow = Color(0xFFF3F3F4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFEEEEEE),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFE2E2E2),
    surfaceVariant = Color(0xFFE2E2E2),
    primary = Color(0xFF111111),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1B1B1B),
    onPrimaryContainer = Color(0xFF848484),
    onSurface = Color(0xFF1A1C1C),
    onSurfaceVariant = Color(0xFF4C4546),
    outline = Color(0xFF7E7576),
    outlineVariant = Color(0xFFCFC4C5),
    isDark = false
)

val DarkFitnessColors = FitnessColors(
    background = Color(0xFF101012),
    surface = Color(0xFF18181B),
    surfaceLowest = Color(0xFF1E1E24),
    surfaceLow = Color(0xFF25252C),
    surfaceContainerLow = Color(0xFF25252C),
    surfaceContainerLowest = Color(0xFF18181B),
    surfaceContainer = Color(0xFF2C2C34),
    surfaceContainerHigh = Color(0xFF383842),
    surfaceContainerHighest = Color(0xFF444450),
    surfaceVariant = Color(0xFF2C2C34),
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF101012),
    primaryContainer = Color(0xFF2E2E38),
    onPrimaryContainer = Color(0xFFD4D4D8),
    onSurface = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF71717A),
    outlineVariant = Color(0xFF3F3F46),
    isDark = true
)

val LocalFitnessColors = staticCompositionLocalOf { LightFitnessColors }

val MonoBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.background

val MonoSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surface

val MonoSurfaceLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceLowest

val MonoSurfaceLow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceLow

val MonoSurfaceContainerLow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceContainerLow

val MonoSurfaceContainerLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceContainerLowest

val MonoSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceContainer

val MonoSurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceContainerHigh

val MonoSurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceContainerHighest

val MonoSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.surfaceVariant

val MonoPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.primary

val MonoOnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.onPrimary

val MonoPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.primaryContainer

val MonoOnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.onPrimaryContainer

val MonoOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.onSurface

val MonoOnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.onSurfaceVariant

val MonoOutline: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.outline

val MonoOutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFitnessColors.current.outlineVariant

// Static fallbacks if ever needed
val MonoDarkBackground = Color(0xFF101012)
val MonoDarkSurface = Color(0xFF18181B)
val MonoDarkSurfaceLowest = Color(0xFF1E1E24)
val MonoDarkOnSurface = Color(0xFFF4F4F5)
val MonoDarkOnSurfaceVariant = Color(0xFFA1A1AA)
