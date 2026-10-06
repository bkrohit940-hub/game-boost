package com.gameboost.optimizer.ui.settings

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.CardBorder
import com.gameboost.optimizer.theme.CardNavy
import com.gameboost.optimizer.theme.CyberAmber
import com.gameboost.optimizer.theme.CyberCyan
import com.gameboost.optimizer.theme.NeonGreen
import com.gameboost.optimizer.theme.ObsidianBg
import com.gameboost.optimizer.theme.TextGray
import com.gameboost.optimizer.theme.TextMuted
import com.gameboost.optimizer.theme.TextWhite
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun SettingsScreen(
    userPreferences: AppUserPreferences,
    shizukuStatus: ShizukuStatus,
    onToggleAutoBoost: (Boolean) -> Unit,
    onToggleRestoreOnExit: (Boolean) -> Unit,
    onRecheckShizuku: () -> Unit,
    onOpenShizukuSetup: () -> Unit,
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
                Text(
                    text = "SETTINGS",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CRITICAL: Honest FPS vs 120Hz Educational Section (Section 18)
            GlassCard(
                border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyberAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "IMPORTANT: 120Hz vs 120 FPS",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "• 120Hz = Display panel refreshes up to 120 times per second.\n\n" +
                            "• 120 FPS = Game engine actively renders up to 120 frames per second.\n\n" +
                            "• 120Hz display does not automatically mean PUBG is rendering at 120 FPS.\n\n" +
                            "• GameBoost optimizes the device's display configuration and supported system settings, but it cannot guarantee a game's internal FPS mode.",
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Automation Settings
            SectionHeader(title = "Background Automation")

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto Game Optimization", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Automatically apply 120Hz when PUBG/BGMI launches", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = userPreferences.isAutoBoostEnabled,
                        onCheckedChange = onToggleAutoBoost,
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBg, checkedTrackColor = CyberCyan)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto-Restore on Exit", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Revert display rate and animation scales when game exits", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = userPreferences.isRestoreOnExitEnabled,
                        onCheckedChange = onToggleRestoreOnExit,
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBg, checkedTrackColor = CyberCyan)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shizuku Service Management
            SectionHeader(title = "Shizuku Service & Authorization")

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Authorization State", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (shizukuStatus.isAuthorized) "Authorized (v${shizukuStatus.version})" else "Not Authorized",
                            color = if (shizukuStatus.isAuthorized) NeonGreen else CyberAmber,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    StatusBadge(
                        text = if (shizukuStatus.isAuthorized) "ACTIVE" else "REQUIRED",
                        state = if (shizukuStatus.isAuthorized) BadgeState.SUCCESS else BadgeState.WARNING
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRecheckShizuku,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RECHECK", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onOpenShizukuSetup,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SETUP GUIDE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Info
            GlassCard {
                Text(text = "GameBoost v1.0", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Built strictly around legitimate Android public APIs and Shizuku. No root exploits, no game binary tampering, no anti-cheat bypasses.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
