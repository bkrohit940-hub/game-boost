package com.gameboost.optimizer.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import com.gameboost.optimizer.R
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.adb.AdbConnectionStatus
import com.gameboost.optimizer.system.adb.WirelessAdbState
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.DeepRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreAngularShape
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameBoostLogo
import com.gameboost.optimizer.ui.components.PerformanceModeSelector
import com.gameboost.optimizer.ui.components.RedCoreCard
import com.gameboost.optimizer.ui.components.RedCoreSwitch
import com.gameboost.optimizer.ui.components.RefreshRateSelector
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

/**
 * SETTINGS: GAMING CONFIGURATION (Section 10)
 * RedMagic-inspired compact gaming configuration:
 * - Default Performance Profile (BALANCED, PERFORMANCE, EXTREME)
 * - Preferred Refresh Rate (60Hz, 90Hz, 120Hz, 144Hz)
 * - Auto-Revert on Game Exit (Mechanical red switch)
 * - Session Monitoring (Mechanical red switch)
 * - Notifications (Mechanical red switch)
 * - Startup Animation (Mechanical red switch)
 * - Privileged Backends (Shizuku & Wireless ADB)
 * - About GameBoost with "Created by Rohit B.K"
 */
@Composable
fun SettingsScreen(
    userPreferences: AppUserPreferences,
    shizukuStatus: ShizukuStatus,
    wirelessAdbState: WirelessAdbState? = null,
    adbOperationStatus: String? = null,
    supportedRefreshRates: List<Float> = emptyList(),
    isGridLayout: Boolean = true,
    onToggleGridLayout: (Boolean) -> Unit = {},
    appSortOrder: String = "NAME_ASC",
    onSetAppSortOrder: (String) -> Unit = {},
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
    var pairingCodeInput by remember { mutableStateOf("") }
    var pairingPortInput by remember { mutableStateOf("") }
    var connectPortInput by remember { mutableStateOf("") }
    var showAdbSection by remember { mutableStateOf(false) }

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
                // 1. SCREEN HEADER
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
                                        text = "GAMING CONFIGURATION",
                                        color = RedCoreTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "USER PREFERENCES & SUBSYSTEM PARAMETERS",
                                    color = RedCoreTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        StatusBadge(text = "SAVED", state = BadgeState.SUCCESS)
                    }
                }

                // =========================================================================
                // 2. DEFAULT PERFORMANCE PROFILE (SECTION 10)
                // =========================================================================
                item {
                    SectionHeader(
                        title = "DEFAULT PERFORMANCE PROFILE",
                        subtitle = "Hardware tuning applied automatically upon boost"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    PerformanceModeSelector(
                        selectedMode = userPreferences.selectedProfileType,
                        onModeSelected = onSelectDefaultProfile
                    )
                }

                // =========================================================================
                // 3. PREFERRED REFRESH RATE (SECTION 10)
                // =========================================================================
                item {
                    SectionHeader(
                        title = "PREFERRED REFRESH RATE",
                        subtitle = "Target panel frequency to lock during active gameplay"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    RefreshRateSelector(
                        currentRate = userPreferences.targetRefreshRate,
                        selectedTarget = userPreferences.targetRefreshRate,
                        supportedRates = supportedRefreshRates,
                        onSelectRate = onSelectPreferredRefreshRate
                    )
                }

                // =========================================================================
                // 4. CORE ENGINE TOGGLES (MECHANICAL DARK SWITCHES WITH RED ACTIVE GLOW)
                // Auto-Revert, Session Monitoring, Notifications, Startup Animation
                // =========================================================================
                item {
                    SectionHeader(
                        title = "AUTOMATION & LIFECYCLE",
                        subtitle = "Runtime hooks and state maintenance"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    RedCoreCard(
                        backgroundColor = RedCoreSurfaceElevated,
                        borderColor = RedCoreBorder
                    ) {
                        // 1. Auto-Revert on Game Exit
                        SettingToggleRow(
                            title = "Auto-Revert on Game Exit",
                            description = "Restores original display refresh rate and power governor immediately when game exits.",
                            checked = userPreferences.isRestoreOnExitEnabled,
                            onCheckedChange = onToggleRestoreOnExit
                        )

                        SettingsDivider()

                        // 2. Session Monitoring
                        SettingToggleRow(
                            title = "Foreground Session Monitoring",
                            description = "Monitors active game window to detect minimization and background sleep transitions.",
                            checked = userPreferences.isSessionMonitoringEnabled,
                            onCheckedChange = onToggleSessionMonitoring
                        )

                        SettingsDivider()

                        // 3. Background Notifications
                        SettingToggleRow(
                            title = "Status Notifications",
                            description = "Displays low-priority persistent HUD notification during active optimization.",
                            checked = userPreferences.isNotificationsEnabled,
                            onCheckedChange = onToggleNotifications
                        )

                        SettingsDivider()

                        // 4. Startup Animation
                        SettingToggleRow(
                            title = "Branded Startup Animation",
                            description = "Plays the fast Red Core emblem intro on cold application launch.",
                            checked = userPreferences.showStartupScreen,
                            onCheckedChange = onToggleStartupScreen
                        )
                    }
                }

                // =========================================================================
                // 5. PRIVILEGED BACKEND (SHIZUKU & WIRELESS ADB)
                // =========================================================================
                item {
                    SectionHeader(
                        title = "PRIVILEGED BACKENDS",
                        subtitle = "IPC binder and local loopback debugging bridges"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    RedCoreCard(
                        backgroundColor = RedCoreSurfaceElevated,
                        borderColor = if (shizukuStatus.isReady) RedCoreSuccess else RedCoreBorder
                    ) {
                        // Shizuku Status Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SHIZUKU PRIVILEGED SERVICE",
                                    color = RedCoreTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (shizukuStatus.isReady)
                                        "Connected via system binder IPC."
                                    else
                                        "Required for display rate lock & process priority.",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (shizukuStatus.isReady) onRecheckShizuku() else onOpenShizukuSetup()
                                },
                                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (shizukuStatus.isReady) RedCoreDarkCrimson else PrimaryGamingRed,
                                    contentColor = if (shizukuStatus.isReady) BrightActiveRed else Color.White
                                ),
                                border = if (shizukuStatus.isReady) BorderStroke(1.dp, BrightActiveRed) else null,
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (shizukuStatus.isReady) "RECHECK" else "SETUP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        SettingsDivider()

                        // Wireless ADB toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAdbSection = !showAdbSection },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "WIRELESS DEBUGGING (DIRECT SHELL)",
                                    color = RedCoreTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Loopback TLS ADB pairing for rootless privileged commands.",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                            StatusBadge(
                                text = if (showAdbSection) "CLOSE" else "CONFIGURE",
                                state = BadgeState.NEUTRAL
                            )
                        }

                        if (showAdbSection) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = CutCornerShape(4.dp),
                                color = RedCoreBgSecondary,
                                border = BorderStroke(1.dp, RedCoreBorder)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "PAIR WIRELESS DEBUGGING",
                                        color = BrightActiveRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = pairingCodeInput,
                                            onValueChange = { pairingCodeInput = it },
                                            label = { Text("Code", fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrightActiveRed,
                                                unfocusedBorderColor = RedCoreBorder
                                            )
                                        )
                                        OutlinedTextField(
                                            value = pairingPortInput,
                                            onValueChange = { pairingPortInput = it },
                                            label = { Text("Port", fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrightActiveRed,
                                                unfocusedBorderColor = RedCoreBorder
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            val code = pairingCodeInput.trim()
                                            val port = pairingPortInput.trim().toIntOrNull()
                                            if (code.isNotEmpty() && port != null) {
                                                onPairWirelessAdb?.invoke(code, port)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed),
                                        shape = CutCornerShape(4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("PAIR DEVICE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 5.5. BOBO LAUNCHER PREFERENCES
                // =========================================================================
                item {
                    val context = LocalContext.current
                    SectionHeader(
                        title = "BOBO LAUNCHER CONFIGURATION",
                        subtitle = "Gaming command center drawer & system home options"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    RedCoreCard(
                        backgroundColor = RedCoreSurfaceElevated,
                        borderColor = RedCoreBorder
                    ) {
                        SettingToggleRow(
                            title = "DRAWER GRID LAYOUT",
                            description = "Display installed apps and games in a multi-column responsive grid instead of a compact list.",
                            checked = isGridLayout,
                            onCheckedChange = onToggleGridLayout
                        )

                        SettingsDivider()

                        SettingToggleRow(
                            title = "ALPHABETICAL APP SORTING",
                            description = "Sort applications alphabetically (A to Z) or by recent/installed priority.",
                            checked = appSortOrder == "NAME_ASC",
                            onCheckedChange = { isAlpha ->
                                onSetAppSortOrder(if (isAlpha) "NAME_ASC" else "DEFAULT")
                            }
                        )

                        SettingsDivider()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DEFAULT HOME LAUNCHER",
                                    color = RedCoreTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Opt into using BOBO Launcher as your primary Android home screen. Opens Android role settings.",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        try {
                                            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                },
                                shape = CutCornerShape(4.dp),
                                border = BorderStroke(1.dp, BrightActiveRed),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightActiveRed)
                            ) {
                                Text("SET HOME", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // =========================================================================
                // 6. ABOUT GAMEBOOST (SECTION 10 & 15)
                // Logo, attribution "Created by Rohit B.K", application specifications
                // =========================================================================
                item {
                    SectionHeader(
                        title = "ABOUT GAMEBOOST",
                        subtitle = "Application build & authorship identity"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    RedCoreCard(
                        backgroundColor = RedCoreSurfaceElevated,
                        borderColor = RedCoreBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GameBoostLogo(sizeDp = 48.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "GAMEBOOST RED CORE",
                                    color = RedCoreTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Native Android Gaming Optimization Center",
                                    color = BrightActiveRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "Version 1.0.5 (BOBO Launcher Edition)",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        SettingsDivider()

                        // Creator Attribution: "Created by Rohit B.K"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RedCoreBgSecondary, CutCornerShape(4.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, BrightActiveRed, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.creator_profile),
                                    contentDescription = "Rohit B.K",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Created by Rohit B.K",
                                    color = RedCoreTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Architect & Lead Engineer",
                                    color = RedCoreTextSecondary,
                                    fontSize = 10.sp
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
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = RedCoreTextPrimary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = RedCoreTextSecondary,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        RedCoreSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = RedCoreBorderSubtle,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
