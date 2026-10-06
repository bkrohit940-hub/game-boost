package com.gameboost.optimizer.ui.firstlaunch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
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
import com.gameboost.optimizer.ui.components.GameBoostLogo
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
        color = DarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Brand Header with clean emblem
            GameBoostLogo(sizeDp = 64.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GAME BOOST",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Performance Center",
                color = AccentPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Professional Android gaming launcher and performance optimizer for competitive battle royale titles.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!hasStartedScan) {
                GlassCard(backgroundColor = SurfaceElevated) {
                    Text(
                        text = "DEVICE INITIALIZATION",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Game Boost will inspect your display panel refresh rates, SoC hardware, and Shizuku authorization state.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { hasStartedScan = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SCAN DEVICE CAPABILITIES", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else {
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        GlassCard(backgroundColor = SurfaceElevated) {
                            Text(
                                text = "HARDWARE SCAN RESULTS",
                                color = TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Device", color = TextSecondary, fontSize = 12.sp)
                                Text("${capabilities.brand} ${capabilities.model}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Display Panel", color = TextSecondary, fontSize = 12.sp)
                                Text("${capabilities.displayState.maxRefreshRate.toInt()}Hz Max", color = AccentPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("System Memory", color = TextSecondary, fontSize = 12.sp)
                                Text(capabilities.formattedRam, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Shizuku Status", color = TextSecondary, fontSize = 12.sp)
                                StatusBadge(
                                    text = if (shizukuStatus.isReady) "READY" else "NOT READY",
                                    state = if (shizukuStatus.isReady) BadgeState.SUCCESS else BadgeState.WARNING
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (!shizukuStatus.isReady) {
                            Button(
                                onClick = onContinueToShizuku,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("CONFIGURE SHIZUKU (RECOMMENDED)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onContinueToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("SKIP TO PERFORMANCE CENTER", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = onContinueToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusReady, contentColor = DarkBg),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("ENTER GAME BOOST", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
