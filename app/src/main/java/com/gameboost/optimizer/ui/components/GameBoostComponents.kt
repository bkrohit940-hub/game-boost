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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    sizeDp: Dp = 32.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_game_boost_logo),
        contentDescription = "Game Boost Logo",
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
    NavItem(HomeKey, "Home", Icons.Default.Home),
    NavItem(GamesKey, "Games", Icons.Default.Apps),
    NavItem(BoostKey, "Boost", Icons.Default.Bolt),
    NavItem(DiagnosticsKey, "Diag", Icons.Default.Terminal),
    NavItem(SettingsKey, "Settings", Icons.Default.Settings)
)

@Composable
fun GameBoostNavBar(
    currentKey: NavKey,
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = DarkBg,
        tonalElevation = 0.dp
    ) {
        MainNavItems.forEach { item ->
            val isSelected = currentKey::class == item.key::class
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.key) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentPrimary,
                    selectedTextColor = AccentPrimary,
                    indicatorColor = AccentPrimaryContainer,
                    unselectedIconColor = TextTertiary,
                    unselectedTextColor = TextTertiary
                )
            )
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
                PerformanceMode.SAFE,
                PerformanceMode.PERFORMANCE,
                PerformanceMode.AGGRESSIVE
            ).forEach { mode ->
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
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = mode.displayName.uppercase(),
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
