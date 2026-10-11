package com.gameboost.optimizer.ui.games

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
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DisplayState
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreAngularShape
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.PerformanceModeSelector
import com.gameboost.optimizer.ui.components.PrimaryBoostButton
import com.gameboost.optimizer.ui.components.RedCoreCard
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
        color = RedCoreBgPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = RedCoreTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = game.displayName.uppercase(),
                        color = RedCoreTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "HARDWARE PROFILE CONFIGURATION",
                        color = RedCoreTextSecondary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Profile Card with Red Core Angular Shape
            RedCoreCard(
                backgroundColor = RedCoreSurfaceElevated,
                borderColor = if (isOptimized) RedCoreSuccess else RedCoreBorderActive
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GameIconView(
                        packageName = game.activePackageName,
                        isInstalled = game.isInstalled,
                        sizeDp = 56.dp,
                        isActive = isOptimized
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
                                color = RedCoreTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
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
                            color = RedCoreTextSecondary,
                            fontSize = 11.sp
                        )
                        if (game.appVersion != null) {
                            Text(
                                text = "Installed version: ${game.appVersion}",
                                color = RedCoreTextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector
            SectionHeader(
                title = "PERFORMANCE MODE",
                subtitle = "Select desired optimization intensity"
            )
            PerformanceModeSelector(
                selectedMode = selectedMode,
                onModeSelected = { selectedMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Supported Capabilities Matrix
            SectionHeader(
                title = "VERIFIED CAPABILITIES",
                subtitle = "Hardware features mapped to this title"
            )
            RedCoreCard(backgroundColor = RedCoreSurfaceElevated) {
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
                            tint = RedCoreSuccess,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            color = RedCoreTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            if (game.isInstalled) {
                PrimaryBoostButton(
                    onClick = {
                        if (selectedMode == PerformanceMode.THERMAL_OVERRIDE) {
                            showThermalDialog = true
                        } else {
                            onApply(OptimizationProfile(mode = selectedMode))
                            onLaunch?.invoke()
                        }
                    },
                    text = "BOOST & PLAY",
                    subtext = "APPLY PROFILE & LAUNCH GAME",
                    modifier = Modifier.fillMaxWidth()
                )

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
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        border = BorderStroke(1.dp, RedCoreBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedCoreTextPrimary)
                    ) {
                        Text("BOOST ONLY", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    if (onLaunch != null) {
                        OutlinedButton(
                            onClick = onLaunch,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                            border = BorderStroke(1.dp, RedCoreBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedCoreTextSecondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PLAY ONLY", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (isOptimized) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onRestore,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        border = BorderStroke(1.dp, RedCoreWarning.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedCoreWarning)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESTORE DEFAULT SETTINGS", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                    color = RedCoreSurfaceElevated,
                    border = BorderStroke(1.dp, RedCoreBorder)
                ) {
                    Text(
                        text = "This game package is not installed on your device. Install it from the official app store to enable launch and boost.",
                        color = RedCoreTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
