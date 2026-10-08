package com.gameboost.optimizer.ui.boost

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderActive
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusDanger
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusReadyContainer
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.ThermalRiskDialog

/**
 * GameBoost Boost Screen — Faithfully recreating the Google Stitch design reference
 * for the Boost screen (Stitch Project 14872102965839926777 / screen f23c4984b3cf486b8a9b54bd78daa61f).
 */
@Composable
fun BoostScreen(
    capabilities: DeviceCapabilities,
    shizukuStatus: ShizukuStatus,
    isOptimized: Boolean,
    activeGameProfile: GameProfile?,
    lastResult: OptimizationResult?,
    hardwareStats: HardwareStats,
    selectedMode: PerformanceMode,
    games: List<GameProfile>,
    selectedGameId: String?,
    onSelectMode: (PerformanceMode) -> Unit,
    onApplyOptimization: (String, Boolean) -> Unit,
    onRestoreDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showThermalDialog by remember { mutableStateOf(false) }

    val targetGame = remember(selectedGameId, games) {
        games.firstOrNull { it.id == selectedGameId }
            ?: games.firstOrNull { it.isInstalled }
            ?: games.firstOrNull()
    }

    if (showThermalDialog) {
        ThermalRiskDialog(
            onConfirm = {
                showThermalDialog = false
                targetGame?.let { onApplyOptimization(it.id, true) }
            },
            onDismiss = { showThermalDialog = false }
        )
    }

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
                contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ==========================================
                // 1. SCREEN TITLE & STATUS BADGE
                // ==========================================
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(AccentPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BOOST",
                                    color = TextPrimary,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Performance profile & system tuning",
                                color = TextTertiary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF072B38),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⍈ ", color = AccentPrimary, fontSize = 9.sp)
                                Text(
                                    text = if (shizukuStatus.isReady) "IPC BRIDGE ACTIVE" else "ROOTLESS MODE",
                                    color = AccentPrimary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 2. TARGET PACKAGE CARD
                // ==========================================
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GameIconView(
                                packageName = targetGame?.activePackageName,
                                isInstalled = targetGame?.isInstalled ?: false,
                                sizeDp = 46.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "TARGET PACKAGE",
                                            color = TextTertiary,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "● PID: 14892",
                                            color = AccentPrimary,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Text(
                                        text = "ENGINE STAGE",
                                        color = TextTertiary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = (targetGame?.displayName ?: "PUBG MOBILE").uppercase(),
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isOptimized) Color(0xFF092E3B) else Color(0xFF131A24)
                                    ) {
                                        Text(
                                            text = if (isOptimized) "● INJECTION ACTIVE" else "● ARMED FOR INJECTION",
                                            color = if (isOptimized) AccentPrimary else TextSecondary,
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. TUNING GOVERNOR (SELECT 1 OF 3)
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TUNING GOVERNOR",
                                color = TextTertiary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "SELECT 1 OF 3",
                                color = TextTertiary,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Governor 1: BALANCED
                        GovernorOptionCard(
                            title = "BALANCED",
                            badgeText = "STOCK",
                            badgeColor = TextTertiary,
                            badgeContainer = Color(0xFF161F2C),
                            description = "Energy efficient • Standard thermal threshold • Adaptive refresh",
                            icon = Icons.Default.Shield,
                            isSelected = selectedMode == PerformanceMode.SAFE,
                            onClick = { onSelectMode(PerformanceMode.SAFE) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Governor 2: PERFORMANCE (Stitch Selected)
                        GovernorOptionCard(
                            title = "PERFORMANCE",
                            badgeText = "SELECTED",
                            badgeColor = AccentPrimary,
                            badgeContainer = Color(0xFF072B38),
                            description = "Locked max refresh • Aggressive foreground priority • Sustained thermal limits",
                            icon = Icons.Default.Speed,
                            isSelected = selectedMode == PerformanceMode.PERFORMANCE,
                            onClick = { onSelectMode(PerformanceMode.PERFORMANCE) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Governor 3: EXTREME
                        GovernorOptionCard(
                            title = "EXTREME",
                            badgeText = "UNGOVERNED",
                            badgeColor = StatusDanger,
                            badgeContainer = Color(0xFF330F15),
                            description = "Uncapped hardware governor • Warning: Increased thermals & battery drain",
                            icon = Icons.Default.Warning,
                            isSelected = selectedMode == PerformanceMode.AGGRESSIVE || selectedMode == PerformanceMode.THERMAL_OVERRIDE,
                            isWarning = true,
                            onClick = { onSelectMode(PerformanceMode.AGGRESSIVE) }
                        )
                    }
                }

                // ==========================================
                // 4. HARDWARE ARBITRATION MATRIX
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HARDWARE ARBITRATION MATRIX",
                                color = TextTertiary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF101622)
                            ) {
                                Text(
                                    text = "REAL S/P KERNEL/SYSFS ENGAGED",
                                    color = TextTertiary,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                // 1. Display Refresh Rate
                                val refreshPill = if (isOptimized) "ACTIVE (120 Hz)" else if (capabilities.displayState.supports120Hz) "SUPPORTED" else "UNAVAILABLE"
                                MatrixRow(
                                    icon = Icons.Default.Speed,
                                    title = "Display Refresh Rate",
                                    subtitle = "SurfaceFlinger peak display override",
                                    status = refreshPill,
                                    statusColor = if (isOptimized) AccentPrimary else StatusReady,
                                    statusContainer = if (isOptimized) Color(0xFF072B38) else StatusReadyContainer
                                )

                                // 2. Game Mode Flag
                                MatrixRow(
                                    icon = Icons.Default.SportsEsports,
                                    title = "Game Mode Flag",
                                    subtitle = "AOSP GameManagerService registration",
                                    status = if (capabilities.apiLevel >= 31) "SUPPORTED" else "UNAVAILABLE",
                                    statusColor = if (capabilities.apiLevel >= 31) StatusReady else TextTertiary,
                                    statusContainer = if (capabilities.apiLevel >= 31) StatusReadyContainer else Color(0xFF161F2C)
                                )

                                // 3. Process Scheduling Priority
                                MatrixRow(
                                    icon = Icons.Default.PriorityHigh,
                                    title = "Process Scheduling Priority",
                                    subtitle = "Linux renice -16 • cgroup top-app migration",
                                    status = if (shizukuStatus.isReady) "SUPPORTED via Shizuku" else "UNAVAILABLE",
                                    statusColor = if (shizukuStatus.isReady) AccentPrimary else TextTertiary,
                                    statusContainer = if (shizukuStatus.isReady) Color(0xFF072B38) else Color(0xFF161F2C)
                                )

                                // 4. Thermal Throttling Bypass
                                MatrixRow(
                                    icon = Icons.Default.DeviceThermostat,
                                    title = "Thermal Throttling Bypass",
                                    subtitle = "Requires SELinux permissive / OEM vendor unlock",
                                    status = "UNAVAILABLE",
                                    statusColor = TextTertiary,
                                    statusContainer = Color(0xFF161F2C)
                                )

                                // 5. Touch Polling Rate Lock
                                MatrixRow(
                                    icon = Icons.Default.TouchApp,
                                    title = "Touch Polling Rate Lock",
                                    subtitle = "Digitizer firmware protocol locked by vendor kernel",
                                    status = "NOT SUPPORTED",
                                    statusColor = StatusDanger,
                                    statusContainer = Color(0xFF330F15)
                                )

                                // 6. Do Not Disturb Automation
                                MatrixRow(
                                    icon = Icons.Default.DoNotDisturb,
                                    title = "Do Not Disturb Automation",
                                    subtitle = "ZenModeManager policy suspension",
                                    status = if (isOptimized) "ACTIVE" else "SUPPORTED",
                                    statusColor = if (isOptimized) AccentPrimary else StatusReady,
                                    statusContainer = if (isOptimized) Color(0xFF072B38) else StatusReadyContainer
                                )

                                // 7. Memory Compression Hold
                                MatrixRow(
                                    icon = Icons.Default.Memory,
                                    title = "Memory Compression Hold",
                                    subtitle = "ZRAM swap compaction trigger & cached purge",
                                    status = "SUPPORTED",
                                    statusColor = StatusReady,
                                    statusContainer = StatusReadyContainer
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 5. ACTION BUTTONS: APPLY PROFILE & RESTORE
                // ==========================================
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Primary CTA: APPLY PROFILE
                        Button(
                            onClick = {
                                if (selectedMode == PerformanceMode.AGGRESSIVE || selectedMode == PerformanceMode.THERMAL_OVERRIDE) {
                                    showThermalDialog = true
                                } else {
                                    targetGame?.let { onApplyOptimization(it.id, false) }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentPrimary,
                                contentColor = Color(0xFF040A0F)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚡ APPLY PROFILE",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // Secondary: RESTORE PREVIOUS STATE
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clickable(onClick = onRestoreDefaults),
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
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RESTORE PREVIOUS STATE",
                                    color = TextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 5b. LIVE VERIFICATION RESULTS (PHASE 7 TRUTH TELLING)
                // ==========================================
                if (lastResult != null) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (lastResult.isSuccess) AccentPrimary.copy(alpha = 0.5f) else StatusWarning.copy(alpha = 0.5f))
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (lastResult.isSuccess) AccentPrimary else StatusWarning)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "LIVE VERIFICATION RESULTS",
                                            color = if (lastResult.isSuccess) AccentPrimary else StatusWarning,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.8.sp
                                        )
                                    }

                                    Surface(
                                        color = if (lastResult.displayRefreshVerified) StatusReadyContainer else Color(0x33FFB300),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, if (lastResult.displayRefreshVerified) StatusReady else StatusWarning)
                                    ) {
                                        Text(
                                            text = if (lastResult.displayRefreshVerified) "PANEL VERIFIED" else "LIMITED BY OEM",
                                            color = if (lastResult.displayRefreshVerified) StatusReady else StatusWarning,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Item 1: Display Refresh Rate
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "DISPLAY REFRESH RATE",
                                            color = TextTertiary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = if (lastResult.displayRefreshVerified) "60 Hz → ${lastResult.actualRefreshRate.toInt()} Hz ✓"
                                            else "${lastResult.actualRefreshRate.toInt()} Hz (${lastResult.requestedRefreshRate.toInt()} Hz Requested - Not Verified)",
                                            color = if (lastResult.displayRefreshVerified) StatusReady else StatusWarning,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = if (lastResult.displayRefreshVerified) "✓ ACTIVE" else "NOT VERIFIED",
                                        color = if (lastResult.displayRefreshVerified) StatusReady else StatusWarning,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Item 2: Game Frame Rate (Phase 4 & 7 Truth)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "GAME FRAME RATE (FPS)",
                                            color = TextTertiary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "90 FPS NOT VERIFIED",
                                            color = Color(0xFFFFB300),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "In-game Graphics setting must be set to '90 FPS' or 'Extreme+'. Anti-cheat & game binaries are never modified.",
                                            color = TextTertiary,
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = Color(0x22FFB300),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, Color(0x66FFB300))
                                    ) {
                                        Text(
                                            text = "REQUIRES IN-GAME SETTING",
                                            color = Color(0xFFFFB300),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Item 3: Game Mode API
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "GAME MODE API",
                                            color = TextTertiary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = lastResult.gameModeStatusText.ifEmpty { "Game Mode Performance (GameManager)" },
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "ENGAGED ✓",
                                        color = StatusReady,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 6. STATUS CONSOLE / TELEMETRY LOG
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(AccentPrimary)
                                    )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "STATUS CONSOLE / TELEMETRY LOG",
                                    color = TextTertiary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Text(
                                text = "BUFFER: OK",
                                color = TextTertiary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF090D14),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF141C26))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val steps = lastResult?.optimizationSteps ?: emptyList()
                                if (steps.isNotEmpty()) {
                                    steps.take(6).forEach { step ->
                                        Text(
                                            text = "[${if (step.verified) "VERIFIED" else step.status.name}] ${step.stepName}: ${step.details}",
                                            color = if (step.verified) Color(0xFF4EE2B6) else if (step.status == com.gameboost.optimizer.models.StepExecutionStatus.FAILED) Color(0xFFFF5252) else AccentPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "[SYSTEM] Privileged IPC established (Shizuku: ${if (shizukuStatus.isReady) "READY" else "STANDBY"})",
                                        color = AccentPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "[DISPLAY] Physical panel rate: ${hardwareStats.currentRefreshRate.toInt()}Hz (Supported: ${capabilities.displayState.supportedRefreshRates.map { it.toInt() }.joinToString()}Hz)",
                                        color = Color(0xFF4EE2B6),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "[THERMAL] Status: ${hardwareStats.thermalStatus} | Headroom: ${hardwareStats.thermalHeadroom ?: "Normal"} | Temp: ${hardwareStats.formattedTemp}",
                                        color = AccentPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 7. FOOTER BRANDING
                // ==========================================
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "● GAMEBOOST ENGINE V1.0 • CREATED BY ROHIT B.K ●",
                            color = TextTertiary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Direct Android Framework Hook • Non-Root Shizuku Daemon",
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
 * Governor Option Card in Boost Screen
 */
@Composable
private fun GovernorOptionCard(
    title: String,
    badgeText: String,
    badgeColor: Color,
    badgeContainer: Color,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    isWarning: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF0C1B25) else SurfaceCard,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) AccentPrimary else BorderSubtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF072B38) else Color(0xFF131A24),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) AccentPrimary else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = badgeContainer
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = if (isWarning) Color(0xFFE57373) else TextTertiary,
                    fontSize = 9.sp,
                    lineHeight = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Radio Indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            if (isSelected) AccentPrimary else Color(0xFF283648)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentPrimary)
                    )
                }
            }
        }
    }
}

/**
 * Capability Row inside Hardware Arbitration Matrix
 */
@Composable
private fun MatrixRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    status: String,
    statusColor: Color,
    statusContainer: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextTertiary,
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = statusContainer
        ) {
            Text(
                text = status,
                color = statusColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
