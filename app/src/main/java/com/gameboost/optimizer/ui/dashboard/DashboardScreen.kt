package com.gameboost.optimizer.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.AccentPrimaryContainer
import com.gameboost.optimizer.theme.BorderActive
import com.gameboost.optimizer.theme.BorderHighlight
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusDanger
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusReadyContainer
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceCardActive
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameBoostLogo
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.StatusBadge
import com.gameboost.optimizer.ui.components.ThermalRiskDialog
import java.util.Locale

/**
 * GameBoost Home Screen — Faithfully recreating the Google Stitch design reference
 * for the Home screen (Stitch Project 14872102965839926777 / screen b40a930d359b40fd936a5495a9f07022).
 *
 * Visual hierarchy:
 * 1. Top Bar: Tactical Logo + Brand Title + Status Pill badge (SHIZUKU ACTIVE) + Profile action
 * 2. Tactical Sub-Status Bar: Real status strip (Shizuku, Wireless ADB, Battery %, Temp)
 * 3. Hero Core Profile Card: Target game, Target FPS, Governor, Touch Accel, BOOST & PLAY primary CTA, OPEN RAW, TUNE
 * 4. Real-Time Telemetry Matrix: 2x2 grid (Refresh Rate, Backend Core, RAM Allocation, Thermal CPU/GPU)
 * 5. Library Quick Select: Horizontal snap carousel of supported titles with active slot marker
 * 6. Tactical Footer Branding: Creator credit & engine status
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

    // Carousel snap state
    val carouselListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = carouselListState)

    LaunchedEffect(activeGame?.id) {
        val index = games.indexOfFirst { it.id == activeGame?.id }
        if (index >= 0) {
            carouselListState.animateScrollToItem(index)
        }
    }

    // Backend state resolution
    val backendType = privilegedState?.activeBackendType
        ?: if (shizukuStatus.isReady) BackendType.SHIZUKU else BackendType.NONE
    val isPrivilegedReady = privilegedState?.isPrivilegedReady ?: shizukuStatus.isReady

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBg
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrowScreen = maxWidth < 360.dp
            val horizontalPadding = if (isNarrowScreen) 12.dp else 16.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ==========================================
                // 1. TOP BAR: LOGO + TITLE + STATUS PILL + PROFILE
                // ==========================================
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Logo + Branding
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GameBoostLogo(sizeDp = 38.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GAMEBOOST",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "TACTICAL ENGINE CORE",
                                    color = TextTertiary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Right: Status Pill & Profile Action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Stitch Status Pill (e.g. SHIZUKU ACTIVE)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isPrivilegedReady) Color(0xFF062833) else Color(0xFF261D0C),
                                border = BorderStroke(
                                    1.dp,
                                    if (isPrivilegedReady) AccentPrimary else StatusWarning
                                ),
                                modifier = Modifier.clickable {
                                    if (isPrivilegedReady) onOpenDiagnostics() else onFixShizuku()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isPrivilegedReady) AccentPrimary else StatusWarning)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when {
                                            isPrivilegedReady && backendType == BackendType.SHIZUKU -> "SHIZUKU ACTIVE"
                                            isPrivilegedReady && backendType == BackendType.WIRELESS_ADB -> "ADB ACTIVE"
                                            isPrivilegedReady -> "PRIVILEGED"
                                            else -> "UNPRIVILEGED"
                                        },
                                        color = if (isPrivilegedReady) AccentPrimary else StatusWarning,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // Profile / Quick Settings button
                            Surface(
                                shape = CircleShape,
                                color = SurfaceElevated,
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onOpenSettings() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Settings",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 2. TACTICAL SUB-STATUS BAR
                // ==========================================
                item {
                    val shizukuText = when {
                        shizukuStatus.isReady -> "SHIZUKU: CONNECTED"
                        shizukuStatus.isInstalled -> "SHIZUKU: UNAUTHORIZED"
                        else -> "SHIZUKU: UNAVAILABLE"
                    }
                    val shizukuDotColor = if (shizukuStatus.isReady) AccentPrimary else StatusWarning

                    val adbText = when {
                        privilegedState?.activeBackendType == BackendType.WIRELESS_ADB -> "ADB: WIRELESS READY"
                        privilegedState?.wirelessAdbState?.status == com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED -> "ADB: CONNECTED"
                        else -> "ADB: DISCONNECTED"
                    }

                    val batteryText = if (hardwareStats.batteryPercentage > 0) "${hardwareStats.batteryPercentage}%" else "UNAVAILABLE"
                    val tempText = if (hardwareStats.batteryTemperatureCelsius > 0) hardwareStats.formattedTemp else "UNAVAILABLE"

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0C1118),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF161F2C))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Shizuku
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(shizukuDotColor)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = shizukuText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (shizukuStatus.isReady) TextPrimary else StatusWarning,
                                    letterSpacing = 0.3.sp
                                )
                            }

                            Text(text = "|", color = Color(0xFF222D3D), fontSize = 10.sp)

                            // ADB
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⍈ ",
                                    fontSize = 9.sp,
                                    color = if (privilegedState?.activeBackendType == BackendType.WIRELESS_ADB) AccentPrimary else TextTertiary
                                )
                                Text(
                                    text = adbText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 0.3.sp
                                )
                            }

                            Text(text = "|", color = Color(0xFF222D3D), fontSize = 10.sp)

                            // Battery & Thermal
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔋 ", fontSize = 9.sp)
                                Text(
                                    text = "$batteryText • $tempText",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 3. CORE PROFILE CARD (HERO)
                // ==========================================
                item {
                    val targetFps = if (selectedRefreshRate > 0) {
                        selectedRefreshRate.toInt()
                    } else if (hardwareStats.currentRefreshRate > 0) {
                        hardwareStats.currentRefreshRate.toInt()
                    } else {
                        120
                    }

                    val governorName = when (selectedMode) {
                        PerformanceMode.SAFE -> "BALANCED"
                        PerformanceMode.PERFORMANCE -> "PERFORMANCE"
                        PerformanceMode.AGGRESSIVE -> "EXTREME"
                        PerformanceMode.THERMAL_OVERRIDE -> "OVERRIDE"
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isOptimized) BorderActive else BorderSubtle
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Top Row: CORE PROFILE : READY/ACTIVE + SECURE KERNEL Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(AccentPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isOptimized) "CORE PROFILE : ACTIVE" else "CORE PROFILE : READY",
                                        color = AccentPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0F1A24),
                                    border = BorderStroke(1.dp, Color(0xFF1E2E40))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🛡 ", fontSize = 9.sp)
                                        Text(
                                            text = "SECURE KERNEL",
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Target Game Details Row
                            if (activeGame != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Game Icon with 120Hz corner badge
                                    Box(modifier = Modifier.size(56.dp)) {
                                        GameIconView(
                                            packageName = activeGame.activePackageName,
                                            isInstalled = activeGame.isInstalled,
                                            sizeDp = 56.dp
                                        )
                                        // 120Hz pill badge overlay on top right
                                        Surface(
                                            modifier = Modifier.align(Alignment.TopEnd),
                                            shape = RoundedCornerShape(topEnd = 10.dp, bottomStart = 6.dp),
                                            color = AccentPrimary
                                        ) {
                                            Text(
                                                text = "${targetFps}Hz",
                                                color = Color.Black,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Installed pill
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (activeGame.isInstalled) StatusReadyContainer else Color(0xFF1E232B)
                                            ) {
                                                Text(
                                                    text = if (activeGame.isInstalled) "INSTALLED" else "NOT INSTALLED",
                                                    color = if (activeGame.isInstalled) StatusReady else TextTertiary,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }

                                            // Profile mode badge
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = when (selectedMode) {
                                                    PerformanceMode.SAFE -> Color(0xFF16202C)
                                                    PerformanceMode.PERFORMANCE -> Color(0xFF092E3B)
                                                    PerformanceMode.AGGRESSIVE, PerformanceMode.THERMAL_OVERRIDE -> Color(0xFF331217)
                                                }
                                            ) {
                                                Text(
                                                    text = when (selectedMode) {
                                                        PerformanceMode.SAFE -> "BALANCED"
                                                        PerformanceMode.PERFORMANCE -> "PERF ACTIVE"
                                                        PerformanceMode.AGGRESSIVE -> "EXTREME"
                                                        PerformanceMode.THERMAL_OVERRIDE -> "OVERRIDE"
                                                    },
                                                    color = when (selectedMode) {
                                                        PerformanceMode.SAFE -> TextSecondary
                                                        PerformanceMode.PERFORMANCE -> AccentPrimary
                                                        PerformanceMode.AGGRESSIVE, PerformanceMode.THERMAL_OVERRIDE -> StatusDanger
                                                    },
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = activeGame.displayName.uppercase(),
                                            color = TextPrimary,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.5.sp
                                        )

                                        Text(
                                            text = activeGame.activePackageName.uppercase(),
                                            color = TextTertiary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // 3-Column Specifications Row
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF0C1017),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF161F2C))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Target FPS
                                        Column {
                                            Text(
                                                text = "TARGET FPS",
                                                color = TextTertiary,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    text = "$targetFps",
                                                    color = TextPrimary,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "LOCKED",
                                                    color = TextTertiary,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(bottom = 2.dp)
                                                )
                                            }
                                        }

                                        Text(text = "|", color = Color(0xFF1E2838), fontSize = 14.sp)

                                        // Governor
                                        Column {
                                            Text(
                                                text = "GOVERNOR",
                                                color = TextTertiary,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = governorName,
                                                color = AccentPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }

                                        Text(text = "|", color = Color(0xFF1E2838), fontSize = 14.sp)

                                        // Touch Accel
                                        Column {
                                            Text(
                                                text = "TOUCH ACCEL",
                                                color = TextTertiary,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "480 Hz",
                                                color = TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Primary CTA Button: BOOST & PLAY (Stitch Vibrant Cyan)
                                Button(
                                    onClick = {
                                        if (selectedMode == PerformanceMode.THERMAL_OVERRIDE || selectedMode == PerformanceMode.AGGRESSIVE) {
                                            showThermalDialogForAction = "boost_play"
                                        } else {
                                            onBoostAndPlay(activeGame.id, false)
                                        }
                                    },
                                    enabled = activeGame.isInstalled && !isBoosting,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AccentPrimary,
                                        contentColor = Color(0xFF040A0F),
                                        disabledContainerColor = Color(0xFF1C2733),
                                        disabledContentColor = TextTertiary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    if (isBoosting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.Black,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ENGAGING TURBO...",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (activeGame.isInstalled) "⚡ BOOST & PLAY" else "GAME NOT INSTALLED",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Secondary Actions Row: OPEN RAW + TUNE
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // OPEN RAW (Launch directly without boost)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clickable(enabled = activeGame.isInstalled) {
                                                onPlayOnly(activeGame.id)
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceElevated,
                                        border = BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                contentDescription = null,
                                                tint = TextPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "OPEN RAW",
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }

                                    // TUNE Button
                                    Surface(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clickable { onGameClick(activeGame.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceElevated,
                                        border = BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Tune,
                                                contentDescription = "Tune",
                                                tint = AccentPrimary,
                                                modifier = Modifier.size(19.dp)
                                            )
                                        }
                                    }
                                }

                                // If optimized, provide direct End Session button
                                if (isOptimized) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = onRestoreDefault,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF2E3848))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("RESTORE SYSTEM DEFAULTS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. REAL-TIME TELEMETRY MATRIX (2x2 GRID)
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "REAL-TIME TELEMETRY MATRIX",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0F151E),
                                border = BorderStroke(1.dp, Color(0xFF1E2836))
                            ) {
                                Text(
                                    text = "POLL: 500MS",
                                    color = TextTertiary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Values calculation
                        val currentHz = if (hardwareStats.currentRefreshRate > 0) hardwareStats.currentRefreshRate.toInt() else 60
                        val usedGb = if (hardwareStats.ramTotalBytes > 0) {
                            String.format(Locale.US, "%.1f", hardwareStats.ramUsedBytes.toDouble() / (1024 * 1024 * 1024))
                        } else "UNAVAILABLE"
                        val totalGb = if (hardwareStats.ramTotalBytes > 0) {
                            (hardwareStats.ramTotalBytes / (1024 * 1024 * 1024)).toInt().toString()
                        } else ""
                        val ramProgress = if (hardwareStats.ramTotalBytes > 0) {
                            (hardwareStats.ramUsedBytes.toFloat() / hardwareStats.ramTotalBytes).coerceIn(0f, 1f)
                        } else 0f
                        val freeRamPercent = if (hardwareStats.ramTotalBytes > 0) {
                            ((1f - ramProgress) * 100).toInt()
                        } else 0

                        val tempStr = if (hardwareStats.batteryTemperatureCelsius > 0) {
                            String.format(Locale.US, "%.1f", hardwareStats.batteryTemperatureCelsius)
                        } else "UNAVAILABLE"

                        // Row 1: REFRESH RATE & BACKEND CORE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Card 1: REFRESH RATE
                            TelemetryCard(
                                title = "REFRESH RATE",
                                icon = Icons.Default.Speed,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$currentHz",
                                        color = TextPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Hz",
                                        color = TextTertiary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(AccentPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isOptimized) "FORCED PEAK" else "SYSTEM DYNAMIC",
                                        color = AccentPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Card 2: BACKEND CORE
                            TelemetryCard(
                                title = "BACKEND CORE",
                                icon = Icons.Default.Terminal,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isPrivilegedReady) "ACTIVE" else "DISCONNECTED",
                                    color = if (isPrivilegedReady) TextPrimary else StatusWarning,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "v13.5.4 API ${android.os.Build.VERSION.SDK_INT}",
                                    color = TextTertiary,
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🛡 ", fontSize = 8.5.sp)
                                    Text(
                                        text = when {
                                            isPrivilegedReady && backendType == BackendType.SHIZUKU -> "SHIZUKU DAEMON"
                                            isPrivilegedReady && backendType == BackendType.WIRELESS_ADB -> "WIRELESS ADB"
                                            else -> "NOT ATTACHED"
                                        },
                                        color = if (isPrivilegedReady) AccentPrimary else TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Row 2: RAM ALLOCATION & THERMAL CPU/GPU
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Card 3: RAM ALLOCATION
                            TelemetryCard(
                                title = "RAM ALLOCATION",
                                icon = Icons.Default.Memory,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = usedGb,
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    if (totalGb.isNotEmpty()) {
                                        Text(
                                            text = " / $totalGb GB",
                                            color = TextTertiary,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { ramProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.5.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = AccentPrimary,
                                    trackColor = Color(0xFF1E2838)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "OPTIMAL STATE",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$freeRamPercent% FREE",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Card 4: THERMAL CPU/GPU
                            TelemetryCard(
                                title = "THERMAL CPU/GPU",
                                icon = Icons.Default.DeviceThermostat,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = tempStr,
                                        color = AccentPrimary,
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "°C",
                                        color = TextTertiary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${hardwareStats.thermalStatus} HEADROOM",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "94%",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. LIBRARY QUICK SELECT (CAROUSEL)
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val installedCount = games.count { it.isInstalled }

                        // Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⊞ ", color = AccentPrimary, fontSize = 12.sp)
                                Text(
                                    text = "LIBRARY QUICK SELECT",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0F151E),
                                border = BorderStroke(1.dp, Color(0xFF1E2836))
                            ) {
                                Text(
                                    text = "$installedCount INSTALLED",
                                    color = TextSecondary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Carousel row
                        LazyRow(
                            state = carouselListState,
                            flingBehavior = snapFlingBehavior,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(games, key = { it.id }) { game ->
                                val isSelected = activeGame?.id == game.id

                                StitchGameCard(
                                    game = game,
                                    isSelected = isSelected,
                                    onClick = { onSelectGame(game.id) }
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 6. FOOTER: CREATOR CREDIT & ENGINE STATUS
                // ==========================================
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "● GAMEBOOST UTILITY V1.0 • CREATED BY ROHIT B.K ●",
                            color = TextTertiary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "SHIZUKU RUNTIME ENGINE • TACTICAL OVERLAY READY",
                            color = Color(0xFF434E60),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stitch 2x2 Telemetry Card
 */
@Composable
private fun TelemetryCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextTertiary,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

/**
 * Stitch Carousel Game Card
 */
@Composable
private fun StitchGameCard(
    game: GameProfile,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(165.dp)
            .height(175.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) BorderActive else BorderSubtle
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Artwork Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(Color(0xFF141C26)),
                contentAlignment = Alignment.Center
            ) {
                // Game Icon
                GameIconView(
                    packageName = game.activePackageName,
                    isInstalled = game.isInstalled,
                    sizeDp = 44.dp
                )

                // Corner FPS Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = if (isSelected) AccentPrimary else Color(0xFF1C2736)
                ) {
                    Text(
                        text = if (isSelected) "LOCKED" else "90FPS",
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Bottom Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = game.displayName.uppercase(),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${game.region.uppercase()} • 120HZ",
                    color = TextTertiary,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Status
                if (isSelected) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AccentPrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTIVE SLOT",
                            color = AccentPrimary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TAP TO LOAD",
                            color = TextTertiary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "›", color = TextTertiary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
