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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.ShizukuManager
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
        color = ObsidianBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

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
                        tint = TextWhite
                    )
                }

                Text(
                    text = "SHIZUKU SETUP",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                IconButton(onClick = onRefreshStatus) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Explanation
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Why GameBoost uses Shizuku",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Android protects display refresh-rate controls from ordinary unprivileged apps. Shizuku enables user-authorized ADB permissions without root or bootloader unlocking, so GameBoost can lock the 120Hz display safely.",
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 1: Install
            StepCard(
                stepNumber = 1,
                title = "Install Shizuku",
                description = if (status.isInstalled) "Shizuku application is installed on your device." else "Shizuku is required to apply elevated display settings without root.",
                isCompleted = status.isInstalled,
                actionButton = if (!status.isInstalled) {
                    {
                        Button(
                            onClick = { openPlayStoreOrBrowser(context, ShizukuManager.SHIZUKU_PACKAGE) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GET SHIZUKU", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2: Running
            StepCard(
                stepNumber = 2,
                title = "Start Shizuku Service",
                description = if (status.isRunning) "Shizuku service is running." else "Launch the Shizuku app and start it using Wireless Debugging (Android 11+) or computer ADB.",
                isCompleted = status.isRunning,
                actionButton = if (status.isInstalled && !status.isRunning) {
                    {
                        OutlinedButton(
                            onClick = { launchShizukuApp(context) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CyberCyan)
                        ) {
                            Text("OPEN SHIZUKU", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3: Grant Permission
            StepCard(
                stepNumber = 3,
                title = "Grant GameBoost Permission",
                description = if (status.isAuthorized) "GameBoost is authorized! Shizuku remembers this state for future launches." else "Allow GameBoost in Shizuku's authorization prompt.",
                isCompleted = status.isAuthorized,
                actionButton = if (status.isRunning && !status.isAuthorized) {
                    {
                        Button(
                            onClick = onRequestPermission,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                        ) {
                            Text("GRANT PERMISSION", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Bottom Action
            if (status.isAuthorized) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = ObsidianBg
                    )
                ) {
                    Text(
                        text = "CONTINUE TO DASHBOARD",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "CONTINUE IN READ-ONLY MODE",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StepCard(
    stepNumber: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    actionButton: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardNavy,
        border = BorderStroke(1.dp, if (isCompleted) NeonGreen.copy(alpha = 0.4f) else CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) NeonGreen else CardNavyElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ObsidianBg,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "$stepNumber",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isCompleted) {
                    Text(
                        text = "COMPLETED",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 40.dp)
            )

            if (actionButton != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp)
                ) {
                    actionButton()
                }
            }
        }
    }
}

private fun launchShizukuApp(context: Context) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(ShizukuManager.SHIZUKU_PACKAGE)
        if (intent != null) {
            context.startActivity(intent)
        }
    } catch (_: Throwable) {}
}

private fun openPlayStoreOrBrowser(context: Context, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Throwable) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }
}
