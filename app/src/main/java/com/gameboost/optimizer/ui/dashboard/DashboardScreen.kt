package com.gameboost.optimizer.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.RestorationResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.BackendType
import com.gameboost.optimizer.system.PrivilegedSystemState
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.DeepRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreAngularShape
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreGlow
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextRed
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.ConnectionStatus
import com.gameboost.optimizer.ui.components.DisplayFpsPanel
import com.gameboost.optimizer.ui.components.GameBoostLogo
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.MetricChip
import com.gameboost.optimizer.ui.components.PrimaryBoostButton
import com.gameboost.optimizer.ui.components.RedCoreCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge
import com.gameboost.optimizer.ui.components.StatusPill
import com.gameboost.optimizer.ui.components.ThermalRiskDialog

/**
 * HOME: GAMING COMMAND CENTER (Section 5)
 * RedMagic-inspired gaming phone control center:
 * 1. Top header: GameBoost tactical logo, compact title, backend status, settings shortcut
 * 2. Featured game panel: Large real game icon, name, installed status, target profile, angular red accent frame
 * 3. Main BOOST control: Dominant red button ("BOOST & PLAY"), pulsing ready glow, real progress
 * 4. Live system panel: Real available telemetry (refresh rate, CPU, RAM, thermals, session)
 * 5. Display & FPS matrix: Hardware refresh rate vs render FPS
 * 6. Quick actions: Select Game, Performance Profiles, Diagnostics, Restore Defaults
 */
@Composable
fun DashboardScreen(
    shizukuStatus: ShizukuStatus,
    isOptimized: Boolean,
    activeGameProfile: GameProfile?,
    lastResult: OptimizationResult?,
    lastRestorationResult: RestorationResult? = null,
    games: List<GameProfile>,
    hardwareStats: HardwareStats,
    userPreferences: AppUserPreferences,
    privilegedState: PrivilegedSystemState? = null,
    selectedMode: PerformanceMode = PerformanceMode.PERFORMANCE,
    selectedGameId: String? = null,
    selectedRefreshRate: Float = 0f,
    supportedRefreshRates: List<Float> = emptyList(),
    isBoosting: Boolean = false,
    onSelectGame: (String) -> Unit = {},
    onSelectMode: (PerformanceMode) -> Unit = {},
    onSelectRefreshRate: (Float) -> Unit = {},
    onBoostAndPlay: (String, Boolean) -> Unit = { _, _ -> },
    onBoostOnly: (String, Boolean) -> Unit = { _, _ -> },
    onPlayOnly: (String) -> Unit = {},
    onRestoreDefault: () -> Unit,
    onGameClick: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onFixShizuku: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showThermalDialogForAction by remember { mutableStateOf<String?>(null) }

    // Resolve target active game
    val activeGame = remember(selectedGameId, games) {
        games.firstOrNull { it.id == selectedGameId }
            ?: games.firstOrNull { it.isInstalled }
            ?: games.firstOrNull()
    }

    if (showThermalDialogForAction != null && activeGame != null) {
        ThermalRiskDialog(
            onConfirm = {
                val action = showThermalDialogForAction
                showThermalDialogForAction = null
                if (action == "boost_play") {
                    onBoostAndPlay(activeGame.id, true)
                } else if (action == "boost_only") {
                    onBoostOnly(activeGame.id, true)
                }
            },
            onDismiss = { showThermalDialogForAction = null }
        )
    }

    val backendType = privilegedState?.activeBackendType
        ?: if (shizukuStatus.isReady) BackendType.SHIZUKU else BackendType.NONE
    val isPrivilegedReady = privilegedState?.isPrivilegedReady ?: shizukuStatus.isReady

    Surface(
        modifier = modifier.fillMaxSize(),
        color = RedCoreBgPrimary
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrowScreen = maxWidth < 360.dp
            val horizontalPadding = if (isNarrowScreen) 10.dp else 14.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // =========================================================================
                // 1. TOP HEADER: LOGO + COMPACT TITLE + BACKEND STATUS + SETTINGS SHORTCUT
                // =========================================================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Tactical Logo + Compact App Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GameBoostLogo(sizeDp = 34.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "GAME",
                                        color = RedCoreTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp
                                    )
                                    Text(
                                        text = "BOOST",
                                        color = PrimaryGamingRed,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp
                                    )
                                }
                                Text(
                                    text = "RED CORE // COMMAND",
                                    color = RedCoreTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        // Right: Backend Connection Status Pill & Settings Shortcut
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ConnectionStatus(
                                backendType = backendType,
                                isReady = isPrivilegedReady,
                                onClick = {
                                    if (isPrivilegedReady) onOpenDiagnostics() else onFixShizuku()
                                }
                            )

                            // Settings shortcut
                            Surface(
                                shape = CutCornerShape(4.dp),
                                color = RedCoreSurfaceElevated,
                                border = BorderStroke(1.dp, RedCoreBorder),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable { onOpenSettings() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = RedCoreTextPrimary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 2. FEATURED GAME PANEL (VISUAL CENTER OF THE DASHBOARD)
                // Large actual game icon, name, installed status, selected profile, angular red frame
                // =========================================================================
                item {
                    if (activeGame != null) {
                        val isGameInstalled = activeGame.isInstalled
                        val isSessionActive = isOptimized && activeGameProfile?.id == activeGame.id

                        RedCoreCard(
                            modifier = Modifier.fillMaxWidth(),
                            isActive = isSessionActive || true, // highlighted as featured
                            borderColor = if (isSessionActive) RedCoreSuccess else RedCoreBorderActive,
                            backgroundColor = RedCoreSurfaceElevated
                        ) {
                            // Sub-header inside panel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 3.dp, height = 11.dp)
                                            .background(PrimaryGamingRed)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "FEATURED TARGET // GAME SPACE",
                                        color = RedCoreTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                StatusBadge(
                                    text = if (isSessionActive) "ACTIVE BOOST" else if (isGameInstalled) "INSTALLED" else "NOT INSTALLED",
                                    state = if (isSessionActive || isGameInstalled) BadgeState.SUCCESS else BadgeState.NEUTRAL
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Main Game Details Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Large Actual Game Icon with Angular Frame
                                GameIconView(
                                    packageName = activeGame.activePackageName,
                                    isInstalled = isGameInstalled,
                                    sizeDp = 64.dp,
                                    isActive = true
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeGame.displayName,
                                        color = RedCoreTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${activeGame.region} • ${activeGame.activePackageName}",
                                        color = RedCoreTextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Selected Profile Tag
                                        Surface(
                                            shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                                            color = RedCoreDarkCrimson,
                                            border = BorderStroke(1.dp, BrightActiveRed.copy(alpha = 0.6f))
                                        ) {
                                            Text(
                                                text = selectedMode.name,
                                                color = BrightActiveRed,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.6.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Refresh rate target
                                        val displayHz = if (selectedRefreshRate > 0) selectedRefreshRate.toInt()
                                        else if (hardwareStats.currentRefreshRate > 0) hardwareStats.currentRefreshRate.toInt()
                                        else 120

                                        Surface(
                                            shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                                            color = RedCoreBgSecondary,
                                            border = BorderStroke(1.dp, RedCoreBorder)
                                        ) {
                                            Text(
                                                text = "${displayHz}Hz LOCK",
                                                color = RedCoreTextPrimary,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Horizontal Quick Game Switcher Carousel
                            Text(
                                text = "SUPPORTED REGIONAL VARIANTS",
                                color = RedCoreTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(games) { gameItem ->
                                    val isCurrentGame = gameItem.id == activeGame.id
                                    Surface(
                                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                        color = if (isCurrentGame) RedCoreDarkCrimson else RedCoreBgSecondary,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isCurrentGame) BrightActiveRed else RedCoreBorder
                                        ),
                                        modifier = Modifier.clickable {
                                            onSelectGame(gameItem.id)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            GameIconView(
                                                packageName = gameItem.activePackageName,
                                                isInstalled = gameItem.isInstalled,
                                                sizeDp = 20.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = gameItem.region,
                                                color = if (isCurrentGame) BrightActiveRed else RedCoreTextSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 3. MAIN BOOST CONTROL (SECTION 5)
                // Dominant red button: "BOOST & PLAY" / "OPTIMIZE GAMING SESSION"
                // =========================================================================
                item {
                    val canBoost = activeGame != null && activeGame.isInstalled
                    val isThermalOverride = selectedMode == PerformanceMode.THERMAL_OVERRIDE

                    Column(modifier = Modifier.fillMaxWidth()) {
                        PrimaryBoostButton(
                            onClick = {
                                if (activeGame != null) {
                                    if (isThermalOverride) {
                                        showThermalDialogForAction = "boost_play"
                                    } else {
                                        onBoostAndPlay(activeGame.id, false)
                                    }
                                }
                            },
                            enabled = canBoost,
                            isBoosting = isBoosting,
                            text = if (isOptimized) "RE-OPTIMIZE & PLAY" else "BOOST & PLAY",
                            subtext = "LOCK MAXIMUM REFRESH RATE & LAUNCH",
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!canBoost) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select an installed game to enable full hardware optimization.",
                                color = RedCoreWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                // =========================================================================
                // 4. LIVE SYSTEM PANEL: COMPACT TELEMETRY BLOCKS (REAL AVAILABLE DATA)
                // Refresh rate, supported rates, CPU, RAM, thermals, session status
                // =========================================================================
                item {
                    SectionHeader(
                        title = "LIVE TELEMETRY MATRIX",
                        subtitle = "Hardware state & operating telemetry",
                        actionText = "DIAGNOSTICS",
                        onAction = onOpenDiagnostics
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 2x2 compact telemetry grid
                    val currentHz = if (hardwareStats.currentRefreshRate > 0) "${hardwareStats.currentRefreshRate.toInt()} Hz" else "60 Hz"
                    val ramText = hardwareStats.formattedRam
                    val thermalText = if (hardwareStats.batteryTemperatureCelsius > 0) hardwareStats.formattedTemp else "NORMAL"
                    val sessionText = if (isOptimized) "ACTIVE BOOST" else "IDLE"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricChip(
                            label = "PANEL REFRESH",
                            value = currentHz,
                            icon = Icons.Default.Speed,
                            accentColor = BrightActiveRed,
                            modifier = Modifier.weight(1f)
                        )

                        MetricChip(
                            label = "RAM USAGE",
                            value = ramText,
                            icon = Icons.Default.Memory,
                            accentColor = PrimaryGamingRed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricChip(
                            label = "DEVICE THERMALS",
                            value = thermalText,
                            icon = Icons.Default.DeviceThermostat,
                            accentColor = if (hardwareStats.batteryTemperatureCelsius > 42f) RedCoreWarning else RedCoreSuccess,
                            modifier = Modifier.weight(1f)
                        )

                        MetricChip(
                            label = "SESSION STATUS",
                            value = sessionText,
                            icon = Icons.Default.Bolt,
                            accentColor = if (isOptimized) RedCoreSuccess else RedCoreTextMuted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // =========================================================================
                // 5. DEDICATED DISPLAY & FPS PANEL (SECTION 8)
                // Distinguishes display refresh rate from in-game frame rate
                // =========================================================================
                item {
                    DisplayFpsPanel(
                        currentDisplayRate = hardwareStats.currentRefreshRate,
                        supportedRates = supportedRefreshRates,
                        targetRate = selectedRefreshRate,
                        onSelectRate = onSelectRefreshRate
                    )
                }

                // =========================================================================
                // 6. QUICK ACTIONS GRID
                // Select Game, Performance Profiles, Diagnostics, Restore Previous State
                // =========================================================================
                item {
                    SectionHeader(
                        title = "TACTICAL QUICK CONTROLS",
                        subtitle = "Fast hardware commands & session restore"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Quick Action 1: BOOST ONLY (no launch)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    activeGame?.let { onBoostOnly(it.id, selectedMode == PerformanceMode.THERMAL_OVERRIDE) }
                                },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = RedCoreSurfaceElevated,
                            border = BorderStroke(1.dp, RedCoreBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = BrightActiveRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "BOOST ONLY",
                                    color = RedCoreTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Quick Action 2: PLAY ONLY (launch without changing display)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    activeGame?.let { onPlayOnly(it.id) }
                                },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = RedCoreSurfaceElevated,
                            border = BorderStroke(1.dp, RedCoreBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = RedCoreTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PLAY DIRECT",
                                    color = RedCoreTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Quick Action 3: RESTORE DEFAULTS
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onRestoreDefault() },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = RedCoreSurfaceElevated,
                            border = BorderStroke(1.dp, RedCoreBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    tint = RedCoreWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "RESTORE STATE",
                                    color = RedCoreTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // 7. REAL OPTIMIZATION RESULT SUMMARY (IF AVAILABLE)
                // =========================================================================
                if (lastResult != null) {
                    item {
                        RedCoreCard(
                            borderColor = if (lastResult.isSuccess) RedCoreSuccess else RedCoreWarning,
                            backgroundColor = RedCoreSurfaceElevated
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LAST HARDWARE APPLIED // VERIFIED",
                                    color = if (lastResult.isSuccess) RedCoreSuccess else RedCoreWarning,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                StatusBadge(
                                    text = if (lastResult.isSuccess) "VERIFIED" else "PARTIAL",
                                    state = if (lastResult.isSuccess) BadgeState.SUCCESS else BadgeState.WARNING
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Target: ${lastResult.requestedRefreshRate.toInt()}Hz • Verified Active: ${lastResult.actualRefreshRate.toInt()}Hz",
                                color = RedCoreTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (lastResult.appliedSettings.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Changes: ${lastResult.appliedSettings.joinToString(", ")}",
                                    color = RedCoreTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Tactical footer
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GAMEBOOST RED CORE • CREATED BY ROHIT B.K",
                            color = RedCoreTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
