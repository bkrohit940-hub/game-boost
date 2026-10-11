package com.gameboost.optimizer.ui.boost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.DeepRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreAngularShape
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDanger
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.CapabilityRow
import com.gameboost.optimizer.ui.components.CapabilityStatus
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.PrimaryBoostButton
import com.gameboost.optimizer.ui.components.RedCoreCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge
import com.gameboost.optimizer.ui.components.ThermalRiskDialog

/**
 * BOOST: PERFORMANCE CONTROL (Section 7)
 * RedMagic gaming hardware control panel aesthetic:
 * 1. Three distinct performance profiles:
 *    - BALANCED: Reliable everyday gaming
 *    - PERFORMANCE: Prioritize supported gaming performance controls
 *    - EXTREME: Apply strongest available safe & supported settings
 * 2. Actual optimization capabilities matrix:
 *    - Refresh Rate
 *    - Android Game Mode
 *    - Background Workload
 *    - Thermal Monitoring
 *    - Process Priority
 *    - Backend Connection
 *    Real statuses: ACTIVE, AVAILABLE, FAILED, UNAVAILABLE, NOT SUPPORTED
 * 3. APPLY PROFILE & RESTORE PREVIOUS STATE
 * 4. Auto-Revert status & Optimization result summary
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
        color = RedCoreBgPrimary
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrowScreen = maxWidth < 360.dp
            val horizontalPadding = if (isNarrowScreen) 10.dp else 14.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // =========================================================================
                // 1. SCREEN TITLE & BACKEND STATUS
                // =========================================================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 14.dp)
                                        .background(PrimaryGamingRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PERFORMANCE CONTROL",
                                    color = RedCoreTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "HARDWARE TUNING & SUBSYSTEM ARBITRATION",
                                color = RedCoreTextSecondary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        StatusBadge(
                            text = if (shizukuStatus.isReady) "PRIVILEGED BINDER" else "ROOTLESS ACCESS",
                            state = if (shizukuStatus.isReady) BadgeState.SUCCESS else BadgeState.WARNING
                        )
                    }
                }

                // =========================================================================
                // 2. TARGET GAME STATUS CARD
                // =========================================================================
                item {
                    RedCoreCard(
                        backgroundColor = RedCoreSurfaceElevated,
                        borderColor = if (isOptimized) RedCoreSuccess else RedCoreBorderActive
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GameIconView(
                                packageName = targetGame?.activePackageName,
                                isInstalled = targetGame?.isInstalled ?: false,
                                sizeDp = 48.dp,
                                isActive = isOptimized
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = (targetGame?.displayName ?: "PUBG MOBILE").uppercase(),
                                        color = RedCoreTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatusBadge(
                                        text = if (isOptimized) "ACTIVE BOOST" else "READY",
                                        state = if (isOptimized) BadgeState.SUCCESS else BadgeState.INFO
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = targetGame?.activePackageName ?: "com.tencent.ig",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // 3. THREE DISTINCT PERFORMANCE PROFILES (SECTION 7)
                // BALANCED, PERFORMANCE, EXTREME
                // =========================================================================
                item {
                    SectionHeader(
                        title = "PERFORMANCE PROFILES",
                        subtitle = "Select hardware governor & execution intensity"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Profile 1: BALANCED
                        ProfileSelectionPanel(
                            title = "BALANCED",
                            badgeText = "ENERGY OPTIMAL",
                            badgeState = BadgeState.NEUTRAL,
                            description = "Reliable everyday gaming. Optimal thermals, battery longevity, and balanced touch response.",
                            icon = Icons.Default.Security,
                            isSelected = selectedMode == PerformanceMode.SAFE,
                            onClick = { onSelectMode(PerformanceMode.SAFE) }
                        )

                        // Profile 2: PERFORMANCE
                        ProfileSelectionPanel(
                            title = "PERFORMANCE",
                            badgeText = "RECOMMENDED",
                            badgeState = BadgeState.INFO,
                            description = "Prioritize supported gaming performance controls. Locks peak 120Hz display refresh & Android Game Mode.",
                            icon = Icons.Default.Speed,
                            isSelected = selectedMode == PerformanceMode.PERFORMANCE,
                            onClick = { onSelectMode(PerformanceMode.PERFORMANCE) }
                        )

                        // Profile 3: EXTREME
                        ProfileSelectionPanel(
                            title = "EXTREME",
                            badgeText = "MAX SUSTAINED",
                            badgeState = BadgeState.ERROR,
                            description = "Apply the strongest available safe and supported settings. Background throttle & priority boosting.",
                            icon = Icons.Default.Bolt,
                            isSelected = selectedMode == PerformanceMode.AGGRESSIVE || selectedMode == PerformanceMode.THERMAL_OVERRIDE,
                            isWarning = true,
                            onClick = { onSelectMode(PerformanceMode.AGGRESSIVE) }
                        )
                    }
                }

                // =========================================================================
                // 4. ACTUAL OPTIMIZATION CAPABILITIES MATRIX (SECTION 7)
                // Refresh Rate, Android Game Mode, Background Workload, Thermal Monitoring, Process Priority, Backend Connection
                // =========================================================================
                item {
                    SectionHeader(
                        title = "HARDWARE CAPABILITY MATRIX",
                        subtitle = "Verified system controls & arbitration status"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    RedCoreCard(
                        backgroundColor = RedCoreSurfaceElevated,
                        borderColor = RedCoreBorder
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // 1. Refresh Rate Control
                            val refreshStatus = when {
                                isOptimized -> CapabilityStatus.ACTIVE
                                capabilities.displayState.supports120Hz || capabilities.displayState.supports90Hz -> CapabilityStatus.SUPPORTED
                                else -> CapabilityStatus.UNAVAILABLE
                            }
                            CapabilityRow(
                                title = "Display Refresh Rate Override",
                                status = refreshStatus,
                                subtitle = "SurfaceFlinger peak display refresh lock (${hardwareStats.currentRefreshRate.toInt()}Hz active)"
                            )

                            HorizontalDivider(color = RedCoreBorderSubtle, thickness = 1.dp)

                            // 2. Android Game Mode
                            val gameModeStatus = when {
                                capabilities.apiLevel >= 31 && shizukuStatus.isReady -> CapabilityStatus.SUPPORTED
                                capabilities.apiLevel >= 31 -> CapabilityStatus.AVAILABLE
                                else -> CapabilityStatus.NOT_SUPPORTED
                            }
                            CapabilityRow(
                                title = "Android Game Mode Service",
                                status = gameModeStatus,
                                subtitle = "AOSP GameManager registration (API 31+)"
                            )

                            HorizontalDivider(color = RedCoreBorderSubtle, thickness = 1.dp)

                            // 3. Background Workload Throttling
                            val bgStatus = if (shizukuStatus.isReady) CapabilityStatus.SUPPORTED else CapabilityStatus.UNAVAILABLE
                            CapabilityRow(
                                title = "Background Workload Throttling",
                                status = bgStatus,
                                subtitle = "Restriction of non-gaming background CPU usage"
                            )

                            HorizontalDivider(color = RedCoreBorderSubtle, thickness = 1.dp)

                            // 4. Thermal Monitoring
                            val thermalStatus = if (hardwareStats.batteryTemperatureCelsius > 0) CapabilityStatus.ACTIVE else CapabilityStatus.AVAILABLE
                            CapabilityRow(
                                title = "Hardware Thermal Telemetry",
                                status = thermalStatus,
                                subtitle = "Real-time battery/chassis temperature monitoring (${hardwareStats.formattedTemp})"
                            )

                            HorizontalDivider(color = RedCoreBorderSubtle, thickness = 1.dp)

                            // 5. Process Scheduling Priority
                            val priorityStatus = if (shizukuStatus.isReady) CapabilityStatus.SUPPORTED else CapabilityStatus.UNAVAILABLE
                            CapabilityRow(
                                title = "Process Scheduling Priority",
                                status = priorityStatus,
                                subtitle = "Linux renice -16 • foreground priority boosting"
                            )

                            HorizontalDivider(color = RedCoreBorderSubtle, thickness = 1.dp)

                            // 6. Backend Connection
                            val backendStatus = if (shizukuStatus.isReady) CapabilityStatus.ACTIVE else CapabilityStatus.UNAVAILABLE
                            CapabilityRow(
                                title = "Privileged Binder Execution",
                                status = backendStatus,
                                subtitle = "Shizuku privileged IPC shell connection"
                            )
                        }
                    }
                }

                // =========================================================================
                // 5. AUTO-REVERT STATUS & OPTIMIZATION ACTION BUTTONS
                // =========================================================================
                item {
                    // Auto-Revert Information Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        color = RedCoreBgSecondary,
                        border = BorderStroke(1.dp, RedCoreBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(RedCoreSuccess, CutCornerShape(1.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AUTO-REVERT ON GAME EXIT",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            StatusBadge(text = "ENABLED", state = BadgeState.SUCCESS)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Main Action: APPLY PROFILE
                    PrimaryBoostButton(
                        onClick = {
                            if (targetGame != null) {
                                if (selectedMode == PerformanceMode.THERMAL_OVERRIDE) {
                                    showThermalDialog = true
                                } else {
                                    onApplyOptimization(targetGame.id, false)
                                }
                            }
                        },
                        enabled = targetGame != null && targetGame.isInstalled,
                        text = "APPLY PROFILE",
                        subtext = "COMMIT HARDWARE TUNING SETTINGS",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary Action: RESTORE PREVIOUS STATE
                    OutlinedButton(
                        onClick = onRestoreDefaults,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        border = BorderStroke(1.dp, RedCoreBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = RedCoreTextSecondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = RedCoreWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RESTORE PREVIOUS STATE",
                            color = RedCoreTextPrimary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // =========================================================================
                // 6. OPTIMIZATION RESULT SUMMARY
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
                                    text = "OPTIMIZATION SUMMARY // VERIFIED",
                                    color = if (lastResult.isSuccess) RedCoreSuccess else RedCoreWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                StatusBadge(
                                    text = if (lastResult.isSuccess) "VERIFIED SUCCESS" else "FAILED",
                                    state = if (lastResult.isSuccess) BadgeState.SUCCESS else BadgeState.ERROR
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Verified Display: ${lastResult.actualRefreshRate.toInt()}Hz • Status: ${lastResult.statusMessage}",
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
            }
        }
    }
}

@Composable
private fun ProfileSelectionPanel(
    title: String,
    badgeText: String,
    badgeState: BadgeState,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    isWarning: Boolean = false,
    onClick: () -> Unit
) {
    val animatedBorder by animateColorAsState(
        targetValue = if (isSelected) BrightActiveRed else RedCoreBorder,
        animationSpec = tween(180),
        label = "panelBorder"
    )

    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) RedCoreDarkCrimson else RedCoreSurfaceElevated,
        animationSpec = tween(180),
        label = "panelBg"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        color = animatedBg,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, animatedBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) BrightActiveRed else if (isWarning) RedCoreDanger else RedCoreTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = if (isSelected) BrightActiveRed else RedCoreTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }

                StatusBadge(text = badgeText, state = if (isSelected) BadgeState.INFO else badgeState)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = if (isSelected) RedCoreTextPrimary else RedCoreTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
