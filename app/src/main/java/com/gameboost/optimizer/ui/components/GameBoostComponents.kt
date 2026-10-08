package com.gameboost.optimizer.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation3.runtime.NavKey
import com.gameboost.optimizer.BoostKey
import com.gameboost.optimizer.DiagnosticsKey
import com.gameboost.optimizer.GamesKey
import com.gameboost.optimizer.HomeKey
import com.gameboost.optimizer.R
import com.gameboost.optimizer.SettingsKey
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.AccentPrimaryContainer
import com.gameboost.optimizer.theme.AccentSecondary
import com.gameboost.optimizer.theme.BorderActive
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusDanger
import com.gameboost.optimizer.theme.StatusDangerContainer
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusReadyContainer
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.StatusWarningContainer
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary

// ==========================================
// 1. GAME BOOST BRAND LOGO
// ==========================================

@Composable
fun GameBoostLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 34.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_game_boost_tactical),
        contentDescription = "Game Boost Tactical Logo",
        modifier = modifier.size(sizeDp)
    )
}

// ==========================================
// 2. REAL GAME APPLICATION ICON
// ==========================================

@Composable
fun GameIconView(
    packageName: String?,
    isInstalled: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 52.dp
) {
    val context = LocalContext.current

    val iconDrawable = produceState<Drawable?>(initialValue = null, key1 = packageName, key2 = isInstalled) {
        if (isInstalled && !packageName.isNullOrEmpty()) {
            value = try {
                context.packageManager.getApplicationIcon(packageName)
            } catch (_: Throwable) {
                null
            }
        } else {
            value = null
        }
    }.value

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (iconDrawable != null) {
            val bitmap = rememberBitmap(iconDrawable, sizeDp)
            if (bitmap != null) {
                Image(
                    painter = BitmapPainter(bitmap),
                    contentDescription = null,
                    modifier = Modifier
                        .size(sizeDp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(sizeDp * 0.55f)
                )
            }
        } else {
            // Clean minimal fallback when game is not installed
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = if (isInstalled) AccentPrimary else TextTertiary,
                modifier = Modifier.size(sizeDp * 0.55f)
            )
        }
    }
}

@Composable
private fun rememberBitmap(drawable: Drawable, sizeDp: Dp): androidx.compose.ui.graphics.ImageBitmap? {
    return androidx.compose.runtime.remember(drawable) {
        try {
            val px = 128
            drawable.toBitmap(px, px).asImageBitmap()
        } catch (_: Throwable) {
            null
        }
    }
}

// ==========================================
// 3. MINIMAL SURFACES & CONTAINERS
// ==========================================

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = BorderStroke(1.dp, BorderSubtle),
    backgroundColor: Color = SurfaceCard,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// ==========================================
// 4. STATUS TAGS & BADGES
// ==========================================

enum class BadgeState {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL
}

@Composable
fun StatusBadge(
    text: String,
    state: BadgeState,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (state) {
        BadgeState.SUCCESS -> Pair(StatusReadyContainer, StatusReady)
        BadgeState.WARNING -> Pair(StatusWarningContainer, StatusWarning)
        BadgeState.ERROR -> Pair(StatusDangerContainer, StatusDanger)
        BadgeState.INFO -> Pair(AccentPrimaryContainer, AccentPrimary)
        BadgeState.NEUTRAL -> Pair(SurfaceElevated, TextSecondary)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(fg)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text.uppercase(),
                color = fg,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

// ==========================================
// 5. METRIC BLOCKS (No fake numbers)
// ==========================================

@Composable
fun MetricChip(
    label: String,
    value: String,
    icon: ImageVector? = null,
    accentColor: Color = AccentPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column {
                Text(
                    text = label.uppercase(),
                    color = TextTertiary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 6. SECTION HEADERS
// ==========================================

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title.uppercase(),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionText,
                    color = AccentPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ==========================================
// 7. BOTTOM NAVIGATION BAR (5 SECTIONS)
// ==========================================

data class NavItem(
    val key: NavKey,
    val title: String,
    val icon: ImageVector
)

val MainNavItems = listOf(
    NavItem(HomeKey, "HOME", Icons.Default.Home),
    NavItem(GamesKey, "GAMES", Icons.Default.SportsEsports),
    NavItem(BoostKey, "BOOST", Icons.Default.Bolt),
    NavItem(DiagnosticsKey, "DIAG", Icons.Default.Speed),
    NavItem(SettingsKey, "SETTINGS", Icons.Default.Tune)
)

@Composable
fun GameBoostNavBar(
    currentKey: NavKey,
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkBg,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainNavItems.forEach { item ->
                val isSelected = currentKey::class == item.key::class
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                        .clickable { onNavigate(item.key) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) SurfaceElevated else androidx.compose.ui.graphics.Color.Transparent,
                    border = if (isSelected) BorderStroke(1.dp, BorderActive) else null
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) AccentPrimary else TextTertiary,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.title,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            color = if (isSelected) AccentPrimary else TextTertiary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. PERFORMANCE MODE SELECTOR
// ==========================================

@Composable
fun PerformanceModeSelector(
    selectedMode: PerformanceMode,
    onModeSelected: (PerformanceMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(PerformanceMode.SAFE, "BALANCED", "Balanced performance prioritizing thermal comfort and battery"),
                Triple(PerformanceMode.PERFORMANCE, "PERFORMANCE", "Recommended profile. Unlocks high refresh rate and optimizes gaming responsiveness"),
                Triple(PerformanceMode.AGGRESSIVE, "EXTREME", "Aggressive profile. Maximum sustained clocks and background process throttling")
            ).forEach { (mode, label, _) ->
                val isSelected = selectedMode == mode
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeSelected(mode) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) AccentPrimaryContainer else SurfaceElevated,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) BorderActive else BorderSubtle
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) AccentPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thermal Override Button (Distinct, experimental & warning)
        val isThermalSelected = selectedMode == PerformanceMode.THERMAL_OVERRIDE
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onModeSelected(PerformanceMode.THERMAL_OVERRIDE) },
            shape = RoundedCornerShape(10.dp),
            color = if (isThermalSelected) StatusDangerContainer else SurfaceElevated,
            border = BorderStroke(
                1.dp,
                if (isThermalSelected) StatusDanger else BorderSubtle
            )
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isThermalSelected) StatusDanger else StatusWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THERMAL OVERRIDE",
                        color = if (isThermalSelected) StatusDanger else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                StatusBadge(
                    text = "DANGEROUS",
                    state = BadgeState.ERROR
                )
            }
        }
    }
}

// ==========================================
// 9. THERMAL RISK CONFIRMATION DIALOG
// ==========================================

@Composable
fun ThermalRiskDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        titleContentColor = StatusDanger,
        textContentColor = TextPrimary,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = StatusDanger,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "THERMAL RISK",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "This mode signals maximum sustained gaming performance to Android power subsystem.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This mode may significantly increase:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "• Device temperature\n• Battery wear\n• System instability\n• Power consumption",
                    fontSize = 12.sp,
                    color = StatusWarning,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "OEM and Android kernel thermal protection will NOT be bypassed and may still override this mode.",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
            ) {
                Text(text = "I Understand the Risks", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextSecondary)
            }
        }
    )
}

// ==========================================
// 10. REUSABLE SYSTEM & GAMING COMPONENTS
// ==========================================

@Composable
fun StatusPill(
    text: String,
    state: BadgeState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bg, fg) = when (state) {
        BadgeState.SUCCESS -> Pair(StatusReadyContainer, StatusReady)
        BadgeState.WARNING -> Pair(StatusWarningContainer, StatusWarning)
        BadgeState.ERROR -> Pair(StatusDangerContainer, StatusDanger)
        BadgeState.INFO -> Pair(AccentPrimaryContainer, AccentPrimary)
        BadgeState.NEUTRAL -> Pair(SurfaceElevated, TextSecondary)
    }

    Surface(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(fg)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text.uppercase(),
                color = fg,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ConnectionStatus(
    backendType: com.gameboost.optimizer.system.BackendType,
    isReady: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (badgeText, badgeState) = when {
        backendType == com.gameboost.optimizer.system.BackendType.SHIZUKU && isReady -> Pair("SHIZUKU", BadgeState.SUCCESS)
        backendType == com.gameboost.optimizer.system.BackendType.WIRELESS_ADB && isReady -> Pair("WIRELESS ADB", BadgeState.INFO)
        backendType == com.gameboost.optimizer.system.BackendType.SHIZUKU -> Pair("SHIZUKU NOT READY", BadgeState.WARNING)
        backendType == com.gameboost.optimizer.system.BackendType.WIRELESS_ADB -> Pair("ADB CONNECTING", BadgeState.WARNING)
        else -> Pair("UNPRIVILEGED", BadgeState.NEUTRAL)
    }

    StatusPill(
        text = badgeText,
        state = badgeState,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun TelemetryItem(
    label: String,
    value: String,
    icon: ImageVector? = null,
    accentColor: Color = AccentPrimary,
    modifier: Modifier = Modifier
) {
    MetricChip(
        label = label,
        value = value,
        icon = icon,
        accentColor = accentColor,
        modifier = modifier
    )
}

@Composable
fun RefreshRateSelector(
    currentRate: Float,
    selectedTarget: Float,
    supportedRates: List<Float>,
    onSelectRate: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = remember(supportedRates) {
        val rates = mutableListOf(0f)
        if (supportedRates.any { it in 59f..61f }) rates.add(60f)
        if (supportedRates.any { it in 89f..91f }) rates.add(90f)
        if (supportedRates.any { it in 119f..121f }) rates.add(120f)
        if (supportedRates.any { it in 143f..145f }) rates.add(144f)
        rates.distinct()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { rate ->
            val isSelected = (selectedTarget == rate) || (rate == 0f && selectedTarget <= 0f)
            val label = if (rate <= 0f) "AUTO MAX" else "${rate.toInt()}Hz"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectRate(rate) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) AccentPrimaryContainer else SurfaceElevated,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) BorderActive else BorderSubtle
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) AccentPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSelector(
    selectedMode: PerformanceMode,
    onModeSelected: (PerformanceMode) -> Unit,
    modifier: Modifier = Modifier
) {
    PerformanceModeSelector(
        selectedMode = selectedMode,
        onModeSelected = onModeSelected,
        modifier = modifier
    )
}

@Composable
fun OptimizationRow(
    title: String,
    status: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isSuccess) StatusReady else StatusWarning,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = status,
            color = if (isSuccess) TextPrimary else StatusWarning,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun HeroGameCard(
    game: com.gameboost.optimizer.models.GameProfile,
    isSelected: Boolean,
    isOptimized: Boolean,
    isBoosting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isOptimized -> StatusReady
        isSelected -> BorderActive
        else -> BorderSubtle
    }

    Card(
        modifier = modifier
            .width(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceElevated else SurfaceCard
        ),
        border = BorderStroke(
            width = if (isSelected || isOptimized) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                GameIconView(
                    packageName = game.activePackageName,
                    isInstalled = game.isInstalled,
                    sizeDp = 56.dp
                )

                val (badgeText, badgeState) = when {
                    !game.isInstalled -> Pair("NOT INSTALLED", BadgeState.NEUTRAL)
                    isOptimized -> Pair("BOOSTED", BadgeState.SUCCESS)
                    else -> Pair("READY", BadgeState.SUCCESS)
                }
                StatusBadge(text = badgeText, state = badgeState)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = game.displayName,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${game.region} • ${game.activePackageName}",
                color = TextTertiary,
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        when {
                            isOptimized -> StatusReady
                            isSelected -> AccentPrimary
                            else -> Color.Transparent
                        }
                    )
            )
        }
    }
}

@Composable
fun TurboIgniteButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    isBoosting: Boolean = false,
    text: String = "TURBO IGNITE & PLAY",
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isBoosting,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentPrimary,
            contentColor = DarkBg,
            disabledContainerColor = SurfaceElevated,
            disabledContentColor = TextTertiary
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        if (isBoosting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = DarkBg,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "IGNITING TURBO...",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

// ==========================================
// 11. STANDARDIZED DESIGN SYSTEM COMPONENTS
// ==========================================

/**
 * Standard GameIcon composable wrapping PackageManager icon retrieval.
 */
@Composable
fun GameIcon(
    packageName: String?,
    isInstalled: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 52.dp
) {
    GameIconView(
        packageName = packageName,
        isInstalled = isInstalled,
        modifier = modifier,
        sizeDp = sizeDp
    )
}

/**
 * Standard GameBoostCard with restrained corner radius and subtle graphite borders.
 */
@Composable
fun GameBoostCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = BorderStroke(1.dp, BorderSubtle),
    backgroundColor: Color = SurfaceElevated,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        border = border,
        backgroundColor = backgroundColor,
        content = content
    )
}

/**
 * Tactical game card displaying actual PackageManager icon, display name, and installation state.
 */
@Composable
fun GameCard(
    game: com.gameboost.optimizer.models.GameProfile,
    isSelected: Boolean,
    isOptimized: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HeroGameCard(
        game = game,
        isSelected = isSelected,
        isOptimized = isOptimized,
        isBoosting = false,
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * Capability execution states matching technical audit requirements.
 */
enum class CapabilityStatus(val displayName: String, val badgeState: BadgeState) {
    SUPPORTED("SUPPORTED", BadgeState.INFO),
    ACTIVE("ACTIVE", BadgeState.SUCCESS),
    FAILED("FAILED", BadgeState.ERROR),
    UNAVAILABLE("UNAVAILABLE", BadgeState.WARNING),
    NOT_SUPPORTED("NOT SUPPORTED", BadgeState.NEUTRAL)
}

/**
 * Capability row showing feature name and technical status indicator.
 */
@Composable
fun CapabilityRow(
    title: String,
    status: CapabilityStatus,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        StatusBadge(
            text = status.displayName,
            state = status.badgeState
        )
    }
}

/**
 * Compact status tile for telemetry and states.
 */
@Composable
fun StatusTile(
    label: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = AccentPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label.uppercase(),
                    color = TextTertiary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Compact telemetry tile for dashboard metrics.
 */
@Composable
fun TelemetryTile(
    label: String,
    value: String,
    icon: ImageVector? = null,
    accentColor: Color = AccentPrimary,
    modifier: Modifier = Modifier
) {
    MetricChip(
        label = label,
        value = value,
        icon = icon,
        accentColor = accentColor,
        modifier = modifier
    )
}

/**
 * Primary action button for Turbo / Boost triggers (Height >= 56dp, min target >= 48dp).
 */
@Composable
fun PrimaryBoostButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    isBoosting: Boolean = false,
    text: String = "BOOST & PLAY",
    modifier: Modifier = Modifier
) {
    TurboIgniteButton(
        onClick = onClick,
        enabled = enabled,
        isBoosting = isBoosting,
        text = text,
        modifier = modifier
    )
}

/**
 * Privileged backend status card displaying connection mode and access info.
 */
@Composable
fun BackendStatusCard(
    backendType: com.gameboost.optimizer.system.BackendType,
    isReady: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (backendType) {
        com.gameboost.optimizer.system.BackendType.SHIZUKU -> "SHIZUKU SERVICE"
        com.gameboost.optimizer.system.BackendType.WIRELESS_ADB -> "WIRELESS DEBUGGING"
        else -> "UNPRIVILEGED MODE"
    }
    val desc = when {
        isReady && backendType == com.gameboost.optimizer.system.BackendType.SHIZUKU ->
            "Active binder interface connected. Full display and game mode controls enabled."
        isReady && backendType == com.gameboost.optimizer.system.BackendType.WIRELESS_ADB ->
            "Direct loopback TLS shell connected. Full privileged commands enabled."
        else ->
            "Connect Shizuku or Wireless ADB to unlock 120Hz display locking and performance control."
    }

    GameBoostCard(
        modifier = modifier,
        border = BorderStroke(1.dp, if (isReady) BorderSubtle else StatusWarning.copy(alpha = 0.5f)),
        backgroundColor = SurfaceElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isReady) StatusReady else StatusWarning,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onAction,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isReady) TextPrimary else StatusWarning
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isReady) "STATUS" else "CONNECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

