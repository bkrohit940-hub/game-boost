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
                title = "Optimization Center",
                subtitle = "Configure system, memory, and performance parameters"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Performance Mode Selector
            GlassCard(backgroundColor = SurfaceElevated) {
                Text(
                    text = "SELECT PERFORMANCE PROFILE",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                PerformanceModeSelector(
                    selectedMode = selectedMode,
                    onModeSelected = { mode -> onSelectMode(mode) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedMode.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Automatic Display Mode Optimization
            GlassCard(backgroundColor = SurfaceElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DISPLAY REFRESH RATE",
                            color = TextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Automatic Detection (${capabilities.displayState.maxRefreshRate.toInt()}Hz max panel)",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    StatusBadge(
                        text = if (capabilities.displayState.supports120Hz) "120HZ PANEL" else "${capabilities.displayState.maxRefreshRate.toInt()}HZ PANEL",
                        state = BadgeState.SUCCESS
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manual refresh-rate selection has been replaced by automatic detection. Game Boost detects the maximum supported display mode from DisplayManager and verifies active rates with the Android compositor.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Legitimate Gaming Memory Optimization
            GlassCard(backgroundColor = SurfaceElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GAMING MEMORY OPTIMIZATION",
                            color = TextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = hardwareStats.formattedRam,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = StatusReady,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Non-destructive background trimming via Android onTrimMemory. Critical services, Game Boost, Shizuku, and telephony remain protected.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Apply / Restore Actions
            val targetGame = selectedGameId ?: games.firstOrNull { it.isInstalled }?.id ?: games.firstOrNull()?.id ?: ""
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
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "APPLY ${selectedMode.displayName.uppercase()} BOOST",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onRestoreDefaults,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESTORE DEFAULT SETTINGS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
