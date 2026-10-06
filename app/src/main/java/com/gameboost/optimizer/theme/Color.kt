package com.gameboost.optimizer.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// GAME BOOST - MINIMAL RESTRAINED DARK THEME
// Professional gaming launcher & utility design
// ==========================================

// Surfaces & Backgrounds
val DarkBg = Color(0xFF0C0E12)
val SurfaceCard = Color(0xFF141820)
val SurfaceElevated = Color(0xFF1B212D)
val SurfaceCardActive = Color(0xFF1E2534)

// Borders & Dividers
val BorderSubtle = Color(0xFF232A38)
val BorderHighlight = Color(0xFF333E52)
val BorderActive = Color(0xFF38BDF8)

// Accents (Clean, non-neon, technical precision)
val AccentPrimary = Color(0xFF38BDF8)       // Ice / Titanium Blue
val AccentPrimaryDim = Color(0xFF0284C7)
val AccentPrimaryContainer = Color(0xFF0C2436)

val AccentSecondary = Color(0xFF94A3B8)
val AccentSecondaryContainer = Color(0xFF1E293B)

// Status & Verification (Accurate, high-readability)
val StatusReady = Color(0xFF10B981)         // Emerald
val StatusReadyContainer = Color(0xFF0B291D)

val StatusWarning = Color(0xFFF59E0B)       // Amber
val StatusWarningContainer = Color(0xFF2D200A)

val StatusDanger = Color(0xFFEF4444)        // Crimson
val StatusDangerContainer = Color(0xFF2E1217)

val StatusNeutral = Color(0xFF64748B)       // Slate
val StatusNeutralContainer = Color(0xFF18202F)

// High-contrast clean typography
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// Compatibility aliases for existing components
val ObsidianBg = DarkBg
val CardNavy = SurfaceCard
val CardNavyElevated = SurfaceElevated
val CardBorder = BorderSubtle
val CyberCyan = AccentPrimary
val CyberCyanDim = AccentPrimaryDim
val CyberCyanContainer = AccentPrimaryContainer
val ElectricPurple = AccentPrimary
val ElectricPurpleDim = AccentPrimaryDim
val ElectricPurpleContainer = AccentPrimaryContainer
val NeonGreen = StatusReady
val NeonGreenDim = Color(0xFF059669)
val NeonGreenContainer = StatusReadyContainer
val CyberAmber = StatusWarning
val CyberAmberContainer = StatusWarningContainer
val NeonRed = StatusDanger
val NeonRedContainer = StatusDangerContainer
val TextWhite = TextPrimary
val TextGray = TextSecondary
val TextMuted = TextTertiary
