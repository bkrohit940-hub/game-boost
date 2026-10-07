package com.gameboost.optimizer.ui.boost

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.CapabilityRow
import com.gameboost.optimizer.ui.components.CapabilityStatus
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.PerformanceModeSelector
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge
import com.gameboost.optimizer.ui.components.ThermalRiskDialog

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

    if (showThermalDialog) {
        val targetGame = selectedGameId ?: games.firstOrNull { it.isInstalled }?.id ?: games.firstOrNull()?.id ?: ""
        ThermalRiskDialog(
            onConfirm = {
                showThermalDialog = false
                onApplyOptimization(targetGame, true)
            },
            onDismiss = { showThermalDialog = false }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(
                title = "PERFORMANCE CONTROL",
                subtitle = "Tune hardware parameters and system profiles"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 1. CURRENT PROFILE: [ BALANCED ] [ PERFORMANCE ] [ EXTREME ]
            // ==========================================
            Text(
                text = "CURRENT PROFILE",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(backgroundColor = SurfaceElevated) {
                PerformanceModeSelector(
                    selectedMode = selectedMode,
                    onModeSelected = { mode -> onSelectMode(mode) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = selectedMode.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 2. OPTIMIZATION CAPABILITY STATUS
            // ==========================================
            SectionHeader(
                title = "CAPABILITY BREAKDOWN",
                subtitle = "Technical feasibility matrix on this device"
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassCard(backgroundColor = SurfaceElevated) {
                // Refresh Rate capability
                val refreshRateStatus = when {
                    isOptimized && lastResult?.isDisplayRateVerified == true -> CapabilityStatus.ACTIVE
                    capabilities.displayState.supports120Hz || capabilities.displayState.supportedRefreshRates.isNotEmpty() -> CapabilityStatus.SUPPORTED
                    else -> CapabilityStatus.NOT_SUPPORTED
                }
                CapabilityRow(
                    title = "Refresh Rate",
                    status = refreshRateStatus,
                    subtitle = "Locked compositor refresh rate (up to ${capabilities.displayState.maxRefreshRate.toInt()}Hz)"
                )

                // Game Mode capability
                val gameModeStatus = when {
                    capabilities.apiLevel < 31 -> CapabilityStatus.UNAVAILABLE
                    isOptimized -> CapabilityStatus.ACTIVE
                    else -> CapabilityStatus.SUPPORTED
                }
                CapabilityRow(
                    title = "Game Mode",
                    status = gameModeStatus,
                    subtitle = if (capabilities.apiLevel >= 31) "Android 12+ GameMode API low-latency mode" else "Requires Android 12+ (API 31+)"
                )

                // Process Priority capability (Legitimately unavailable without unsafe root)
                CapabilityRow(
                    title = "Process Priority",
                    status = CapabilityStatus.UNAVAILABLE,
                    subtitle = "Android security sandbox restricts foreign process scheduling"
                )

                // Thermal Control capability (Legitimately not available to bypass OEM thermal limits)
                CapabilityRow(
                    title = "Thermal Control",
                    status = CapabilityStatus.NOT_SUPPORTED,
                    subtitle = "OEM kernel thermal trip-points cannot be modified without unsafe exploits"
                )

                // Animation Scaling
                val animStatus = when {
                    !shizukuStatus.isReady -> CapabilityStatus.UNAVAILABLE
                    isOptimized -> CapabilityStatus.ACTIVE
                    else -> CapabilityStatus.SUPPORTED
                }
                CapabilityRow(
                    title = "Animation Latency",
                    status = animStatus,
                    subtitle = "Window & animator duration scale reduction"
                )

                // Memory Trimming
                CapabilityRow(
                    title = "Memory Optimization",
                    status = if (isOptimized) CapabilityStatus.ACTIVE else CapabilityStatus.SUPPORTED,
                    subtitle = "Non-destructive onTrimMemory background process reclamation"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 3. PRIMARY ACTIONS (CTA >= 56dp, min target >= 48dp)
            // ==========================================
            val targetGame = selectedGameId ?: games.firstOrNull { it.isInstalled }?.id ?: games.firstOrNull()?.id ?: ""
            val profileTitle = when (selectedMode) {
                PerformanceMode.SAFE -> "BALANCED"
                PerformanceMode.PERFORMANCE -> "PERFORMANCE"
                PerformanceMode.AGGRESSIVE -> "EXTREME"
                PerformanceMode.THERMAL_OVERRIDE -> "THERMAL OVERRIDE"
            }

            Button(
                onClick = {
                    if (selectedMode == PerformanceMode.THERMAL_OVERRIDE) {
                        showThermalDialog = true
                    } else {
                        onApplyOptimization(targetGame, false)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "APPLY $profileTitle BOOST",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onRestoreDefaults,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RESTORE DEFAULT SETTINGS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
