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
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreAngularShape
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameBoostLogo
import com.gameboost.optimizer.ui.components.RedCoreCard
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
        color = RedCoreBgPrimary
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
            GameBoostLogo(sizeDp = 68.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "GAME",
                    color = RedCoreTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "BOOST",
                    color = PrimaryGamingRed,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RED CORE // INITIAL SETUP",
                color = BrightActiveRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Native Android gaming command center and display refresh optimizer for competitive battle royale titles.",
                color = RedCoreTextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!hasStartedScan) {
                RedCoreCard(
                    backgroundColor = RedCoreSurfaceElevated,
                    borderColor = RedCoreBorder
                ) {
                    Text(
                        text = "DEVICE INITIALIZATION",
                        color = RedCoreTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "GameBoost will inspect your display panel refresh rates, SoC hardware, and Shizuku authorization state.",
                        color = RedCoreTextPrimary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { hasStartedScan = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed, contentColor = Color.White),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp)
                    ) {
                        Text("SCAN DEVICE CAPABILITIES", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else {
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        RedCoreCard(
                            backgroundColor = RedCoreSurfaceElevated,
                            borderColor = RedCoreBorderActive
                        ) {
                            Text(
                                text = "HARDWARE SCAN RESULTS",
                                color = BrightActiveRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Device", color = RedCoreTextSecondary, fontSize = 12.sp)
                                Text("${capabilities.brand} ${capabilities.model}", color = RedCoreTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Display Panel", color = RedCoreTextSecondary, fontSize = 12.sp)
                                Text("${capabilities.displayState.maxRefreshRate.toInt()}Hz Max", color = BrightActiveRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("System Memory", color = RedCoreTextSecondary, fontSize = 12.sp)
                                Text(capabilities.formattedRam, color = RedCoreTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Shizuku Status", color = RedCoreTextSecondary, fontSize = 12.sp)
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
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed, contentColor = Color.White),
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
                            ) {
                                Text("CONFIGURE SHIZUKU (RECOMMENDED)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onContinueToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedCoreTextSecondary),
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                border = BorderStroke(1.dp, RedCoreBorder)
                            ) {
                                Text("SKIP TO COMMAND CENTER", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = onContinueToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RedCoreSuccess, contentColor = Color.Black),
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
                            ) {
                                Text("ENTER GAME BOOST COMMAND CENTER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
