package com.gameboost.optimizer.ui.diagnostics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.DisplayDiagnosticData
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.PrivilegedSystemState
import com.gameboost.optimizer.system.adb.AdbConnectionStatus
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
import com.gameboost.optimizer.theme.RedCoreSuccessContainer
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.RedCoreCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

/**
 * DIAGNOSTICS: SYSTEM HUD (Section 9)
 * Advanced RedMagic Gaming HUD Aesthetic:
 * 1. DEVICE: Manufacturer, Model, Android version, CPU, RAM
 * 2. DISPLAY: Resolution, Current refresh rate, Supported refresh rates, Current display mode
 * 3. BACKEND: Shizuku service, Permission status, Binder connection, Shell test, Wireless ADB
 * 4. OPTIMIZATION: Refresh control, Game Mode, Thermal monitoring, Process priority, Restore capability
 *
 * Thin dividers, small labels, strong numbers, red section markers, real diagnostic scan animation.
 */
@Composable
fun DiagnosticsScreen(
    capabilities: DeviceCapabilities,
    shizukuStatus: ShizukuStatus,
    lastResult: OptimizationResult?,
    activeBackup: DisplayStateBackup?,
    hardwareStats: HardwareStats? = null,
    displayDiagnostics: DisplayDiagnosticData? = null,
    privilegedState: PrivilegedSystemState? = null,
    testResult: ShellCommandResult? = null,
    isTestingBackend: Boolean = false,
    onTestBackend: (() -> Unit)? = null,
    onRefreshDiagnostics: (() -> Unit)? = null,
    onClearTestResult: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHz = hardwareStats?.currentRefreshRate ?: capabilities.displayState.currentRefreshRate
    val isPrivileged = shizukuStatus.isReady || (privilegedState?.wirelessAdbState?.status == AdbConnectionStatus.CONNECTED)

    // Scanning animation for diagnostics
    val infiniteTransition = rememberInfiniteTransition(label = "diagScan")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

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
                // 1. SCREEN HEADER: TITLE + BACK ACTION + SCAN TRIGGER
                // =========================================================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = RedCoreTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 3.dp, height = 14.dp)
                                            .background(PrimaryGamingRed)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SYSTEM HUD DIAGNOSTICS",
                                        color = RedCoreTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "LOW-LEVEL TELEMETRY & CAPABILITY PROBING",
                                    color = RedCoreTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        // Real Scan / Probing Action
                        Surface(
                            modifier = Modifier.clickable {
                                onRefreshDiagnostics?.invoke()
                                onTestBackend?.invoke()
                            },
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            color = RedCoreDarkCrimson,
                            border = BorderStroke(1.dp, BrightActiveRed)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isTestingBackend) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        color = BrightActiveRed,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "SCANNING",
                                        color = BrightActiveRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = BrightActiveRed,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "RUN SCAN",
                                        color = BrightActiveRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 2. DEVICE SECTION
                // Manufacturer, Model, Android version, CPU, RAM
                // =========================================================================
                item {
                    HudSectionCard(
                        title = "DEVICE SPECIFICATIONS",
                        marker = "[DEV]"
                    ) {
                        HudTelemetryRow(label = "MANUFACTURER", value = capabilities.manufacturer.uppercase())
                        HudDivider()
                        HudTelemetryRow(label = "MODEL", value = capabilities.model.uppercase())
                        HudDivider()
                        HudTelemetryRow(
                            label = "ANDROID VERSION",
                            value = "Android ${capabilities.androidVersion} (API ${capabilities.apiLevel})"
                        )
                        HudDivider()
                        val cpuSoc = if (capabilities.socHardware.isNotBlank()) capabilities.socHardware else "ARM64"
                        val cpuCores = capabilities.cpuCores
                        HudTelemetryRow(label = "CPU & CHIPSET", value = "$cpuSoc • $cpuCores CORES")
                        HudDivider()
                        val ramDisplay = hardwareStats?.formattedRam ?: capabilities.formattedRam
                        HudTelemetryRow(
                            label = "RAM ALLOCATION",
                            value = ramDisplay
                        )
                    }
                }

                // =========================================================================
                // 3. DISPLAY SECTION
                // Resolution, Current refresh rate, Supported refresh rates, Current display mode
                // =========================================================================
                item {
                    HudSectionCard(
                        title = "DISPLAY HARDWARE TELEMETRY",
                        marker = "[DSP]"
                    ) {
                        HudTelemetryRow(
                            label = "RESOLUTION",
                            value = "${capabilities.displayState.resolutionWidth} x ${capabilities.displayState.resolutionHeight} px"
                        )
                        HudDivider()
                        HudTelemetryRow(
                            label = "CURRENT REFRESH RATE",
                            value = "${currentHz.toInt()} Hz",
                            highlightValue = true
                        )
                        HudDivider()
                        val supportedRatesText = capabilities.displayState.supportedRefreshRates.joinToString(" / ") { "${it.toInt()}Hz" }
                        HudTelemetryRow(
                            label = "SUPPORTED MODES",
                            value = if (supportedRatesText.isNotEmpty()) supportedRatesText else "60Hz / 90Hz / 120Hz"
                        )
                        HudDivider()
                        HudTelemetryRow(
                            label = "ACTIVE DISPLAY MODE",
                            value = "${capabilities.displayState.resolutionWidth}x${capabilities.displayState.resolutionHeight}@${currentHz.toInt()}Hz"
                        )
                    }
                }

                // =========================================================================
                // 4. BACKEND SECTION
                // Shizuku service, Permission status, Binder connection, Shell test, Wireless ADB
                // =========================================================================
                item {
                    HudSectionCard(
                        title = "PRIVILEGED BACKEND SUBSYSTEM",
                        marker = "[IPC]"
                    ) {
                        HudStatusRow(
                            label = "SHIZUKU SERVICE",
                            statusText = if (shizukuStatus.isInstalled) "INSTALLED" else "NOT INSTALLED",
                            isSuccess = shizukuStatus.isInstalled
                        )
                        HudDivider()
                        HudStatusRow(
                            label = "PERMISSION STATUS",
                            statusText = if (shizukuStatus.isAuthorized) "GRANTED" else "NOT GRANTED",
                            isSuccess = shizukuStatus.isAuthorized
                        )
                        HudDivider()
                        HudStatusRow(
                            label = "BINDER CONNECTION",
                            statusText = if (shizukuStatus.isReady) "READY & BOUND" else "DISCONNECTED",
                            isSuccess = shizukuStatus.isReady
                        )
                        HudDivider()
                        val adbStatusText = privilegedState?.wirelessAdbState?.status?.name ?: "DISCONNECTED"
                        HudStatusRow(
                            label = "WIRELESS ADB BRIDGE",
                            statusText = adbStatusText,
                            isSuccess = privilegedState?.wirelessAdbState?.status == AdbConnectionStatus.CONNECTED
                        )

                        HudDivider()

                        // Shell Command Test Action & Output
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "READ-ONLY SHELL TEST",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Execute verified command: 'id'",
                                    color = RedCoreTextMuted,
                                    fontSize = 9.5.sp
                                )
                            }
                            Button(
                                onClick = { onTestBackend?.invoke() },
                                enabled = isPrivileged && !isTestingBackend,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed),
                                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("RUN ID TEST", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Test Result Panel (Real execution output)
                        if (testResult != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = CutCornerShape(4.dp),
                                color = RedCoreBgSecondary,
                                border = BorderStroke(1.dp, if (testResult.isSuccess) RedCoreSuccess else RedCoreDanger)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "EXIT CODE: ${testResult.exitCode} (${testResult.durationMs}ms)",
                                            color = if (testResult.isSuccess) RedCoreSuccess else RedCoreDanger,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "VERIFIED OUTPUT",
                                            color = RedCoreTextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (testResult.stdout.isNotBlank()) testResult.stdout.trim() else testResult.stderr.trim(),
                                        color = RedCoreTextPrimary,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 5. OPTIMIZATION SECTION
                // Refresh control, Game Mode, Thermal monitoring, Process priority, Restore capability
                // =========================================================================
                item {
                    HudSectionCard(
                        title = "OPTIMIZATION ARBITRATION",
                        marker = "[OPT]"
                    ) {
                        HudStatusRow(
                            label = "REFRESH RATE CONTROL",
                            statusText = if (capabilities.displayState.supports120Hz || capabilities.displayState.supports90Hz) "CAPABLE (SurfaceFlinger)" else "FIXED 60Hz",
                            isSuccess = capabilities.displayState.supports120Hz || capabilities.displayState.supports90Hz
                        )
                        HudDivider()
                        HudStatusRow(
                            label = "ANDROID GAME MODE",
                            statusText = if (capabilities.apiLevel >= 31) "SUPPORTED (API ${capabilities.apiLevel})" else "NOT SUPPORTED (< API 31)",
                            isSuccess = capabilities.apiLevel >= 31
                        )
                        HudDivider()
                        HudStatusRow(
                            label = "THERMAL TELEMETRY",
                            statusText = if (hardwareStats?.batteryTemperatureCelsius ?: 0f > 0) "ACTIVE SENSOR PROBING" else "UNAVAILABLE",
                            isSuccess = (hardwareStats?.batteryTemperatureCelsius ?: 0f) > 0
                        )
                        HudDivider()
                        HudStatusRow(
                            label = "PROCESS PRIORITY BOOST",
                            statusText = if (shizukuStatus.isReady) "PRIVILEGED RENICE READY" else "REQUIRES SHIZUKU",
                            isSuccess = shizukuStatus.isReady
                        )
                        HudDivider()
                        HudStatusRow(
                            label = "BASELINE RESTORATION STATE",
                            statusText = if (activeBackup != null) "BASELINE SNAPSHOT ACTIVE" else "NO ACTIVE MUTATIONS",
                            isSuccess = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HudSectionCard(
    title: String,
    marker: String,
    content: @Composable () -> Unit
) {
    RedCoreCard(
        backgroundColor = RedCoreSurfaceElevated,
        borderColor = RedCoreBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 12.dp)
                        .background(PrimaryGamingRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = RedCoreTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = marker,
                color = BrightActiveRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        content()
    }
}

@Composable
private fun HudTelemetryRow(
    label: String,
    value: String,
    highlightValue: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = RedCoreTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            color = if (highlightValue) BrightActiveRed else RedCoreTextPrimary,
            fontSize = if (highlightValue) 13.5.sp else 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun HudStatusRow(
    label: String,
    statusText: String,
    isSuccess: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = RedCoreTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(if (isSuccess) RedCoreSuccess else RedCoreWarning, CutCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = statusText.uppercase(),
                color = if (isSuccess) RedCoreSuccess else RedCoreWarning,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HudDivider() {
    HorizontalDivider(
        color = RedCoreBorderSubtle,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
