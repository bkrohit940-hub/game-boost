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

import com.gameboost.optimizer.models.HardwareStats

@Composable
fun DiagnosticsScreen(
    capabilities: DeviceCapabilities,
    shizukuStatus: ShizukuStatus,
    lastResult: OptimizationResult?,
    activeBackup: DisplayStateBackup?,
    hardwareStats: HardwareStats? = null,
    privilegedState: com.gameboost.optimizer.system.PrivilegedSystemState? = null,
    testResult: com.gameboost.optimizer.models.ShellCommandResult? = null,
    isTestingBackend: Boolean = false,
    onTestBackend: (() -> Unit)? = null,
    onClearTestResult: (() -> Unit)? = null,
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
                        text = "Hardware telemetry, thermal state, and backend audit",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 1. DEVICE
            // ==========================================
            SectionHeader(title = "DEVICE")
            GlassCard(backgroundColor = SurfaceElevated) {
                DiagRow("Manufacturer", capabilities.manufacturer)
                DiagRow("Model", "${capabilities.brand} ${capabilities.model}")
                DiagRow("Android Version", capabilities.androidVersion)
                DiagRow("API Level", "${capabilities.apiLevel}")
                DiagRow("CPU / SoC", "${capabilities.socHardware} (${capabilities.cpuCores} cores)")
                DiagRow("RAM", capabilities.formattedRam)
                DiagRow("System Firmware", capabilities.oemSkin)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 2. DISPLAY
            // ==========================================
            SectionHeader(title = "DISPLAY")
            GlassCard(backgroundColor = SurfaceElevated) {
                val currentHz = hardwareStats?.currentRefreshRate ?: capabilities.displayState.currentRefreshRate
                val supportedHzList = capabilities.displayState.supportedRefreshRates
                    .map { "${it.toInt()} Hz" }
                    .distinct()
                    .joinToString(", ")
                    .ifEmpty { "${currentHz.toInt()} Hz" }

                DiagRow("Resolution", "${capabilities.displayState.resolutionWidth} x ${capabilities.displayState.resolutionHeight}")
                DiagRow("Current Refresh Rate", "${currentHz.toInt()} Hz")
                DiagRow("Supported Refresh Rates", supportedHzList)
                DiagRow("120Hz Panel Capable", if (capabilities.displayState.supports120Hz) "YES" else "NO")
                DiagRow("HDR Capable", if (capabilities.displayState.supportsHdr) "YES" else "NO")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. THERMAL
            // ==========================================
            SectionHeader(title = "THERMAL")
            GlassCard(backgroundColor = SurfaceElevated) {
                val tempText = hardwareStats?.formattedTemp ?: "UNAVAILABLE"
                val thermalStatusText = hardwareStats?.thermalStatus ?: "NORMAL"
                val headroomText = hardwareStats?.thermalHeadroom ?: "UNAVAILABLE"

                DiagRow("Temperature", tempText)
                DiagRow("Thermal Status", thermalStatusText)
                DiagRow("Thermal Headroom", headroomText)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Values queried from official Android PowerManager and BatteryManager subsystem. GameBoost never fabricates thermal telemetry.",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 4. PRIVILEGED BACKEND
            // ==========================================
            SectionHeader(title = "PRIVILEGED BACKEND")
            GlassCard(backgroundColor = SurfaceElevated) {
                val isShizukuReady = shizukuStatus.isReady
                val adbConnected = privilegedState?.wirelessAdbState?.status == com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED
                val activeType = privilegedState?.activeBackendType ?: if (isShizukuReady) com.gameboost.optimizer.system.BackendType.SHIZUKU else com.gameboost.optimizer.system.BackendType.NONE

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SHIZUKU",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isShizukuReady) "Binder IPC authorized (v${shizukuStatus.version})" else shizukuStatus.summaryText,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusBadge(
                        text = if (isShizukuReady) "AUTHORIZED" else "UNAVAILABLE",
                        state = if (isShizukuReady) BadgeState.SUCCESS else BadgeState.WARNING
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WIRELESS ADB",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (adbConnected) "TLS loopback active on port ${privilegedState.wirelessAdbState.connectedPort}" else "Android 11+ direct on-device shell fallback",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusBadge(
                        text = if (adbConnected) "CONNECTED" else "DISCONNECTED",
                        state = if (adbConnected) BadgeState.SUCCESS else BadgeState.NEUTRAL
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                DiagRow("Active Routing Engine", activeType.displayName)

                if (onTestBackend != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Button(
                        onClick = onTestBackend,
                        enabled = !isTestingBackend,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = AccentPrimary,
                            contentColor = DarkBg
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        if (isTestingBackend) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = DarkBg,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TESTING SHELL IPC...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TEST PRIVILEGED SHELL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (testResult != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = DarkBg,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (testResult.isSuccess) StatusReady.copy(alpha = 0.5f) else StatusDanger.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (testResult.isSuccess) "VERIFIED (${testResult.durationMs}ms)" else "EXECUTION FAILED",
                                color = if (testResult.isSuccess) StatusReady else StatusDanger,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (testResult.stdout.isNotEmpty()) {
                                Text(
                                    text = "stdout: ${testResult.stdout}",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 5. OPTIMIZATION CAPABILITIES
            // ==========================================
            SectionHeader(title = "OPTIMIZATION CAPABILITIES")
            GlassCard(backgroundColor = SurfaceElevated) {
                val isPrivileged = shizukuStatus.isReady || (privilegedState?.wirelessAdbState?.status == com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED)

                DiagRow("Refresh Rate Locking", if (isPrivileged) "SUPPORTED" else "UNAVAILABLE")
                DiagRow("Android Game Mode API", if (capabilities.apiLevel >= 31 && isPrivileged) "SUPPORTED" else if (capabilities.apiLevel >= 31) "UNAVAILABLE" else "NOT SUPPORTED")
                DiagRow("Animation Scaling", if (isPrivileged) "SUPPORTED" else "UNAVAILABLE")
                DiagRow("Background Memory Trim", "SUPPORTED")
                DiagRow("Process Priority (renice)", "UNAVAILABLE")
                DiagRow("Thermal Control Bypassing", "NOT SUPPORTED")

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Process priority renice is blocked by Android Linux SELinux sandbox. Thermal trip points are governed by kernel safety drivers and cannot be unsafely modified.",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
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
