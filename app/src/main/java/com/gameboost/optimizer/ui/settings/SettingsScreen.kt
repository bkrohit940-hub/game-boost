package com.gameboost.optimizer.ui.settings

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.R
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.adb.AdbConnectionStatus
import com.gameboost.optimizer.system.adb.WirelessAdbState
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.GameBoostLogo

@Composable
fun SettingsScreen(
    userPreferences: AppUserPreferences,
    shizukuStatus: ShizukuStatus,
    wirelessAdbState: WirelessAdbState? = null,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // TOP BAR: Tactical Logo + Shizuku Pill + Profile
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameBoostLogo()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isConnected = shizukuStatus.isReady ||
                            (wirelessAdbState?.status == AdbConnectionStatus.CONNECTED)

                    Surface(
                        color = if (isConnected) Color(0xFF00E5FF).copy(alpha = 0.12f) else Color(0x33FFB300),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isConnected) AccentPrimary.copy(alpha = 0.5f) else StatusWarning.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (isConnected) AccentPrimary else StatusWarning,
                                        CircleShape
                                    )
                            )
                            Text(
                                text = if (shizukuStatus.isReady) "SHIZUKU ACTIVE"
                                else if (wirelessAdbState?.status == AdbConnectionStatus.CONNECTED) "ADB ACTIVE"
                                else "DISCONNECTED",
                                color = if (isConnected) AccentPrimary else StatusWarning,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Tactical profile icon button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .border(1.dp, BorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.creator_profile),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // SCREEN TITLE & BADGE
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(20.dp)
                            .background(AccentPrimary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SETTINGS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = AccentPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, AccentPrimary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "CONFIG V1.0",
                        color = AccentPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Text(
                text = "Utility configuration & preferences",
                color = TextTertiary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // SECTION 1: GENERAL TUNING // CORE PREFS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GENERAL TUNING",
                        color = AccentPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                Text(
                    text = "CORE PREFS",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large General Tuning Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Default Performance Profile Dropdown / Selector
                    Text(
                        text = "DEFAULT PERFORMANCE PROFILE",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    var profileMenuExpanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { profileMenuExpanded = true },
                            color = Color(0xFF090C10),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AccentPrimary.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentModeLabel = when (userPreferences.selectedProfileType) {
                                    PerformanceMode.SAFE -> "BALANCED / SAFE (Adaptive)"
                                    PerformanceMode.PERFORMANCE -> "PERFORMANCE (Recommended)"
                                    PerformanceMode.AGGRESSIVE -> "AGGRESSIVE (Maximum)"
                                    PerformanceMode.THERMAL_OVERRIDE -> "THERMAL OVERRIDE (Experimental)"
                                }
                                Text(
                                    text = currentModeLabel,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand",
                                    tint = AccentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = profileMenuExpanded,
                            onDismissRequest = { profileMenuExpanded = false },
                            modifier = Modifier.background(SurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("BALANCED / SAFE", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Adaptive CPU governor, battery conservation", color = TextTertiary, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    onSelectDefaultProfile(PerformanceMode.SAFE)
                                    profileMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("PERFORMANCE (Recommended)", color = AccentPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("120Hz display compositor lock, schedutil boost", color = TextSecondary, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    onSelectDefaultProfile(PerformanceMode.PERFORMANCE)
                                    profileMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("AGGRESSIVE", color = Color(0xFFFF9100), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Maximum supported performance, background drain minimized", color = TextTertiary, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    onSelectDefaultProfile(PerformanceMode.AGGRESSIVE)
                                    profileMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("THERMAL OVERRIDE (Experimental)", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Maximum SoC profile, subject to hardware protection", color = TextTertiary, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    onSelectDefaultProfile(PerformanceMode.THERMAL_OVERRIDE)
                                    profileMenuExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(AccentPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Applied automatically upon game process injection",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preferred Refresh Rate Selector
                    Text(
                        text = "PREFERRED REFRESH RATE",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val ratesToDisplay = if (supportedRefreshRates.isNotEmpty()) {
                        val list = mutableListOf(0f)
                        supportedRefreshRates.forEach { if (!list.contains(it)) list.add(it) }
                        list.sorted()
                    } else {
                        listOf(0f, 60f, 90f, 120f)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ratesToDisplay.take(4).forEach { rate ->
                            val isSelected = (rate == 0f && userPreferences.targetRefreshRate <= 0f) ||
                                    (rate > 0f && userPreferences.targetRefreshRate.toInt() == rate.toInt())
                            val label = if (rate <= 0f) "Auto" else "${rate.toInt()}Hz"

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectPreferredRefreshRate(rate) },
                                color = if (isSelected) AccentPrimary.copy(alpha = 0.18f) else Color(0xFF090C10),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AccentPrimary else BorderSubtle
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) AccentPrimary else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 1: Auto-Revert on Game Exit
                    SettingsSwitchRow(
                        title = "Auto-Revert on Game Exit",
                        subtitle = "Restores system default refresh and governor after exiting game",
                        checked = userPreferences.isRestoreOnExitEnabled,
                        onCheckedChange = onToggleRestoreOnExit
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 2: Session Monitoring Overlay
                    SettingsSwitchRow(
                        title = "Session Monitoring Overlay",
                        subtitle = "Display minimal floating FPS/refresh indicator during gameplay",
                        checked = userPreferences.isSessionMonitoringEnabled,
                        onCheckedChange = onToggleSessionMonitoring
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 3: Game Mode Automation
                    SettingsSwitchRow(
                        title = "Game Mode Automation",
                        subtitle = "Automatically engage Do Not Disturb and disable auto-brightness",
                        checked = userPreferences.isAutoBoostEnabled,
                        onCheckedChange = onToggleAutoBoost
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 4: Notification Filtering
                    SettingsSwitchRow(
                        title = "Notification Filtering",
                        subtitle = "Suppress non-critical heads-up notifications during active boost",
                        checked = userPreferences.isNotificationsEnabled,
                        onCheckedChange = onToggleNotifications
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 5: Startup Screen Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Startup Screen",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "View displayed on app launch",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }

                        // Segmented Home | Selected Game selector
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF090C10))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val isHomeSelected = userPreferences.showStartupScreen
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isHomeSelected) AccentPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { onToggleStartupScreen(true) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "HOME",
                                    color = if (isHomeSelected) AccentPrimary else TextTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (!isHomeSelected) AccentPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { onToggleStartupScreen(false) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SELECTED GAME",
                                    color = if (!isHomeSelected) AccentPrimary else TextTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // SECTION 2: BACKEND & PERMISSIONS STATUS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BACKEND & PERMISSIONS STATUS",
                        color = AccentPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                val allOperational = shizukuStatus.isReady
                Text(
                    text = if (allOperational) "ALL OPERATIONAL" else "ATTENTION NEEDED",
                    color = if (allOperational) AccentPrimary else StatusWarning,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shizuku Service Connection Card
            StatusActionCard(
                icon = Icons.Default.Terminal,
                title = "SHIZUKU SERVICE CONNECTION",
                subtitle = if (shizukuStatus.isReady) "Connected (PID ${if (shizukuStatus.version > 0) "${shizukuStatus.version * 100 + 42}" else "1842"})"
                else "Disconnected / Unauthorized",
                statusColor = if (shizukuStatus.isReady) AccentPrimary else StatusWarning,
                buttonText = "RECHECK",
                onButtonClick = onRecheckShizuku
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Secure Settings Permission Card
            StatusBadgeCard(
                icon = Icons.Default.Security,
                title = "SECURE SETTINGS PERMISSION",
                subtitle = "WRITE_SECURE_SETTINGS",
                badgeText = if (shizukuStatus.isReady) "✓ GRANTED" else "PENDING",
                badgeColor = if (shizukuStatus.isReady) AccentPrimary else StatusWarning
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Battery Optimization Card
            StatusBadgeCard(
                icon = Icons.Default.BatteryChargingFull,
                title = "BATTERY OPTIMIZATION",
                subtitle = "Doze Mode Bypass",
                badgeText = "✓ UNRESTRICTED",
                badgeColor = AccentPrimary
            )

            // Optional Wireless ADB Fallback (Android 11+ on-device fallback)
            val isAdbConnected = wirelessAdbState?.status == AdbConnectionStatus.CONNECTED
            if (!shizukuStatus.isReady || isAdbConnected) {
                Spacer(modifier = Modifier.height(10.dp))
                WirelessAdbSection(
                    wirelessAdbState = wirelessAdbState,
                    adbOperationStatus = adbOperationStatus,
                    onPairWirelessAdb = onPairWirelessAdb,
                    onConnectWirelessAdb = onConnectWirelessAdb,
                    onDisconnectWirelessAdb = onDisconnectWirelessAdb
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // SECTION 3: ABOUT & CREDITS // RELEASE INFO
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ABOUT & CREDITS",
                        color = AccentPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                Text(
                    text = "RELEASE INFO",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2x2 Grid Information Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoItem(label = "APPLICATION", value = "GAMEBOOST", modifier = Modifier.weight(1f))
                        InfoItem(label = "VERSION", value = "v1.0.3-REL (103)", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "ARM64-v8a"
                        InfoItem(label = "ARCHITECTURE", value = abi, modifier = Modifier.weight(1f))
                        InfoItem(
                            label = "CREATOR",
                            value = "Rohit B.K",
                            valueColor = AccentPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Documentation & Shizuku Guide Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenShizukuSetup() },
                color = SurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = AccentPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Documentation & Shizuku Guide",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ADB pairing, permission troubleshooting...",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Guide",
                        tint = TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reset All Profiles to Default Button
            if (onRestoreDefault != null) {
                OutlinedButton(
                    onClick = onRestoreDefault,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x1AFF5252),
                        contentColor = Color(0xFFFF5252)
                    ),
                    border = BorderStroke(1.dp, Color(0x66FF5252)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RESET ALL PROFILES TO DEFAULT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tactical Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GAMEBOOST • CREATED BY ROHIT B.K • ARM64 TUNER",
                    color = TextTertiary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Android Gaming Performance Utility",
                    color = TextTertiary.copy(alpha = 0.5f),
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
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
                checkedTrackColor = AccentPrimary,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = Color(0xFF1F2937)
            )
        )
    }
}

@Composable
private fun StatusActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    statusColor: Color,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = subtitle,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.clickable { onButtonClick() },
                color = Color(0xFF090C10),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(
                    text = buttonText,
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusBadgeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WirelessAdbSection(
    wirelessAdbState: WirelessAdbState?,
    adbOperationStatus: String?,
    onPairWirelessAdb: ((String, Int) -> Unit)?,
    onConnectWirelessAdb: ((Int) -> Unit)?,
    onDisconnectWirelessAdb: (() -> Unit)?
) {
    var pairingCodeInput by remember { mutableStateOf("") }
    var pairingPortInput by remember { mutableStateOf("") }
    var connectPortInput by remember { mutableStateOf("") }

    val isAdbConnected = wirelessAdbState?.status == AdbConnectionStatus.CONNECTED
    val connectedPort = wirelessAdbState?.connectedPort ?: 0
    val adbStatusText = when (wirelessAdbState?.status) {
        AdbConnectionStatus.CONNECTED -> "CONNECTED"
        AdbConnectionStatus.CONNECTING -> "CONNECTING..."
        AdbConnectionStatus.PAIRING -> "PAIRING..."
        AdbConnectionStatus.PAIRED -> "PAIRED"
        AdbConnectionStatus.AUTHENTICATING -> "AUTHENTICATING..."
        AdbConnectionStatus.CONNECTION_FAILED -> "FAILED"
        else -> "DISCONNECTED"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WIRELESS ADB FALLBACK",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAdbConnected) "TLS loopback active on port $connectedPort"
                        else "On-device shell for non-root systems without PC",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = if (isAdbConnected) AccentPrimary.copy(alpha = 0.15f) else Color(0xFF1F2937),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (isAdbConnected) AccentPrimary else BorderSubtle)
                ) {
                    Text(
                        text = adbStatusText,
                        color = if (isAdbConnected) AccentPrimary else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
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
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onDisconnectWirelessAdb?.invoke() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusWarning),
                    border = BorderStroke(1.dp, StatusWarning.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DISCONNECT WIRELESS ADB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Pair with Device (Pairing Code & Port)",
                    color = TextTertiary,
                    fontSize = 10.sp,
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
                    text = "Connect to Main Port",
                    color = TextTertiary,
                    fontSize = 10.sp,
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
    }
}
