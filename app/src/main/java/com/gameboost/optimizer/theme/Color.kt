package com.gameboost.optimizer.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// GAMEBOOST RED CORE — REDMAGIC-INSPIRED PALETTE
// Strongly red-dominant, dark industrial surfaces, technical gaming HUD
// =========================================================================

// Backgrounds & Industrial Surfaces
val RedCoreBgPrimary = Color(0xFF080808)       // Primary background #080808
val RedCoreBgSecondary = Color(0xFF111111)     // Secondary background #111111
val RedCoreSurfaceElevated = Color(0xFF191919) // Elevated surface #191919
val RedCoreSurfaceCard = Color(0xFF131313)     // Dark industrial card surface
val RedCoreSurfaceActive = Color(0xFF221113)   // Active surface with dark crimson tint

// Borders & Dividers
val RedCoreBorder = Color(0xFF303030)          // Panel border #303030
val RedCoreBorderSubtle = Color(0xFF222222)    // Subtle industrial divider
val RedCoreBorderActive = Color(0xFFFF202E)    // Active red border #FF202E
val RedCoreBorderGlow = Color(0xFFFF3344)      // Glowing edge highlight #FF3344

// Gaming Red Accents (RED CORE)
val RedCoreGamingRed = Color(0xFFFF202E)       // Primary gaming red #FF202E
val RedCoreBrightRed = Color(0xFFFF3344)       // Bright active red #FF3344
val RedCoreDeepRed = Color(0xFF8F0715)         // Deep red #8F0715
val RedCoreDarkCrimson = Color(0xFF3D060B)     // Dark background crimson
val RedCoreGlow = Color(0x33FF202E)            // Red glow at low opacity
val RedCoreGlowSubtle = Color(0x1AFF202E)      // Subtle glow
val RedCoreGlowBright = Color(0x66FF202E)      // Prominent glow for active controls

// Typography & Content
val RedCoreTextPrimary = Color(0xFFF5F5F5)     // Main text #F5F5F5
val RedCoreTextSecondary = Color(0xFF929292)   // Secondary text #929292
val RedCoreTextMuted = Color(0xFF5E5E5E)       // Muted/tertiary text
val RedCoreTextRed = Color(0xFFFF3344)         // Red accented text

// Technical Status Telemetry
val RedCoreSuccess = Color(0xFF42D982)         // Success #42D982
val RedCoreSuccessContainer = Color(0xFF0C2B1A)
val RedCoreWarning = Color(0xFFFFB020)         // Warning #FFB020
val RedCoreWarningContainer = Color(0xFF332004)
val RedCoreDanger = Color(0xFFFF202E)          // Danger / Error (Matches Gaming Red)
val RedCoreDangerContainer = Color(0xFF38070B)
val RedCoreNeutral = Color(0xFF757575)
val RedCoreNeutralContainer = Color(0xFF1C1C1C)

// BOBO Launcher Palette (Section 2)
val BoboBgPrimary = Color(0xFF080808)
val BoboBgSecondary = Color(0xFF111111)
val BoboCardSurface = Color(0xFF191919)
val BoboPrimaryAccent = Color(0xFFFF2538)
val BoboSecondaryAccent = Color(0xFFFF5260)
val BoboTextPrimary = Color(0xFFF5F5F5)
val BoboTextMuted = Color(0xFF9B9B9B)
val BoboSuccess = Color(0xFF42D982)
val BoboWarning = Color(0xFFFFB020)

// Backward-compatible aliases for existing references across codebase
val DarkBg = RedCoreBgPrimary
val SurfaceCard = RedCoreSurfaceCard
val SurfaceElevated = RedCoreSurfaceElevated
val SurfaceCardActive = RedCoreSurfaceActive
val BorderSubtle = RedCoreBorderSubtle
val BorderHighlight = RedCoreBorder
val BorderActive = RedCoreBorderActive

val AccentPrimary = RedCoreGamingRed
val AccentPrimaryDim = RedCoreDeepRed
val AccentPrimaryContainer = RedCoreDarkCrimson
val AccentSecondary = RedCoreTextSecondary
val AccentSecondaryContainer = RedCoreSurfaceElevated

val StatusReady = RedCoreSuccess
val StatusReadyContainer = RedCoreSuccessContainer
val StatusWarning = RedCoreWarning
val StatusWarningContainer = RedCoreWarningContainer
val StatusDanger = RedCoreDanger
val StatusDangerContainer = RedCoreDangerContainer
val StatusNeutral = RedCoreNeutral
val StatusNeutralContainer = RedCoreNeutralContainer

val TextPrimary = RedCoreTextPrimary
val TextSecondary = RedCoreTextSecondary
val TextTertiary = RedCoreTextMuted
val TextWhite = RedCoreTextPrimary
val TextGray = RedCoreTextSecondary
val TextMuted = RedCoreTextMuted

// RedCore specific aliases
val PrimaryGamingRed = RedCoreGamingRed
val BrightActiveRed = RedCoreBrightRed
val DeepRed = RedCoreDeepRed
val PanelBorder = RedCoreBorder
val RedGlow = RedCoreGlow

// Legacy aliases mapping to Red Core
val ObsidianBg = RedCoreBgPrimary
val CardNavy = RedCoreSurfaceCard
val CardNavyElevated = RedCoreSurfaceElevated
val CardBorder = RedCoreBorder
val CyberCyan = RedCoreGamingRed
val CyberCyanDim = RedCoreDeepRed
val CyberCyanContainer = RedCoreDarkCrimson
val ElectricPurple = RedCoreBrightRed
val ElectricPurpleDim = RedCoreDeepRed
val ElectricPurpleContainer = RedCoreDarkCrimson
val NeonGreen = RedCoreSuccess
val NeonGreenDim = Color(0xFF2E9E5E)
val NeonGreenContainer = RedCoreSuccessContainer
val CyberAmber = RedCoreWarning
val CyberAmberContainer = RedCoreWarningContainer
val NeonRed = RedCoreGamingRed
val NeonRedContainer = RedCoreDarkCrimson
