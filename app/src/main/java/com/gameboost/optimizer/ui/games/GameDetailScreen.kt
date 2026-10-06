package com.gameboost.optimizer.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DisplayState
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationProfileType
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.theme.CardBorder
import com.gameboost.optimizer.theme.CardNavy
import com.gameboost.optimizer.theme.CardNavyElevated
import com.gameboost.optimizer.theme.CyberCyan
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
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun GameDetailScreen(
    game: GameProfile,
    displayState: DisplayState,
    isOptimized: Boolean,
    lastResult: OptimizationResult?,
    onApply: (OptimizationProfile) -> Unit,
    onRestore: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRate by remember { mutableFloatStateOf(120f) }
    var selectedProfileType by remember { mutableStateOf(OptimizationProfileType.PERFORMANCE) }
    var autoBoost by remember { mutableStateOf(true) }
    var restoreOnExit by remember { mutableStateOf(true) }

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
                        text = game.displayName,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${game.region} • ${game.activePackageName}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Status Card
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STATUS",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isOptimized) "OPTIMIZED (ACTIVE)" else "READY TO APPLY",
                            color = if (isOptimized) NeonGreen else TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    StatusBadge(
                        text = if (game.isInstalled) "INSTALLED" else "NOT INSTALLED",
                        state = if (game.isInstalled) BadgeState.SUCCESS else BadgeState.WARNING
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Refresh Rate Selection
            SectionHeader(title = "Display Refresh Rate", subtitle = "Choose panel refresh target")

            GlassCard {
                val targets = com.gameboost.optimizer.models.RefreshRateTarget.getAvailableForDisplay(displayState)

                targets.forEach { target ->
                    val isSelected = selectedRate == target.targetRate

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRate = target.targetRate }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedRate = target.targetRate },
                            colors = RadioButtonDefaults.colors(selectedColor = CyberCyan, unselectedColor = TextMuted)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (target == com.gameboost.optimizer.models.RefreshRateTarget.HIGHEST_AVAILABLE) {
                                "Highest Available (${displayState.maximumRefreshRate.toInt()}Hz)"
                            } else {
                                target.displayName
                            },
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Performance Profile Selection
            SectionHeader(title = "Performance Profile", subtitle = "System level adjustments")

            GlassCard {
                OptimizationProfileType.values().forEach { profileType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedProfileType = profileType }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedProfileType == profileType,
                            onClick = { selectedProfileType = profileType },
                            colors = RadioButtonDefaults.colors(selectedColor = CyberCyan, unselectedColor = TextMuted)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = profileType.name,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = if (selectedProfileType == profileType) FontWeight.Bold else FontWeight.Normal
                            )
                            val desc = when (profileType) {
                                OptimizationProfileType.BALANCED -> "Highest refresh rate, standard animations"
                                OptimizationProfileType.PERFORMANCE -> "120Hz lock, reduced animations, GameMode API"
                                OptimizationProfileType.EXTREME -> "Max refresh rate, zero animation latency"
                            }
                            Text(text = desc, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Automation Toggles
            SectionHeader(title = "Automation")

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto Boost on Launch", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Apply target rate when game opens", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = autoBoost,
                        onCheckedChange = { autoBoost = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBg, checkedTrackColor = CyberCyan)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Restore after Exit", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Restore default display when game closes", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = restoreOnExit,
                        onCheckedChange = { restoreOnExit = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBg, checkedTrackColor = CyberCyan)
                    )
                }
            }

            // Verification Result Card
            if (lastResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (lastResult.isDisplayRateVerified) NeonGreenContainer else NeonRedContainer,
                    border = BorderStroke(1.dp, if (lastResult.isDisplayRateVerified) NeonGreen.copy(alpha = 0.5f) else NeonRed.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "VERIFICATION STATUS",
                            color = if (lastResult.isDisplayRateVerified) NeonGreen else NeonRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = lastResult.verificationSummary, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        if (lastResult.technicalExplanation != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = lastResult.technicalExplanation, color = TextGray, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {
                    onApply(
                        OptimizationProfile(
                            type = selectedProfileType,
                            targetRefreshRate = selectedRate,
                            autoBoostOnLaunch = autoBoost,
                            restoreOnExit = restoreOnExit
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "APPLY NOW", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            if (isOptimized) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onRestore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, tint = TextGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "RESTORE DEFAULT", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
