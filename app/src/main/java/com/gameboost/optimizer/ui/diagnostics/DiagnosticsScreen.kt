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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.gameboost.optimizer.theme.CardBorder
import com.gameboost.optimizer.theme.CardNavy
import com.gameboost.optimizer.theme.CardNavyElevated
import com.gameboost.optimizer.theme.CyberCyan
import com.gameboost.optimizer.theme.NeonGreen
import com.gameboost.optimizer.theme.NeonRed
import com.gameboost.optimizer.theme.ObsidianBg
import com.gameboost.optimizer.theme.TextGray
import com.gameboost.optimizer.theme.TextMuted
import com.gameboost.optimizer.theme.TextWhite
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.SectionHeader

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
        color = ObsidianBg
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
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SYSTEM DIAGNOSTICS",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hardware, Shizuku, and Display Mode Matrix",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shizuku Service Telemetry
            SectionHeader(title = "Shizuku Service Status")
            GlassCard {
                DiagRow("Installed", if (shizukuStatus.isInstalled) "YES (Package found)" else "NO")
                DiagRow("Service Running (pingBinder)", if (shizukuStatus.isRunning) "YES" else "NO (Stopped)")
                DiagRow("App Authorized", if (shizukuStatus.isAuthorized) "YES (Permission granted)" else "NO")
                DiagRow("Server Version", if (shizukuStatus.version > 0) "v${shizukuStatus.version}" else "N/A")
                DiagRow("Service UID", if (shizukuStatus.uid >= 0) "${shizukuStatus.uid}" else "N/A")
                if (shizukuStatus.errorMessage != null) {
                    DiagRow("Last Error", shizukuStatus.errorMessage, isError = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Device & Hardware
            SectionHeader(title = "Hardware & SoC")
            GlassCard {
                DiagRow("Manufacturer", capabilities.manufacturer)
                DiagRow("Model", capabilities.model)
                DiagRow("SoC / Hardware", capabilities.socHardware)
                DiagRow("CPU Cores", "${capabilities.cpuCores}")
                DiagRow("Total RAM", capabilities.formattedRam)
                DiagRow("Android Version", "${capabilities.androidVersion} (API ${capabilities.apiLevel})")
                DiagRow("OEM Skin", capabilities.oemSkin)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Modes Matrix
            SectionHeader(
                title = "Display Mode Matrix",
                subtitle = "${capabilities.displayState.supportedModes.size} hardware modes detected"
            )
            GlassCard {
                DiagRow("Current Refresh Rate", "${capabilities.displayState.currentRefreshRate.toInt()}Hz")
                DiagRow("Maximum Supported Rate", "${capabilities.displayState.maxRefreshRate.toInt()}Hz")
                DiagRow("Panel Resolution", "${capabilities.displayState.resolutionWidth} x ${capabilities.displayState.resolutionHeight}")

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                Text(
                    text = "SUPPORTED HARDWARE MODES",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                capabilities.displayState.supportedModes.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mode #${mode.modeId}: ${mode.width}x${mode.height}",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${mode.refreshRate.toInt()}Hz",
                            color = if (mode.refreshRate >= 119f) NeonGreen else TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Capability Matrix
            SectionHeader(title = "Device Capability Matrix")
            GlassCard {
                capabilities.capabilityMatrix.forEach { cap ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = cap.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = cap.detail, color = TextMuted, fontSize = 11.sp)
                        }
                        Icon(
                            imageVector = if (cap.isSupported) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (cap.isSupported) NeonGreen else NeonRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reversible Backup State
            SectionHeader(title = "Reversible Session Backup")
            GlassCard {
                if (activeBackup != null) {
                    DiagRow("peak_refresh_rate backup", activeBackup.peakRefreshRate ?: "Default / null")
                    DiagRow("min_refresh_rate backup", activeBackup.minRefreshRate ?: "Default / null")
                    DiagRow("user_refresh_rate backup", activeBackup.userRefreshRate ?: "Default / null")
                    DiagRow("window_animation_scale backup", activeBackup.windowAnimationScale ?: "1.0")
                    DiagRow("transition_animation_scale backup", activeBackup.transitionAnimationScale ?: "1.0")
                    DiagRow("animator_duration_scale backup", activeBackup.animatorDurationScale ?: "1.0")
                } else {
                    Text(
                        text = "No active optimization session. Baseline state will be captured when 120Hz boost is activated.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last Optimization Result Log
            if (lastResult != null) {
                SectionHeader(title = "Last Optimization Result")
                GlassCard {
                    DiagRow("Game", lastResult.gameName)
                    DiagRow("Target Rate", "${lastResult.requestedRefreshRate.toInt()}Hz")
                    DiagRow("Actual Verified Rate", "${lastResult.actualRefreshRate.toInt()}Hz")
                    DiagRow("Status", lastResult.verificationSummary)
                    if (lastResult.appliedSettings.isNotEmpty()) {
                        DiagRow("Applied Items", lastResult.appliedSettings.joinToString(", "))
                    }
                    if (lastResult.failedSettings.isNotEmpty()) {
                        DiagRow("Failed Items", lastResult.failedSettings.joinToString(", "), isError = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DiagRow(label: String, value: String, isError: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextGray, fontSize = 12.sp)
        Text(
            text = value,
            color = if (isError) NeonRed else TextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
