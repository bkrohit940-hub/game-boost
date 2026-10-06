package com.gameboost.optimizer.ui.games

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DisplayState
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.PerformanceModeSelector
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge
import com.gameboost.optimizer.ui.components.ThermalRiskDialog

@Composable
fun GameDetailScreen(
    game: GameProfile,
    displayState: DisplayState,
    isOptimized: Boolean,
    lastResult: OptimizationResult?,
    onApply: (OptimizationProfile) -> Unit,
    onRestore: () -> Unit,
    onLaunch: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(PerformanceMode.PERFORMANCE) }
    var showThermalDialog by remember { mutableStateOf(false) }

    if (showThermalDialog) {
        ThermalRiskDialog(
            onConfirm = {
                showThermalDialog = false
                onApply(
                    OptimizationProfile(
                        mode = PerformanceMode.THERMAL_OVERRIDE,
                        thermalOverrideConfirmed = true
                    )
                )
            },
            onDismiss = { showThermalDialog = false }
        )
    }

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
                Text(
                    text = game.displayName.uppercase(),
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Profile Card
            GlassCard(backgroundColor = SurfaceElevated) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GameIconView(
                        packageName = game.activePackageName,
                        isInstalled = game.isInstalled,
                        sizeDp = 56.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = game.displayName,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val (badgeText, badgeState) = when {
                                !game.isInstalled -> Pair("NOT INSTALLED", BadgeState.NEUTRAL)
                                isOptimized -> Pair("BOOST ACTIVE", BadgeState.SUCCESS)
                                else -> Pair("READY", BadgeState.SUCCESS)
                            }
                            StatusBadge(text = badgeText, state = badgeState)
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${game.region} • ${game.activePackageName}",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                        if (game.appVersion != null) {
                            Text(
                                text = "Installed version: ${game.appVersion}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector
            SectionHeader(
                title = "Performance Mode",
                subtitle = "Select desired optimization intensity"
            )
            PerformanceModeSelector(
                selectedMode = selectedMode,
                onModeSelected = { selectedMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Supported Capabilities Matrix
            SectionHeader(title = "Verified Game Capabilities")
            GlassCard(backgroundColor = SurfaceElevated) {
                game.supportedFeatures.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = StatusReady,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            if (game.isInstalled) {
                Button(
                    onClick = {
                        if (selectedMode == PerformanceMode.THERMAL_OVERRIDE) {
                            showThermalDialog = true
                        } else {
                            onApply(OptimizationProfile(mode = selectedMode))
                            onLaunch?.invoke()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BOOST & PLAY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (selectedMode == PerformanceMode.THERMAL_OVERRIDE) {
                                showThermalDialog = true
                            } else {
                                onApply(OptimizationProfile(mode = selectedMode))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("BOOST ONLY", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (onLaunch != null) {
                        OutlinedButton(
                            onClick = onLaunch,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PLAY ONLY", fontSize = 12.sp)
                        }
                    }
                }

                if (isOptimized) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onRestore,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESTORE DEFAULT SETTINGS", fontSize = 12.sp)
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(
                        text = "This game package is not installed on your device. Install it from the official app store to enable launch and boost.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
