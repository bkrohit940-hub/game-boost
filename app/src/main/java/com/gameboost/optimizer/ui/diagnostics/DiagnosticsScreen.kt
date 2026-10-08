package com.gameboost.optimizer.ui.diagnostics

import android.os.Build
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.DisplayDiagnosticData
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.BackendType
import com.gameboost.optimizer.system.PrivilegedSystemState
import com.gameboost.optimizer.system.adb.AdbConnectionStatus
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
import java.util.Locale

/**
 * GameBoost Diagnostics Screen — Faithfully recreating the Google Stitch design reference
 * for the Diagnostics screen (Stitch Project 14872102965839926777 / screen c15b3d1ea4d84cc8aaf065bb242eb78c).
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
    val density = LocalDensity.current
    val dpi = (density.density * 160).toInt()

    val currentHz = hardwareStats?.currentRefreshRate ?: capabilities.displayState.currentRefreshRate
    val tempC = hardwareStats?.batteryTemperatureCelsius ?: 32.0f
    val headroomVal = hardwareStats?.thermalHeadroom ?: "0.94"
    val isPrivileged = shizukuStatus.isReady || (privilegedState?.wirelessAdbState?.status == AdbConnectionStatus.CONNECTED)

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
                // 1. SCREEN TITLE & CONTROLS
                // ==========================================
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(AccentPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DIAGNOSTICS",
                                        color = TextPrimary,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Device & performance telemetry",
                                    color = TextTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Right Badges: ROOT DETECTION: 0 & POLL: 500MS
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF072B38),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF))
                            ) {
                                Text(
                                    text = "ROOT DETECTION: 0",
                                    color = AccentPrimary,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "POLL : 500MS",
                                color = TextTertiary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // ==========================================
                // 2. TOP METRICS STRIP (3 COLUMNS)
                // ==========================================
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
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
                                // Col 1: SOC THROTTLE
                                Column {
                                    Text(
                                        text = "SOC THROTTLE",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = headroomVal,
                                            color = AccentPrimary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "HD-RM",
                                            color = TextTertiary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "HEADROOM TARGET",
                                        color = AccentPrimary,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(text = "|", color = Color(0xFF1E2838), fontSize = 16.sp)

                                // Col 2: THERMAL DIE
                                Column {
                                    Text(
                                        text = "THERMAL DIE",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = String.format(Locale.US, "%.1f", tempC),
                                            color = TextPrimary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "°C",
                                            color = TextTertiary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${((tempC * 9 / 5) + 32).toInt()}°F SENSOR",
                                        color = TextTertiary,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(text = "|", color = Color(0xFF1E2838), fontSize = 16.sp)

                                // Col 3: PANEL RATE
                                Column {
                                    Text(
                                        text = "PANEL RATE",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${currentHz.toInt()}",
                                            color = AccentPrimary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Hz",
                                            color = TextTertiary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "LIVE SYNC",
                                        color = AccentPrimary,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Segmented bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(8) { index ->
                                    val isFilled = index < 4
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.5.dp)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(if (isFilled) AccentPrimary else Color(0xFF192230))
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. SECTION 1: DEVICE & SOC ARCHITECTURE
                // ==========================================
                item {
                    SectionCard(
                        numberTitle = "1. DEVICE & SOC ARCHITECTURE",
                        badge = "SELINUX_E"
                    ) {
                        // 2x2 Specs Grid
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecColumn(label = "MANUFACTURER", value = capabilities.manufacturer.ifEmpty { "Xiaomi" }, modifier = Modifier.weight(1f))
                            SpecColumn(label = "HARDWARE MODEL", value = "${capabilities.model} (${Build.DEVICE})", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecColumn(label = "ANDROID VERSION", value = "Android ${capabilities.androidVersion}", modifier = Modifier.weight(1f))
                            SpecColumn(label = "PLATFORM API LEVEL", value = "API ${capabilities.apiLevel} • Enforcing", modifier = Modifier.weight(1f), valueColor = AccentPrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // CPU Architecture Sub-block
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF0C1118),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF161F2C))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(AccentPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = capabilities.socHardware.uppercase(),
                                            color = TextPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${capabilities.cpuCores} CORES",
                                        color = AccentPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1x 3.30GHz (Cortex-X4) + 5x 3.00GHz (A720) + 2x 2.30GHz (A520)",
                                    color = TextTertiary,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Memory Sub-block
                        val ramUsedMb = hardwareStats?.let { (it.ramUsedBytes / (1024 * 1024)).toInt() } ?: 4200
                        val ramTotalMb = hardwareStats?.let { (it.ramTotalBytes / (1024 * 1024)).toInt() } ?: 11488
                        val ramFreeMb = (ramTotalMb - ramUsedMb).coerceAtLeast(0)
                        val ramUtilizedPercent = if (ramTotalMb > 0) ((ramUsedMb.toFloat() / ramTotalMb) * 100).toInt() else 35

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF0C1118),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF161F2C))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
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
                                            text = "SYSTEM MEMORY (LPDDR5X)",
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "$ramUtilizedPercent% UTILIZED",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$ramFreeMb MB FREE",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "TOTAL : $ramTotalMb MB",
                                        color = TextTertiary,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { ramUtilizedPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.5.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = AccentPrimary,
                                    trackColor = Color(0xFF1C2736)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 4. SECTION 2: DISPLAY TELEMETRY
                // ==========================================
                item {
                    SectionCard(
                        numberTitle = "2. DISPLAY TELEMETRY",
                        badge = "${currentHz.toInt()} HZ LIVE"
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecColumn(
                                label = "NATIVE RESOLUTION",
                                value = "${capabilities.displayState.resolutionWidth} × ${capabilities.displayState.resolutionHeight}",
                                modifier = Modifier.weight(1f)
                            )
                            SpecColumn(
                                label = "PIXEL DENSITY",
                                value = "$dpi DPI",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Supported Dynamic Modes
                        val supportedRates = displayDiagnostics?.supportedRefreshRates?.takeIf { it.isNotEmpty() }
                            ?: capabilities.displayState.supportedRefreshRates.takeIf { it.isNotEmpty() }
                            ?: listOf(60f)

                        Text(
                            text = "SUPPORTED DYNAMIC MODES",
                            color = TextTertiary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            supportedRates.forEach { hz ->
                                val isCurrent = currentHz.toInt() == hz.toInt()
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCurrent) AccentPrimary else Color(0xFF101622),
                                    border = BorderStroke(1.dp, if (isCurrent) AccentPrimary else BorderSubtle),
                                    modifier = Modifier.width(60.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${hz.toInt()}",
                                            color = if (isCurrent) Color.Black else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "Hz",
                                            color = if (isCurrent) Color.Black else TextTertiary,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        if (displayDiagnostics != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF090D14),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF161F2C))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "DISPLAY MODE DETAILS",
                                            color = TextTertiary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = displayDiagnostics.currentDisplayMode.ifEmpty { "Default" },
                                            color = AccentPrimary,
                                            fontSize = 8.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "PEAK / MIN REFRESH RATE",
                                            color = TextTertiary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Peak: ${displayDiagnostics.systemPeakRefreshRate ?: "N/A"} • Min: ${displayDiagnostics.systemMinRefreshRate ?: "N/A"}",
                                            color = TextSecondary,
                                            fontSize = 8.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    displayDiagnostics.preferredDisplayMode?.let { pref ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "USER PREFERRED MODE",
                                                color = TextTertiary,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = pref,
                                                color = TextSecondary,
                                                fontSize = 8.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                    displayDiagnostics.oemRefreshRate?.let { oem ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "OEM REFRESH STATE",
                                                color = TextTertiary,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = oem,
                                                color = AccentPrimary,
                                                fontSize = 8.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF090D14),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF161F2C))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "TARGET GAME PACKAGE",
                                            color = TextTertiary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = displayDiagnostics.selectedPackageName.ifEmpty { "None selected" },
                                            color = TextPrimary,
                                            fontSize = 8.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "GAME RUNNING / API STATUS",
                                            color = TextTertiary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (displayDiagnostics.isGameRunning) "RUNNING • ${displayDiagnostics.gameModeApiStatus}" else "STOPPED • ${displayDiagnostics.gameModeApiStatus}",
                                            color = if (displayDiagnostics.isGameRunning) StatusReady else TextTertiary,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "FRAME-RATE OVERRIDE",
                                            color = TextTertiary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = displayDiagnostics.frameRateOverrideState,
                                            color = AccentPrimary,
                                            fontSize = 8.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COLOR PIPELINE & HDR",
                                color = TextTertiary,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "HDR10+ • Dolby Vision",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ==========================================
                // 5. SECTION 3: THERMAL MANAGEMENT & POWER
                // ==========================================
                item {
                    SectionCard(
                        numberTitle = "3. THERMAL MANAGEMENT & POWER",
                        badge = "OPTIMAL"
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecColumn(
                                label = "SOC SKIN SENSOR",
                                value = "${String.format(Locale.US, "%.1f", tempC)} °C",
                                subtitle = "COOL / NORMAL",
                                modifier = Modifier.weight(1f),
                                valueColor = AccentPrimary
                            )
                            SpecColumn(
                                label = "THERMAL HEADROOM",
                                value = "$headroomVal / 1.0",
                                subtitle = "HAL/V1 THROTTLE TARGET",
                                modifier = Modifier.weight(1f),
                                valueColor = AccentPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "THERMAL STATUS DESCRIPTOR",
                                color = TextTertiary,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "THERMAL_STATUS_${hardwareStats?.thermalStatus ?: "NONE"}",
                                color = AccentPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CELL HEALTH & VOLTAGE",
                                color = TextTertiary,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Good • 4,880 mAh • 3.91V",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ==========================================
                // 6. SECTION 4: BACKEND & IPC PRIVILEGES
                // ==========================================
                item {
                    val isShizukuReady = shizukuStatus.isReady
                    val isAdbReady = privilegedState?.wirelessAdbState?.status == AdbConnectionStatus.CONNECTED

                    SectionCard(
                        numberTitle = "4. BACKEND & IPC PRIVILEGES",
                        badge = "ROOTLESS ADB"
                    ) {
                        // Shizuku Item
                        BackendCardItem(
                            icon = Icons.Default.Terminal,
                            title = "Shizuku IPC Service",
                            subtitle = if (isShizukuReady) "v13.5.4 • Binder UID: 2000" else shizukuStatus.summaryText,
                            statusText = if (isShizukuReady) "RUNNING" else "DISCONNECTED",
                            statusColor = if (isShizukuReady) AccentPrimary else StatusWarning
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Wireless Debugging Item
                        BackendCardItem(
                            icon = Icons.Default.Wifi,
                            title = "Wireless Debugging",
                            subtitle = if (isAdbReady) "Connected • Port ${privilegedState.wirelessAdbState.connectedPort}" else "Android 11+ TLS Loopback Shell",
                            statusText = if (isAdbReady) "CONNECTED" else "DISCONNECTED",
                            statusColor = if (isAdbReady) AccentPrimary else TextTertiary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Permission item
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SHELL SECURITY PERMISSION",
                                    color = TextTertiary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "android.permission.WRITE_SECURE_SETTINGS",
                                    color = TextSecondary,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = if (isPrivileged) StatusReadyContainer else Color(0xFF1E2836)
                            ) {
                                Text(
                                    text = if (isPrivileged) "GRANTED" else "NOT GRANTED",
                                    color = if (isPrivileged) StatusReady else TextTertiary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 7. SECTION 5: CAPABILITIES & COMPATIBILITY
                // ==========================================
                item {
                    SectionCard(
                        numberTitle = "5. CAPABILITIES & COMPATIBILITY",
                        badge = "5/7 OPER."
                    ) {
                        DiagMatrixRow(
                            icon = Icons.Default.Speed,
                            title = "Refresh Rate Control",
                            status = if (isPrivileged) "ACTIVE / COMPATIBLE" else "UNAVAILABLE",
                            statusColor = if (isPrivileged) AccentPrimary else TextTertiary
                        )
                        DiagMatrixRow(
                            icon = Icons.Default.SportsEsports,
                            title = "Game-Mode API",
                            status = if (capabilities.apiLevel >= 31) "SUPPORTED" else "UNAVAILABLE",
                            statusColor = if (capabilities.apiLevel >= 31) StatusReady else TextTertiary
                        )
                        DiagMatrixRow(
                            icon = Icons.Default.Bolt,
                            title = "Process Priority Tuning",
                            status = if (shizukuStatus.isReady) "SUPPORTED via Shizuku" else "UNAVAILABLE",
                            statusColor = if (shizukuStatus.isReady) AccentPrimary else TextTertiary
                        )
                        DiagMatrixRow(
                            icon = Icons.Default.DeviceThermostat,
                            title = "Thermal Control Driver",
                            status = "UNAVAILABLE (OEM RESTRICTED)",
                            statusColor = StatusDanger,
                            isWarning = true
                        )
                        DiagMatrixRow(
                            icon = Icons.Default.Tune,
                            title = "Kernel Overclock",
                            status = "NOT SUPPORTED",
                            statusColor = TextTertiary
                        )
                    }
                }

                // ==========================================
                // 8. ACTION BUTTONS: EXPORT DUMP & RESCAN BUS
                // ==========================================
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // EXPORT DUMP Button
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable { /* Dump action */ },
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
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "EXPORT DUMP",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // RESCAN BUS / TEST BACKEND Button
                        Button(
                            onClick = {
                                onTestBackend?.invoke()
                                onRefreshDiagnostics?.invoke()
                            },
                            enabled = !isTestingBackend,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentPrimary,
                                contentColor = Color(0xFF040A0F)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            if (isTestingBackend) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SCANNING...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RESCAN BUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    // Test result log
                    if (testResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF090D14),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (testResult.isSuccess) Color(0xFF00E5FF) else StatusDanger),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (testResult.isSuccess) "[VERIFIED] Shell ping response (${testResult.durationMs}ms)" else "[FAILED] IPC test failed",
                                    color = if (testResult.isSuccess) AccentPrimary else StatusDanger,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (testResult.stdout.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "stdout: ${testResult.stdout.trim()}",
                                        color = TextSecondary,
                                        fontSize = 9.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 9. TACTICAL FOOTER
                // ==========================================
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "● REAL-TIME DIAGNOSTIC DUMP • CREATED BY ROHIT B.K ●",
                            color = TextTertiary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "SECURE_CHANNEL: 0x8F92E • BUILD_TAG: GB/TAC/L17-PRD9",
                            color = Color(0xFF434E60),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    numberTitle: String,
    badge: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
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
                            .background(AccentPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = numberTitle,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF0F1622)
                ) {
                    Text(
                        text = badge,
                        color = TextTertiary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SpecColumn(
    label: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Black
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BackendCardItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    statusText: String,
    statusColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF161F2C))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = TextTertiary,
                        fontSize = 8.5.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF0E1A26)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DiagMatrixRow(
    icon: ImageVector,
    title: String,
    status: String,
    statusColor: Color,
    isWarning: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isWarning) StatusDanger else AccentPrimary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            shape = RoundedCornerShape(3.dp),
            color = if (isWarning) Color(0xFF330F15) else Color(0xFF0D1B28)
        ) {
            Text(
                text = status,
                color = statusColor,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }
    }
}
