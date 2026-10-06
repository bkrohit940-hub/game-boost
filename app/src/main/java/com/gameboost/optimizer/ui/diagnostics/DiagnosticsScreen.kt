package com.gameboost.optimizer.ui.diagnostics

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusDanger
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun DiagnosticsScreen(
    capabilities: DeviceCapabilities,
    shizukuStatus: ShizukuStatus,
    lastResult: OptimizationResult?,
    activeBackup: DisplayStateBackup?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SYSTEM DIAGNOSTICS",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hardware, Shizuku, and Display Mode Matrix",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CRITICAL TECHNICAL NOTICE: Distinguishing Display Hz vs Game FPS vs Performance State
            GlassCard(
                backgroundColor = SurfaceElevated,
                border = BorderStroke(1.dp, AccentPrimary.copy(alpha = 0.4f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TECHNICAL METRICS ARCHITECTURE",
                        color = AccentPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Display Hz: The physical panel refresh rate configured via Android display compositor (e.g., 60Hz, 90Hz, 120Hz).\n" +
                            "• Game FPS: The in-game rendering frame rate generated by the game engine. Setting a 120Hz display mode does NOT guarantee 120 FPS if the game engine caps frames or GPU load throttles.\n" +
                            "• Performance State: Android Game Mode API profile and low-latency system animation scheduling.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shizuku State Diagnostics
            SectionHeader(title = "Shizuku Service Status")
            GlassCard(backgroundColor = SurfaceElevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = shizukuStatus.title,
                            color = if (shizukuStatus.isReady) StatusReady else StatusWarning,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = shizukuStatus.summaryText,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusBadge(
                        text = if (shizukuStatus.isReady) "AUTHORIZED" else "ATTENTION",
                        state = if (shizukuStatus.isReady) BadgeState.SUCCESS else BadgeState.WARNING
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                DiagRow("Package Installed", if (shizukuStatus.isInstalled) "Yes (moe.shizuku.privileged.api)" else "No")
                DiagRow("Binder Alive", if (shizukuStatus.isRunning) "Yes" else "No")
                DiagRow("API Version", "v${shizukuStatus.version}")
                DiagRow("Process UID", "${shizukuStatus.uid}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Capability Matrix
            SectionHeader(title = "Display Panel Modes")
            GlassCard(backgroundColor = SurfaceElevated) {
                DiagRow("Active Refresh Rate", "${capabilities.displayState.currentRefreshRate.toInt()}Hz")
                DiagRow("Maximum Supported Rate", "${capabilities.displayState.maxRefreshRate.toInt()}Hz")
                DiagRow("120Hz Panel Support", if (capabilities.displayState.supports120Hz) "Supported" else "Not Supported")
                DiagRow("Resolution", "${capabilities.displayState.resolutionWidth} x ${capabilities.displayState.resolutionHeight}")
                DiagRow("HDR Capable", if (capabilities.displayState.supportsHdr) "Yes" else "No")

                if (capabilities.displayState.supportedModes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "DETECTED HARDWARE MODES",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    capabilities.displayState.supportedModes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mode #${mode.modeId}: ${mode.width}x${mode.height}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${mode.refreshRate.toInt()}Hz",
                                color = AccentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hardware & OS Telemetry
            SectionHeader(title = "Hardware Specifications")
            GlassCard(backgroundColor = SurfaceElevated) {
                DiagRow("Device", "${capabilities.brand} ${capabilities.model}")
                DiagRow("Manufacturer", capabilities.manufacturer)
                DiagRow("Platform SoC", capabilities.socHardware)
                DiagRow("CPU Cores", "${capabilities.cpuCores}")
                DiagRow("Physical Memory", capabilities.formattedRam)
                DiagRow("Android Version", "Android ${capabilities.androidVersion} (API ${capabilities.apiLevel})")
                DiagRow("System Firmware", capabilities.oemSkin)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optimization Capability Matrix (Transparency specification)
            SectionHeader(title = "Optimization Capability Matrix")
            GlassCard(backgroundColor = SurfaceElevated) {
                val refreshStatus = if (shizukuStatus.isReady) "SUPPORTED" else "UNAVAILABLE"
                val gameModeStatus = if (android.os.Build.VERSION.SDK_INT >= 31) {
                    if (shizukuStatus.isReady) "SUPPORTED" else "UNAVAILABLE"
                } else {
                    "NOT SUPPORTED"
                }

                DiagRow("Refresh Control", refreshStatus)
                DiagRow("Game Mode API", gameModeStatus)
                DiagRow("Background Memory Trim", if (shizukuStatus.isReady) "SUPPORTED" else "STANDALONE")
                DiagRow("Thermal Override", "NOT SUPPORTED")
                DiagRow("Process Priority (renice)", "UNAVAILABLE")

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Transparency Notice: Hardware thermal protection is never bypassed. Process priority renice is blocked by Linux kernel SELinux policies.",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Backup State
            SectionHeader(title = "Reversible Session Backup")
            GlassCard(backgroundColor = SurfaceElevated) {
                if (activeBackup != null) {
                    DiagRow("Peak Refresh Rate", activeBackup.peakRefreshRate ?: "System default")
                    DiagRow("Min Refresh Rate", activeBackup.minRefreshRate ?: "System default")
                    DiagRow("User Refresh Rate", activeBackup.userRefreshRate ?: "Not set")
                    DiagRow("Window Animation", "${activeBackup.windowAnimationScale ?: "1.0"}x")
                    DiagRow("Transition Animation", "${activeBackup.transitionAnimationScale ?: "1.0"}x")
                    DiagRow("Animator Duration", "${activeBackup.animatorDurationScale ?: "1.0"}x")
                } else {
                    Text(
                        text = "No active session backup. System is running at baseline defaults.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DiagRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
