package com.gameboost.optimizer.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.R
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.AccentPrimary
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
import com.gameboost.optimizer.ui.components.RefreshRateSelector
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun SettingsScreen(
    userPreferences: AppUserPreferences,
    shizukuStatus: ShizukuStatus,
    wirelessAdbState: com.gameboost.optimizer.system.adb.WirelessAdbState? = null,
    adbOperationStatus: String? = null,
    supportedRefreshRates: List<Float> = emptyList(),
    onSelectDefaultProfile: (PerformanceMode) -> Unit = {},
    onSelectPreferredRefreshRate: (Float) -> Unit = {},
    onToggleAutoBoost: (Boolean) -> Unit,
    onToggleRestoreOnExit: (Boolean) -> Unit,
    onToggleSessionMonitoring: (Boolean) -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    onToggleStartupScreen: (Boolean) -> Unit = {},
    onPairWirelessAdb: ((String, Int) -> Unit)? = null,
    onConnectWirelessAdb: ((Int) -> Unit)? = null,
    onDisconnectWirelessAdb: (() -> Unit)? = null,
    onRecheckShizuku: () -> Unit,
    onOpenShizukuSetup: () -> Unit,
    onRestoreDefault: (() -> Unit)? = null,
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
                        text = "SETTINGS",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Application preferences and engine configuration",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 1. GENERAL
            // ==========================================
            SectionHeader(title = "GENERAL")
            GlassCard(backgroundColor = SurfaceElevated) {
                // Default Profile
                Text(
                    text = "DEFAULT PROFILE",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                PerformanceModeSelector(
                    selectedMode = userPreferences.selectedProfileType,
                    onModeSelected = onSelectDefaultProfile
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Profile applied by default to newly launched game sessions",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                Spacer(modifier = Modifier.height(16.dp))

                // Preferred Refresh Rate
                Text(
                    text = "PREFERRED REFRESH RATE",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                RefreshRateSelector(
                    currentRate = userPreferences.targetRefreshRate,
                    selectedTarget = userPreferences.targetRefreshRate,
                    supportedRates = supportedRefreshRates,
                    onSelectRate = onSelectPreferredRefreshRate
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Target display frequency set in Android display compositor",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                Spacer(modifier = Modifier.height(16.dp))

                // Auto-Revert
                SettingsSwitchRow(
                    title = "Auto-Revert",
                    subtitle = "Revert display rates and animation scales when gaming session ends",
                    checked = userPreferences.isRestoreOnExitEnabled,
                    onCheckedChange = onToggleRestoreOnExit
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Session Monitoring
                SettingsSwitchRow(
                    title = "Session Monitoring",
                    subtitle = "Track active gaming sessions and collect hardware telemetry",
                    checked = userPreferences.isSessionMonitoringEnabled,
                    onCheckedChange = onToggleSessionMonitoring
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notifications
                SettingsSwitchRow(
                    title = "Notifications",
                    subtitle = "Show background optimization status and completion notices",
                    checked = userPreferences.isNotificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Startup Screen
                SettingsSwitchRow(
                    title = "Startup Screen",
                    subtitle = "Display quick attribution splash on cold application launch",
                    checked = userPreferences.showStartupScreen,
                    onCheckedChange = onToggleStartupScreen
                )

                if (onRestoreDefault != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onRestoreDefault,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESTORE SYSTEM BASELINE DEFAULTS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 2. PRIVILEGED BACKEND & WIRELESS ADB
            // ==========================================
            SectionHeader(title = "PRIVILEGED BACKEND")
            GlassCard(backgroundColor = SurfaceElevated) {
                // Shizuku
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SHIZUKU IPC",
                            color = TextPrimary,
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

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRecheckShizuku,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RECHECK", fontSize = 11.sp)
                    }
                    Button(
                        onClick = onOpenShizukuSetup,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SETUP GUIDE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                // Wireless ADB Pairing & Connect
                var pairingCodeInput by remember { mutableStateOf("") }
                var pairingPortInput by remember { mutableStateOf("") }
                var connectPortInput by remember { mutableStateOf("") }

                val isAdbConnected = wirelessAdbState?.status == com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED
                val adbStatusText = when (wirelessAdbState?.status) {
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED -> "CONNECTED"
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTING -> "CONNECTING..."
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.PAIRING -> "PAIRING..."
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.PAIRED -> "PAIRED"
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.AUTHENTICATING -> "AUTHENTICATING..."
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTION_FAILED -> "FAILED"
                    else -> "DISCONNECTED"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WIRELESS DEBUGGING (FALLBACK)",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAdbConnected) "TLS loopback active on port ${wirelessAdbState.connectedPort}"
                            else "Android 11+ direct on-device shell fallback (No PC required)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusBadge(
                        text = adbStatusText,
                        state = if (isAdbConnected) BadgeState.SUCCESS else BadgeState.NEUTRAL
                    )
                }

                if (adbOperationStatus != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = adbOperationStatus,
                        color = AccentPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isAdbConnected) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onDisconnectWirelessAdb?.invoke() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusWarning),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DISCONNECT ADB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Step 1: Pair with Device",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pairingCodeInput,
                            onValueChange = { if (it.length <= 6) pairingCodeInput = it },
                            label = { Text("Code (6 digits)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPrimary,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = pairingPortInput,
                            onValueChange = { if (it.length <= 5) pairingPortInput = it },
                            label = { Text("Port", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.9f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPrimary,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Button(
                            onClick = {
                                val port = pairingPortInput.toIntOrNull() ?: 0
                                if (pairingCodeInput.length == 6 && port in 1024..65535) {
                                    onPairWirelessAdb?.invoke(pairingCodeInput, port)
                                }
                            },
                            enabled = pairingCodeInput.length == 6 && (pairingPortInput.toIntOrNull() ?: 0) in 1024..65535,
                            modifier = Modifier.height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PAIR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Step 2: Connect to Main Port",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = connectPortInput,
                            onValueChange = { if (it.length <= 5) connectPortInput = it },
                            label = { Text("Connect Port", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPrimary,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Button(
                            onClick = {
                                val port = connectPortInput.toIntOrNull() ?: 0
                                if (port in 1024..65535) {
                                    onConnectWirelessAdb?.invoke(port)
                                }
                            },
                            enabled = (connectPortInput.toIntOrNull() ?: 0) in 1024..65535,
                            modifier = Modifier.height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusReady, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CONNECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. ABOUT
            // ==========================================
            SectionHeader(title = "ABOUT")
            GlassCard(backgroundColor = SurfaceElevated) {
                // GameBoost Version
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_game_boost_emblem),
                        contentDescription = "GameBoost Logo",
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GameBoost",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Version 1.0.3 • Release",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                // Creator Attribution: Created by Rohit B.K
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, BorderSubtle, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.creator_profile),
                            contentDescription = "Rohit B.K",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Created by Rohit B.K",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gaming Performance & System Utility",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkBg,
                checkedTrackColor = AccentPrimary
            )
        )
    }
}
