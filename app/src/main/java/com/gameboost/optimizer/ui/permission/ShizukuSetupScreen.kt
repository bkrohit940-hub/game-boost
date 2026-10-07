package com.gameboost.optimizer.ui.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.ShizukuState
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.ShizukuManager
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusDanger
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameBoostLogo
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun ShizukuSetupScreen(
    status: ShizukuStatus,
    onRequestPermission: () -> Unit,
    onRefreshStatus: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "SHIZUKU SETUP",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                IconButton(onClick = onRefreshStatus) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = AccentPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current State Card
            GlassCard(
                backgroundColor = SurfaceElevated,
                border = BorderStroke(
                    1.dp,
                    if (status.isReady) StatusReady else StatusWarning.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = status.title,
                            color = if (status.isReady) StatusReady else StatusWarning,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = status.summaryText,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    StatusBadge(
                        text = if (status.isReady) "READY" else "ACTION REQUIRED",
                        state = if (status.isReady) BadgeState.SUCCESS else BadgeState.WARNING
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = status.actionPrompt,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step-by-Step Instructions based on active state
            GlassCard(backgroundColor = SurfaceElevated) {
                Text(
                    text = "SETUP GUIDE",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                SetupStepItem(
                    stepNumber = "1",
                    title = "Install Shizuku",
                    description = "Install the official Shizuku application from GitHub or Google Play Store.",
                    isCompleted = status.isInstalled
                )

                SetupStepItem(
                    stepNumber = "2",
                    title = "Start Shizuku Service",
                    description = "Open Shizuku and start the service via Wireless Debugging (Android 11+) or ADB over USB.",
                    isCompleted = status.isRunning
                )

                SetupStepItem(
                    stepNumber = "3",
                    title = "Authorize Game Boost",
                    description = "Grant Game Boost permission to adjust display modes and legitimate performance settings.",
                    isCompleted = status.isAuthorized
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            when (status.state) {
                is ShizukuState.ShellVerified -> {
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusReady, contentColor = DarkBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CONTINUE TO GAME BOOST", fontWeight = FontWeight.Bold)
                    }
                }

                is ShizukuState.ServiceRunningPermissionMissing,
                is ShizukuState.PermissionGranted,
                is ShizukuState.BinderConnected,
                is ShizukuState.ShellFailed -> {
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (status.wasPreviouslyAuthorized) "RE-AUTHORIZE PERMISSION" else "AUTHORIZE SHIZUKU",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                is ShizukuState.InstalledServiceStopped,
                is ShizukuState.BinderDead -> {
                    Button(
                        onClick = {
                            val intent = context.packageManager.getLaunchIntentForPackage(ShizukuManager.SHIZUKU_PACKAGE)
                            if (intent != null) {
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OPEN SHIZUKU APP", fontWeight = FontWeight.Bold)
                    }
                }

                is ShizukuState.NotInstalled -> {
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/RikkaApps/Shizuku/releases")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary, contentColor = DarkBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DOWNLOAD SHIZUKU", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("CONTINUE ANYWAY (LIMITED MODE)", fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SetupStepItem(
    stepNumber: String,
    title: String,
    description: String,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = RoundedCornerShape(6.dp),
            color = if (isCompleted) StatusReady.copy(alpha = 0.15f) else SurfaceCard,
            border = BorderStroke(1.dp, if (isCompleted) StatusReady else BorderSubtle)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = StatusReady,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = stepNumber,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = if (isCompleted) TextPrimary else TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextTertiary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
