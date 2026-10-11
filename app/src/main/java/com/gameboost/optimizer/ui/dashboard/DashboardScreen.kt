package com.gameboost.optimizer.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.InstalledAppItem
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.RestorationResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.BackendType
import com.gameboost.optimizer.system.PrivilegedSystemState
import com.gameboost.optimizer.theme.BoboBgPrimary
import com.gameboost.optimizer.theme.BoboCardSurface
import com.gameboost.optimizer.theme.BoboPrimaryAccent
import com.gameboost.optimizer.theme.BoboSecondaryAccent
import com.gameboost.optimizer.theme.BoboTextMuted
import com.gameboost.optimizer.theme.BoboTextPrimary
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
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
import com.gameboost.optimizer.ui.components.ThermalRiskDialog
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class BoboAppCategory {
    ALL, GAMES, FAVORITES, RECENTS
}

/**
 * BOBO LAUNCHER: GAMING COMMAND CENTER (Section 2 & 5)
 * Combines full native Android application launcher capabilities with GameBoost hardware control:
 * 1. BOBO Launcher branding & real-time live clock + date HUD
 * 2. Real telemetry: Battery %, charging status, battery temp, display Hz
 * 3. Featured gaming panel with verified capabilities & profile tag
 * 4. Distinct Display Refresh Rate vs in-game FPS telemetry (truth-telling HUD)
 * 5. Dominant "BOOST & PLAY" control
 * 6. Installed application drawer with real-time search, Grid (4-col) & List modes
 * 7. Quick access to Diagnostics, Settings, and Restore Defaults
 * 8. Creator attribution: "Created by Rohit B.K"
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
    installedApps: List<InstalledAppItem> = emptyList(),
    searchQuery: String = "",
    isGridLayout: Boolean = true,
    favoritePackages: Set<String> = emptySet(),
    recentPackages: List<String> = emptyList(),
    onSearchQueryChange: (String) -> Unit = {},
    onToggleGridLayout: (Boolean) -> Unit = {},
    onToggleFavoriteApp: (String) -> Unit = {},
    onLaunchApp: (String) -> Unit = {},
    onRefreshInstalledApps: () -> Unit = {},
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
    var showAboutDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(BoboAppCategory.ALL) }

    // Live Clock & Date HUD
    val currentTime = produceState(
        initialValue = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    ) {
        while (true) {
            value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1000L)
        }
    }.value

    val currentDate = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date()).uppercase()
    }

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

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GameBoostLogo(sizeDp = 26.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BOBO LAUNCHER",
                        color = BoboPrimaryAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "GameBoost v1.0.5 // BOBO Edition",
                        color = BoboTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Created by Rohit B.K",
                        color = BoboSecondaryAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A modern high-performance gaming phone launcher designed for maximum FPS stability, Shizuku privileged system controls, and instantaneous app launching.",
                        color = RedCoreTextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = CutCornerShape(4.dp),
                        color = RedCoreDarkCrimson,
                        border = BorderStroke(1.dp, BoboPrimaryAccent.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "MODE: In-App Launcher & Optional System Home App",
                            color = BoboTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("CLOSE", color = BoboPrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BoboCardSurface,
            shape = CutCornerShape(8.dp)
        )
    }

    val backendType = privilegedState?.activeBackendType
        ?: if (shizukuStatus.isReady) BackendType.SHIZUKU else BackendType.NONE
    val isPrivilegedReady = privilegedState?.isPrivilegedReady ?: shizukuStatus.isReady

    // Filter apps based on category and search query
    val filteredApps = remember(installedApps, searchQuery, selectedCategory, favoritePackages, recentPackages) {
        installedApps.filter { app ->
            val matchesCategory = when (selectedCategory) {
                BoboAppCategory.ALL -> true
                BoboAppCategory.GAMES -> app.isGame
                BoboAppCategory.FAVORITES -> favoritePackages.contains(app.packageName)
                BoboAppCategory.RECENTS -> recentPackages.contains(app.packageName)
            }
            val matchesSearch = searchQuery.isBlank() ||
                app.label.contains(searchQuery, ignoreCase = true) ||
                app.packageName.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BoboBgPrimary
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrowScreen = maxWidth < 360.dp
            val horizontalPadding = if (isNarrowScreen) 10.dp else 14.dp
            val appColumns = if (isNarrowScreen) 3 else 4

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // =========================================================================
                // 1. TOP HEADER: BOBO LAUNCHER BRANDING + BACKEND PILL + SETTINGS
                // =========================================================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Logo + BOBO Branding
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showAboutDialog = true }
                        ) {
                            GameBoostLogo(sizeDp = 34.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "BOBO",
                                        color = BoboPrimaryAccent,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LAUNCHER",
                                        color = BoboTextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp
                                    )
                                }
                                Text(
                                    text = "GAMING COMMAND CENTER // v1.0.5",
                                    color = BoboTextMuted,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        // Right: Status Pill & Settings
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

                            // About shortcut
                            Surface(
                                shape = CutCornerShape(4.dp),
                                color = RedCoreSurfaceElevated,
                                border = BorderStroke(1.dp, RedCoreBorder),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable { showAboutDialog = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "About",
                                        tint = RedCoreTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

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
                                        tint = BoboTextPrimary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 2. LIVE CLOCK & SYSTEM TELEMETRY HUD BAR
                // Real time, date, battery %, charging state, and display refresh rate
                // =========================================================================
                item {
                    val currentHz = if (hardwareStats.currentRefreshRate > 0) "${hardwareStats.currentRefreshRate.toInt()} Hz" else "60 Hz"
                    val batteryIcon = if (hardwareStats.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull

                    Surface(
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        color = BoboCardSurface,
                        border = BorderStroke(1.dp, RedCoreBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Digital Clock & Date
                            Column {
                                Text(
                                    text = currentTime,
                                    color = BoboTextPrimary,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = currentDate,
                                    color = BoboTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            // Right: Battery & Refresh Rate HUD Pills
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Battery pill
                                Surface(
                                    shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                                    color = RedCoreBgSecondary,
                                    border = BorderStroke(1.dp, RedCoreBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = batteryIcon,
                                            contentDescription = null,
                                            tint = if (hardwareStats.batteryPercentage > 20) RedCoreSuccess else BoboPrimaryAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${hardwareStats.batteryPercentage}%",
                                            color = BoboTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (hardwareStats.batteryTemperatureCelsius > 0) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "• ${hardwareStats.formattedTemp}",
                                                color = BoboTextMuted,
                                                fontSize = 9.5.sp
                                            )
                                        }
                                    }
                                }

                                // Refresh rate pill
                                Surface(
                                    shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                                    color = RedCoreDarkCrimson,
                                    border = BorderStroke(1.dp, BoboPrimaryAccent)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = BoboSecondaryAccent,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentHz,
                                            color = BoboSecondaryAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 3. DEDICATED GAMING COMMAND CENTER
                // Featured game panel with angular frame, profile selector, and BOOST button
                // =========================================================================
                item {
                    if (activeGame != null) {
                        val isGameInstalled = activeGame.isInstalled
                        val isSessionActive = isOptimized && activeGameProfile?.id == activeGame.id

                        RedCoreCard(
                            modifier = Modifier.fillMaxWidth(),
                            isActive = isSessionActive || true,
                            borderColor = if (isSessionActive) RedCoreSuccess else BoboPrimaryAccent,
                            backgroundColor = BoboCardSurface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 3.dp, height = 11.dp)
                                            .background(BoboPrimaryAccent)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GAMING COMMAND // ACTIVE TARGET",
                                        color = BoboTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                StatusBadge(
                                    text = if (isSessionActive) "ACTIVE BOOST" else if (isGameInstalled) "INSTALLED" else "SUPPORTED",
                                    state = if (isSessionActive || isGameInstalled) BadgeState.SUCCESS else BadgeState.NEUTRAL
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Main Game Details Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GameIconView(
                                    packageName = activeGame.activePackageName,
                                    isInstalled = isGameInstalled,
                                    sizeDp = 60.dp,
                                    isActive = true
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeGame.displayName,
                                        color = BoboTextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${activeGame.region} • ${activeGame.activePackageName}",
                                        color = BoboTextMuted,
                                        fontSize = 10.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                                            color = RedCoreDarkCrimson,
                                            border = BorderStroke(1.dp, BoboPrimaryAccent.copy(alpha = 0.6f))
                                        ) {
                                            Text(
                                                text = selectedMode.name,
                                                color = BoboSecondaryAccent,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

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
                                                color = BoboTextPrimary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Quick Carousel of Detected Games
                            if (games.size > 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(games) { gameItem ->
                                        val isCurrentGame = gameItem.id == activeGame.id
                                        Surface(
                                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                            color = if (isCurrentGame) RedCoreDarkCrimson else RedCoreBgSecondary,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isCurrentGame) BoboPrimaryAccent else RedCoreBorder
                                            ),
                                            modifier = Modifier.clickable {
                                                onSelectGame(gameItem.id)
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                GameIconView(
                                                    packageName = gameItem.activePackageName,
                                                    isInstalled = gameItem.isInstalled,
                                                    sizeDp = 18.dp
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = gameItem.name,
                                                    color = if (isCurrentGame) BoboSecondaryAccent else RedCoreTextSecondary,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 4. MAIN BOOST & PLAY CONTROL (SECTION 4 & 5)
                // Dominant Red Button with pulsing ready glow & light sweep
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
                            subtext = "OPTIMIZE GAMING SESSION & LAUNCH",
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!canBoost) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Select an installed game below to enable full hardware optimization.",
                                color = RedCoreWarning,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                // =========================================================================
                // 5. DEDICATED DISPLAY & FPS PANEL (SECTION 8)
                // Separates Display Refresh Rate from in-game FPS
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
                // 6. BOBO LAUNCHER APPLICATION DRAWER (SECTION 2 & 3)
                // Search bar, category filters, Grid/List view toggle, and launchable apps
                // =========================================================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "APPLICATIONS // BOBO LAUNCHER",
                            subtitle = "${filteredApps.size} apps accessible • tap to launch"
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Search Bar & Grid/List Toggle Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Search Text Input
                            Surface(
                                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                color = BoboCardSurface,
                                border = BorderStroke(1.dp, RedCoreBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = BoboSecondaryAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = onSearchQueryChange,
                                        textStyle = TextStyle(
                                            color = BoboTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        cursorBrush = SolidColor(BoboPrimaryAccent),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        decorationBox = { innerTextField ->
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = "Search installed applications...",
                                                    color = BoboTextMuted,
                                                    fontSize = 11.5.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                    if (searchQuery.isNotEmpty()) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = BoboTextMuted,
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clickable { onSearchQueryChange("") }
                                        )
                                    }
                                }
                            }

                            // Grid / List Toggle Button
                            Surface(
                                shape = CutCornerShape(4.dp),
                                color = BoboCardSurface,
                                border = BorderStroke(1.dp, if (isGridLayout) BoboPrimaryAccent else RedCoreBorder),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable { onToggleGridLayout(!isGridLayout) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isGridLayout) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                                        contentDescription = "Toggle View",
                                        tint = if (isGridLayout) BoboSecondaryAccent else RedCoreTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Filter Tabs (ALL, GAMES, FAVORITES, RECENTS)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BoboAppCategory.entries.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                val count = when (cat) {
                                    BoboAppCategory.ALL -> installedApps.size
                                    BoboAppCategory.GAMES -> installedApps.count { it.isGame }
                                    BoboAppCategory.FAVORITES -> favoritePackages.size
                                    BoboAppCategory.RECENTS -> recentPackages.size
                                }

                                Surface(
                                    shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                                    color = if (isSelected) RedCoreDarkCrimson else RedCoreBgSecondary,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) BoboPrimaryAccent else RedCoreBorderSubtle
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategory = cat }
                                ) {
                                    Text(
                                        text = "${cat.name} ($count)",
                                        color = if (isSelected) BoboSecondaryAccent else BoboTextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // App Items Rendering (Grid vs List)
                if (filteredApps.isEmpty()) {
                    item {
                        Surface(
                            shape = CutCornerShape(6.dp),
                            color = BoboCardSurface,
                            border = BorderStroke(1.dp, RedCoreBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = BoboTextMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "NO MATCHING APPLICATIONS" else "NO APPS IN THIS CATEGORY",
                                    color = BoboTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "Try searching with a different application name" else "Mark favorites with the star icon to pin apps here",
                                    color = BoboTextMuted,
                                    fontSize = 9.5.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else if (isGridLayout) {
                    // Responsive Grid inside LazyColumn (chunked rows)
                    val appRows = filteredApps.chunked(appColumns)
                    items(appRows) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { appItem ->
                                BoboAppGridItem(
                                    app = appItem,
                                    isFavorite = favoritePackages.contains(appItem.packageName),
                                    onClick = { onLaunchApp(appItem.packageName) },
                                    onToggleFavorite = { onToggleFavoriteApp(appItem.packageName) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining spaces in row
                            repeat(appColumns - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    // List View
                    items(filteredApps) { appItem ->
                        BoboAppListItem(
                            app = appItem,
                            isFavorite = favoritePackages.contains(appItem.packageName),
                            onClick = { onLaunchApp(appItem.packageName) },
                            onToggleFavorite = { onToggleFavoriteApp(appItem.packageName) }
                        )
                    }
                }

                // =========================================================================
                // 7. QUICK HARDWARE ACTIONS
                // Boost Only, Play Direct, Restore State
                // =========================================================================
                item {
                    SectionHeader(
                        title = "QUICK ACTIONS",
                        subtitle = "Hardware arbitration & session control"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    activeGame?.let { onBoostOnly(it.id, selectedMode == PerformanceMode.THERMAL_OVERRIDE) }
                                },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = BoboCardSurface,
                            border = BorderStroke(1.dp, RedCoreBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = BoboSecondaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "BOOST ONLY",
                                    color = BoboTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    activeGame?.let { onPlayOnly(it.id) }
                                },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = BoboCardSurface,
                            border = BorderStroke(1.dp, RedCoreBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = BoboTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PLAY DIRECT",
                                    color = BoboTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onRestoreDefault() },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = BoboCardSurface,
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
                                    color = BoboTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // 8. FOOTER: ATTRIBUTION
                // =========================================================================
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOBO LAUNCHER // CREATED BY ROHIT B.K",
                            color = BoboTextMuted,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoboAppGridItem(
    app: InstalledAppItem,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp))
            .clickable(onClick = onClick),
        color = BoboCardSurface,
        border = BorderStroke(
            1.dp,
            if (app.isGame) BoboPrimaryAccent.copy(alpha = 0.5f) else RedCoreBorderSubtle
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                GameIconView(
                    packageName = app.packageName,
                    isInstalled = true,
                    sizeDp = 42.dp,
                    isActive = app.isGame
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(18.dp)
                        .offset(x = 6.dp, y = (-4).dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFB020) else RedCoreTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = app.label,
                color = BoboTextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (app.isGame) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "GAME",
                    color = BoboSecondaryAccent,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun BoboAppListItem(
    app: InstalledAppItem,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp))
            .clickable(onClick = onClick),
        color = BoboCardSurface,
        border = BorderStroke(
            1.dp,
            if (app.isGame) BoboPrimaryAccent.copy(alpha = 0.4f) else RedCoreBorderSubtle
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                GameIconView(
                    packageName = app.packageName,
                    isInstalled = true,
                    sizeDp = 36.dp,
                    isActive = app.isGame
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.label,
                            color = BoboTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (app.isGame) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CutCornerShape(topStart = 2.dp, bottomEnd = 2.dp),
                                color = RedCoreDarkCrimson,
                                border = BorderStroke(0.5.dp, BoboPrimaryAccent)
                            ) {
                                Text(
                                    text = "GAME",
                                    color = BoboSecondaryAccent,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${app.packageName} ${if (!app.versionName.isNullOrBlank()) "• v${app.versionName}" else ""}",
                        color = BoboTextMuted,
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFB020) else RedCoreTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
                    color = BoboPrimaryAccent,
                    modifier = Modifier.clickable(onClick = onClick)
                ) {
                    Text(
                        text = "OPEN",
                        color = BoboTextPrimary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
