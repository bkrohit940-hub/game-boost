package com.gameboost.optimizer.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.CardBorder
import com.gameboost.optimizer.theme.CardNavy
import com.gameboost.optimizer.theme.CardNavyElevated
import com.gameboost.optimizer.theme.CyberAmber
import com.gameboost.optimizer.theme.CyberAmberContainer
import com.gameboost.optimizer.theme.CyberCyan
import com.gameboost.optimizer.theme.CyberCyanContainer
import com.gameboost.optimizer.theme.ElectricPurple
import com.gameboost.optimizer.theme.NeonGreen
import com.gameboost.optimizer.theme.NeonGreenContainer
import com.gameboost.optimizer.theme.NeonRed
import com.gameboost.optimizer.theme.NeonRedContainer
import com.gameboost.optimizer.theme.ObsidianBg
import com.gameboost.optimizer.theme.TextGray
import com.gameboost.optimizer.theme.TextMuted
import com.gameboost.optimizer.theme.TextWhite
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.MetricChip
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    shizukuStatus: ShizukuStatus,
    isOptimized: Boolean,
    activeGameProfile: GameProfile?,
    lastResult: OptimizationResult?,
    lastRestorationResult: com.gameboost.optimizer.models.RestorationResult? = null,
    games: List<GameProfile>,
    hardwareStats: HardwareStats,
    userPreferences: AppUserPreferences,
    onQuickBoost: () -> Unit,
    onRestoreDefault: () -> Unit,
    onGameClick: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onFixShizuku: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ObsidianBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // App Bar / Branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GAMEBOOST",
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Gaming Performance Optimizer",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row {
                        IconButton(onClick = onOpenDiagnostics) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Diagnostics",
                                tint = TextGray
                            )
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Warning Banner if Shizuku was previously authorized but is now lost
            if (!shizukuStatus.isAuthorized && userPreferences.wasShizukuEverAuthorized) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = CyberAmberContainer,
                        border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = CyberAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Shizuku authorization is no longer available.",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Button(
                                onClick = onFixShizuku,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberAmber, contentColor = ObsidianBg)
                            ) {
                                Text("FIX", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Hero Live Status Card
            item {
                LiveStatusHeroCard(
                    isOptimized = isOptimized,
                    activeGame = activeGameProfile,
                    currentRate = hardwareStats.currentRefreshRate,
                    targetRate = userPreferences.targetRefreshRate,
                    shizukuStatus = shizukuStatus,
                    profileType = userPreferences.selectedProfileType.name,
                    onQuickBoost = onQuickBoost,
                    onRestoreDefault = onRestoreDefault
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Verification Result Card (Honest outcome display)
            if (lastResult != null && isOptimized) {
                item {
                    VerificationResultCard(lastResult = lastResult)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Restoration Result Card (Section 16 requirement)
            if (lastRestorationResult != null && !isOptimized) {
                item {
                    RestorationResultCard(result = lastRestorationResult)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


            // Games Section
            item {
                SectionHeader(
                    title = "Supported Games",
                    subtitle = "PUBG Mobile • BGMI • Korean PUBG"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(games) { game ->
                GameCardItem(
                    game = game,
                    isOptimized = isOptimized && activeGameProfile?.id == game.id,
                    onClick = { onGameClick(game.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // System Telemetry Section
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(
                    title = "System Telemetry",
                    subtitle = "Live hardware metrics (low battery overhead)"
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "Display",
                        value = "${hardwareStats.currentRefreshRate.toInt()}Hz",
                        icon = Icons.Default.Speed,
                        accentColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Battery",
                        value = "${hardwareStats.batteryPercentage}%",
                        icon = Icons.Default.BatteryChargingFull,
                        accentColor = if (hardwareStats.batteryPercentage > 20) NeonGreen else NeonRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "Temperature",
                        value = hardwareStats.formattedTemp,
                        icon = Icons.Default.DeviceThermostat,
                        accentColor = if (hardwareStats.batteryTemperatureCelsius < 42f) CyberAmber else NeonRed,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "RAM Usage",
                        value = hardwareStats.formattedRam,
                        icon = Icons.Default.Memory,
                        accentColor = ElectricPurple,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LiveStatusHeroCard(
    isOptimized: Boolean,
    activeGame: GameProfile?,
    currentRate: Float,
    targetRate: Float,
    shizukuStatus: ShizukuStatus,
    profileType: String,
    onQuickBoost: () -> Unit,
    onRestoreDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.5.dp, if (isOptimized) NeonGreen.copy(alpha = 0.6f) else CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isOptimized) NeonGreen else CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOptimized) "OPTIMIZATION ACTIVE" else "READY TO BOOST",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                StatusBadge(
                    text = if (shizukuStatus.isAuthorized) "SHIZUKU READY" else "NOT AUTHORIZED",
                    state = if (shizukuStatus.isAuthorized) BadgeState.SUCCESS else BadgeState.WARNING
                )
            }

            if (isOptimized && activeGame != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Active session: ${activeGame.displayName}",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeroMetricItem(label = "DISPLAY", value = "${currentRate.toInt()}Hz", color = NeonGreen)
                HeroMetricItem(label = "TARGET", value = "${targetRate.toInt()}Hz", color = CyberCyan)
                HeroMetricItem(label = "PROFILE", value = profileType, color = ElectricPurple)
                HeroMetricItem(label = "BOOST", value = if (isOptimized) "ACTIVE" else "STANDBY", color = if (isOptimized) NeonGreen else TextMuted)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onQuickBoost,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOptimized) NeonGreen else CyberCyan,
                        contentColor = ObsidianBg
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOptimized) "RE-APPLY BOOST" else "ENABLE 120Hz BOOST",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isOptimized) {
                    OutlinedButton(
                        onClick = onRestoreDefault,
                        modifier = Modifier
                            .weight(0.7f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RESTORE",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroMetricItem(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun VerificationResultCard(lastResult: OptimizationResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (lastResult.isDisplayRateVerified) NeonGreenContainer else NeonRedContainer,
        border = BorderStroke(1.dp, if (lastResult.isDisplayRateVerified) NeonGreen.copy(alpha = 0.5f) else NeonRed.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPTIMIZATION VERIFICATION",
                    color = if (lastResult.isDisplayRateVerified) NeonGreen else NeonRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (lastResult.isDisplayRateVerified) "VERIFIED" else "DEVICE REFUSED",
                    color = if (lastResult.isDisplayRateVerified) NeonGreen else NeonRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = lastResult.verificationSummary,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (lastResult.technicalExplanation != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lastResult.technicalExplanation,
                    color = TextGray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            if (lastResult.appliedSettings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Applied: " + lastResult.appliedSettings.joinToString(", "),
                    color = CyberCyan,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun GameCardItem(
    game: GameProfile,
    isOptimized: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.dp, if (isOptimized) NeonGreen.copy(alpha = 0.5f) else CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (game.isInstalled) CyberCyanContainer else CardNavyElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = if (game.isInstalled) CyberCyan else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = game.displayName,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${game.region} • ${if (game.isInstalled) "v${game.appVersion ?: "Installed"}" else "Not installed"}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                    text = if (isOptimized) "ACTIVE" else if (game.isInstalled) "INSTALLED" else "NOT FOUND",
                    state = if (isOptimized) BadgeState.SUCCESS else if (game.isInstalled) BadgeState.INFO else BadgeState.NEUTRAL
                )
            }
        }
    }
}

@Composable
private fun RestorationResultCard(result: com.gameboost.optimizer.models.RestorationResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (result.isCompleteSuccess) NeonGreenContainer else CyberAmberContainer,
        border = BorderStroke(1.dp, if (result.isCompleteSuccess) NeonGreen.copy(alpha = 0.5f) else CyberAmber.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (result.isCompleteSuccess) "RESTORE COMPLETE" else "RESTORE PARTIALLY COMPLETE",
                color = if (result.isCompleteSuccess) NeonGreen else CyberAmber,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            result.restoredItems.forEach { item ->
                Text(text = "✓ $item", color = TextWhite, fontSize = 12.sp)
            }
            result.failedItems.forEach { item ->
                Text(text = "✕ $item", color = NeonRed, fontSize = 12.sp)
            }
            if (result.failureReason != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Reason: ${result.failureReason}", color = TextGray, fontSize = 11.sp)
            }
        }
    }
}

