package com.gameboost.optimizer.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// =========================================================================
// GAMEBOOST RED CORE — SHAPES & TOKENS
// =========================================================================

val RedCoreAngularShape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)

private val GameBoostDarkColorScheme = darkColorScheme(
    primary = RedCoreGamingRed,
    onPrimary = Color.White,
    primaryContainer = RedCoreDarkCrimson,
    onPrimaryContainer = RedCoreBrightRed,

    secondary = RedCoreDeepRed,
    onSecondary = Color.White,
    secondaryContainer = RedCoreSurfaceElevated,
    onSecondaryContainer = RedCoreTextPrimary,

    tertiary = RedCoreSuccess,
    onTertiary = Color.Black,
    tertiaryContainer = RedCoreSuccessContainer,
    onTertiaryContainer = RedCoreSuccess,

    background = RedCoreBgPrimary,
    onBackground = RedCoreTextPrimary,

    surface = RedCoreSurfaceCard,
    onSurface = RedCoreTextPrimary,
    surfaceVariant = RedCoreSurfaceElevated,
    onSurfaceVariant = RedCoreTextSecondary,

    outline = RedCoreBorder,
    outlineVariant = RedCoreBorderSubtle,

    error = RedCoreDanger,
    errorContainer = RedCoreDangerContainer,
    onError = Color.White,
    onErrorContainer = RedCoreBrightRed
)

@Composable
fun GameBoostTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameBoostDarkColorScheme,
        typography = Typography,
        content = content
    )
}
