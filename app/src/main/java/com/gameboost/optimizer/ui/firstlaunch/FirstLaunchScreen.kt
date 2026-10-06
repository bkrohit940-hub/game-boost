package com.gameboost.optimizer.ui.firstlaunch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.CardBorder
import com.gameboost.optimizer.theme.CardNavy
import com.gameboost.optimizer.theme.CardNavyElevated
import com.gameboost.optimizer.theme.CyberCyan
import com.gameboost.optimizer.theme.ElectricPurple
import com.gameboost.optimizer.theme.NeonGreen
import com.gameboost.optimizer.theme.ObsidianBg
import com.gameboost.optimizer.theme.TextGray
import com.gameboost.optimizer.theme.TextMuted
import com.gameboost.optimizer.theme.TextWhite
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun FirstLaunchScreen(
    capabilities: DeviceCapabilities,
    shizukuStatus: ShizukuStatus,
    onContinueToShizuku: () -> Unit,
    onContinueToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasStartedScan by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ObsidianBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Brand Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(CyberCyan, ElectricPurple)),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GAMEBOOST",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "Gaming Performance Optimizer",
                color = CyberCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Optimize your Android device for PUBG Mobile, BGMI, and Korean PUBG with genuine 120Hz display locking.",
                color = TextGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!hasStartedScan) {
                // Value Propositions
                GlassCard {
                    FeatureCheckRow("120Hz display optimization where supported")
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCheckRow("Legitimate Android & Shizuku system control")
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCheckRow("Automatic PUBG / BGMI package detection")
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCheckRow("One-time authorization (remembered state)")
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCheckRow("Reversible session backup & auto-restore")
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCheckRow("No APK/OBB hacking, no anti-cheat tampering")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { hasStartedScan = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = ObsidianBg
                    )
                ) {
                    Text(
                        text = "GET STARTED",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                // Animated Device Capability Scan Results
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        GlassCard {
                            Text(
                                text = "DEVICE HARDWARE SCAN",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            ScanDetailRow("Device", "${capabilities.manufacturer} ${capabilities.model}")
                            ScanDetailRow("Android OS", "Android ${capabilities.androidVersion} (API ${capabilities.apiLevel})")
                            ScanDetailRow("OEM Environment", capabilities.oemSkin)
                            ScanDetailRow("CPU / SoC", "${capabilities.socHardware} (${capabilities.cpuCores} cores)")
                            ScanDetailRow("Total RAM", capabilities.formattedRam)

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = CardBorder
                            )

                            Text(
                                text = "DISPLAY CAPABILITIES",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            ScanDetailRow("Current Refresh Rate", "${capabilities.displayState.currentRefreshRate.toInt()}Hz")
                            ScanDetailRow("Maximum Refresh Rate", "${capabilities.displayState.maxRefreshRate.toInt()}Hz")
                            ScanDetailRow(
                                "120Hz Support",
                                if (capabilities.displayState.supports120Hz) "Hardware Supported ✓" else "Not Supported on Panel ✕"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = CardBorder
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SHIZUKU PRIVILEGED API",
                                        color = CyberCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (shizukuStatus.isAuthorized) "Authorization active" else "Requires one-time permission",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                }

                                val badgeState = when {
                                    shizukuStatus.isAuthorized -> BadgeState.SUCCESS
                                    shizukuStatus.isRunning -> BadgeState.WARNING
                                    else -> BadgeState.ERROR
                                }
                                StatusBadge(text = shizukuStatus.summaryText, state = badgeState)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (shizukuStatus.isAuthorized) {
                            Button(
                                onClick = onContinueToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonGreen,
                                    contentColor = ObsidianBg
                                )
                            ) {
                                Text(
                                    text = "CONTINUE TO DASHBOARD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = onContinueToShizuku,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberCyan,
                                    contentColor = ObsidianBg
                                )
                            ) {
                                Text(
                                    text = "SET UP SHIZUKU",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onContinueToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, CardBorder)
                            ) {
                                Text(
                                    text = "CONTINUE WITHOUT SHIZUKU (READ-ONLY)",
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureCheckRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ScanDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
