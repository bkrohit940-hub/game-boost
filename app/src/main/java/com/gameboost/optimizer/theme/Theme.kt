package com.gameboost.optimizer.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameBoostDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = CyberCyanContainer,
    onPrimaryContainer = CyberCyan,

    secondary = ElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = ElectricPurpleContainer,
    onSecondaryContainer = ElectricPurple,

    tertiary = NeonGreen,
    onTertiary = Color(0xFF003816),
    tertiaryContainer = NeonGreenContainer,
    onTertiaryContainer = NeonGreen,

    background = ObsidianBg,
    onBackground = TextWhite,

    surface = CardNavy,
    onSurface = TextWhite,
    surfaceVariant = CardNavyElevated,
    onSurfaceVariant = TextGray,

    outline = CardBorder,
    error = NeonRed,
    errorContainer = NeonRedContainer,
    onError = Color.White
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
