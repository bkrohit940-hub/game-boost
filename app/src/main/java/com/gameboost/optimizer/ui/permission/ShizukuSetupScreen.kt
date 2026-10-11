package com.gameboost.optimizer.ui.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.ShizukuState
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.ShizukuManager
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.DeepRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreAngularShape
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSuccessContainer
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.theme.RedCoreWarningContainer
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.RedCoreCard
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
        color = RedCoreBgPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Navigation Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RedCoreTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SHIZUKU PRIVILEGED SETUP",
                        color = RedCoreTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(onClick = onRefreshStatus) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = BrightActiveRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current State Card
            RedCoreCard(
                backgroundColor = RedCoreSurfaceElevated,
                borderColor = if (status.isReady) RedCoreSuccess else RedCoreBorderActive
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = status.title,
                            color = if (status.isReady) RedCoreSuccess else BrightActiveRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = status.summaryText,
                            color = RedCoreTextSecondary,
                            fontSize = 11.5.sp
                        )
                    }

                    StatusBadge(
                        text = if (status.isReady) "READY" else "ACTION REQUIRED",
                        state = if (status.isReady) BadgeState.SUCCESS else BadgeState.WARNING
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = status.actionPrompt,
                    color = RedCoreTextPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-Step Instructions based on active state
            RedCoreCard(
                backgroundColor = RedCoreSurfaceElevated,
                borderColor = RedCoreBorder
            ) {
                Text(
                    text = "SETUP INSTRUCTIONS",
                    color = RedCoreTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

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
                    title = "Authorize GameBoost",
                    description = "Grant GameBoost permission to adjust display modes and legitimate performance settings.",
                    isCompleted = status.isAuthorized
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            when (status.state) {
                is ShizukuState.ShellVerified -> {
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedCoreSuccess, contentColor = Color.Black),
                        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CONTINUE TO COMMAND CENTER", fontWeight = FontWeight.Bold)
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
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed, contentColor = Color.White),
                        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
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
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed, contentColor = Color.White),
                        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
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
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGamingRed, contentColor = Color.White),
                        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedCoreTextSecondary),
                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                border = BorderStroke(1.dp, RedCoreBorder)
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
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(22.dp),
            shape = CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp),
            color = if (isCompleted) RedCoreSuccessContainer else RedCoreSurfaceCard,
            border = BorderStroke(1.dp, if (isCompleted) RedCoreSuccess else RedCoreBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = RedCoreSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                } else {
                    Text(
                        text = stepNumber,
                        color = RedCoreTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                color = if (isCompleted) RedCoreTextPrimary else RedCoreTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = RedCoreTextSecondary,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
            )
        }
    }
}
