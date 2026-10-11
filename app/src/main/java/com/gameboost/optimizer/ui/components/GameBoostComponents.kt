package com.gameboost.optimizer.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
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
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.DeepRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBgSecondary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreBorderSubtle
import com.gameboost.optimizer.theme.RedCoreDanger
import com.gameboost.optimizer.theme.RedCoreDangerContainer
import com.gameboost.optimizer.theme.RedCoreDarkCrimson
import com.gameboost.optimizer.theme.RedCoreGlow
import com.gameboost.optimizer.theme.RedCoreGlowBright
import com.gameboost.optimizer.theme.RedCoreGlowSubtle
import com.gameboost.optimizer.theme.RedCoreSuccess
import com.gameboost.optimizer.theme.RedCoreSuccessContainer
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextRed
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.theme.RedCoreWarningContainer

// =========================================================================
// 1. GAMEBOOST BRAND LOGO
// =========================================================================

@Composable
fun GameBoostLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 34.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_game_boost_tactical),
        contentDescription = "GameBoost Tactical Logo",
        modifier = modifier.size(sizeDp)
    )
}

// =========================================================================
// 2. REAL GAME APPLICATION ICON (WITH REDMAGIC ANGULAR FRAME)
// =========================================================================

@Composable
fun GameIconView(
    packageName: String?,
    isInstalled: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 56.dp,
    isActive: Boolean = false
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

    val borderColor = if (isActive) BrightActiveRed else if (isInstalled) RedCoreBorder else RedCoreBorderSubtle
    val cornerCut = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(cornerCut)
            .background(RedCoreSurfaceElevated)
            .border(BorderStroke(1.dp, borderColor), cornerCut),
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
                        .clip(cornerCut)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = if (isActive) BrightActiveRed else PrimaryGamingRed,
                    modifier = Modifier.size(sizeDp * 0.55f)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = if (isInstalled) RedCoreTextSecondary else RedCoreTextMuted,
                modifier = Modifier.size(sizeDp * 0.55f)
            )
        }
    }
}

@Composable
private fun rememberBitmap(drawable: Drawable, sizeDp: Dp): androidx.compose.ui.graphics.ImageBitmap? {
    return remember(drawable) {
        try {
            val px = 128
            drawable.toBitmap(px, px).asImageBitmap()
        } catch (_: Throwable) {
            null
        }
    }
}

// =========================================================================
// 3. RED CORE ANGULAR PANEL / CARD
// Dark industrial surface, angular chamfered corners, subtle red edge
// =========================================================================

val RedCoreAngularShape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
val RedCoreRestrainedShape = RoundedCornerShape(8.dp)

@Composable
fun RedCoreCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isActive: Boolean = false,
    borderColor: Color = if (isActive) RedCoreBorderActive else RedCoreBorder,
    backgroundColor: Color = RedCoreSurfaceCard,
    shape: Shape = RedCoreAngularShape,
    content: @Composable ColumnScope.() -> Unit
) {
    val animatedBorder by animateColorAsState(
        targetValue = if (isActive) RedCoreBorderActive else borderColor,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "panelBorder"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, animatedBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// Compatibility wrapper for GlassCard
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = BorderStroke(1.dp, RedCoreBorder),
    backgroundColor: Color = RedCoreSurfaceCard,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RedCoreAngularShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// =========================================================================
// 4. PRECISION STATUS INDICATORS & BADGES
// =========================================================================

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
        BadgeState.SUCCESS -> Pair(RedCoreSuccessContainer, RedCoreSuccess)
        BadgeState.WARNING -> Pair(RedCoreWarningContainer, RedCoreWarning)
        BadgeState.ERROR -> Pair(RedCoreDangerContainer, RedCoreDanger)
        BadgeState.INFO -> Pair(RedCoreDarkCrimson, BrightActiveRed)
        BadgeState.NEUTRAL -> Pair(RedCoreSurfaceElevated, RedCoreTextSecondary)
    }

    Surface(
        modifier = modifier,
        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(fg, CutCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text.uppercase(),
                color = fg,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    state: BadgeState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bg, fg) = when (state) {
        BadgeState.SUCCESS -> Pair(RedCoreSuccessContainer, RedCoreSuccess)
        BadgeState.WARNING -> Pair(RedCoreWarningContainer, RedCoreWarning)
        BadgeState.ERROR -> Pair(RedCoreDangerContainer, RedCoreDanger)
        BadgeState.INFO -> Pair(RedCoreDarkCrimson, BrightActiveRed)
        BadgeState.NEUTRAL -> Pair(RedCoreSurfaceElevated, RedCoreTextSecondary)
    }

    Surface(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(fg, CutCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text.uppercase(),
                color = fg,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
    }
}

// =========================================================================
// 5. HUD TELEMETRY BLOCKS (REAL SENSOR & HARDWARE DATA)
// Large numbers, technical labels, sharp red accents
// =========================================================================

@Composable
fun MetricChip(
    label: String,
    value: String,
    icon: ImageVector? = null,
    accentColor: Color = PrimaryGamingRed,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RedCoreAngularShape,
        color = RedCoreSurfaceElevated,
        border = BorderStroke(1.dp, RedCoreBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sharp red left status indicator
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(28.dp)
                    .background(accentColor)
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column {
                Text(
                    text = label.uppercase(),
                    color = RedCoreTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = value,
                    color = RedCoreTextPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

@Composable
fun TelemetryItem(
    label: String,
    value: String,
    icon: ImageVector? = null,
    accentColor: Color = PrimaryGamingRed,
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
fun StatusTile(
    label: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = PrimaryGamingRed,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RedCoreAngularShape,
        color = RedCoreSurfaceElevated,
        border = BorderStroke(1.dp, RedCoreBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 10.dp)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = label.uppercase(),
                        color = RedCoreTextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = RedCoreTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.4.sp
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = RedCoreTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun TelemetryTile(
    label: String,
    value: String,
    icon: ImageVector? = null,
    accentColor: Color = PrimaryGamingRed,
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

// =========================================================================
// 6. TECHNICAL SECTION HEADERS
// Small uppercase title with red prefix markers and thin divider line
// =========================================================================

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // RedMagic style indicator bar
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 13.dp)
                        .background(PrimaryGamingRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = RedCoreTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
            }
            if (actionText != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionText.uppercase(),
                        color = BrightActiveRed,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = RedCoreTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 9.dp)
            )
        }
    }
}

// =========================================================================
// 7. MAIN BOOST & PLAY CONTROL (SECTION 4 & 5)
// Dominant red button, pulsing perimeter glow while ready, light sweep, real progress
// =========================================================================

@Composable
fun PrimaryBoostButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    isBoosting: Boolean = false,
    text: String = "BOOST & PLAY",
    subtext: String = "OPTIMIZE GAMING SESSION",
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Ready state pulsing perimeter glow
    val infiniteTransition = rememberInfiniteTransition(label = "boostPulse")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val buttonScale = if (isPressed) 0.97f else 1f

    val buttonShape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = buttonScale
                scaleY = buttonScale
            },
        contentAlignment = Alignment.Center
    ) {
        // Glowing perimeter aura when enabled & ready
        if (enabled && !isBoosting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(1.dp)
                    .clip(buttonShape)
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    DeepRed.copy(alpha = pulseGlowAlpha),
                                    BrightActiveRed.copy(alpha = pulseGlowAlpha),
                                    DeepRed.copy(alpha = pulseGlowAlpha)
                                )
                            )
                        ),
                        buttonShape
                    )
            )
        }

        Button(
            onClick = onClick,
            enabled = enabled && !isBoosting,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = RedCoreSurfaceElevated,
                contentColor = RedCoreTextPrimary,
                disabledContentColor = RedCoreTextMuted
            ),
            shape = buttonShape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (enabled) {
                            Brush.linearGradient(
                                colors = listOf(
                                    BrightActiveRed,
                                    PrimaryGamingRed,
                                    DeepRed
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(RedCoreSurfaceElevated, RedCoreSurfaceElevated)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isBoosting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "APPLYING HARDWARE PROFILE...",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.2.sp
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (enabled) Color.White else RedCoreTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = text,
                                color = if (enabled) Color.White else RedCoreTextMuted,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 1.4.sp
                            )
                            Text(
                                text = subtext,
                                color = if (enabled) Color.White.copy(alpha = 0.85f) else RedCoreTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Backward-compatible alias for TurboIgniteButton
@Composable
fun TurboIgniteButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    isBoosting: Boolean = false,
    text: String = "TURBO IGNITE & PLAY",
    modifier: Modifier = Modifier
) {
    PrimaryBoostButton(
        onClick = onClick,
        enabled = enabled,
        isBoosting = isBoosting,
        text = text,
        subtext = "OPTIMIZE GAMING SESSION",
        modifier = modifier
    )
}

// =========================================================================
// 8. MECHANICAL GAMING SWITCH (SECTION 10)
// Custom dark switch with red active glow and sliding thumb
// =========================================================================

@Composable
fun RedCoreSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "switchThumb"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) RedCoreDarkCrimson else RedCoreSurfaceElevated,
        animationSpec = tween(180),
        label = "switchTrack"
    )

    val borderColor by animateColorAsState(
        targetValue = if (checked) PrimaryGamingRed else RedCoreBorder,
        animationSpec = tween(180),
        label = "switchBorder"
    )

    val thumbColor by animateColorAsState(
        targetValue = if (checked) BrightActiveRed else RedCoreTextMuted,
        animationSpec = tween(180),
        label = "switchThumbColor"
    )

    Box(
        modifier = modifier
            .width(46.dp)
            .height(26.dp)
            .clip(CutCornerShape(4.dp))
            .background(trackColor)
            .border(BorderStroke(1.dp, borderColor), CutCornerShape(4.dp))
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .size(width = 18.dp, height = 18.dp)
                .clip(CutCornerShape(2.dp))
                .background(thumbColor)
        )
    }
}

// =========================================================================
// 9. DEDICATED DISPLAY AND FPS PANEL (SECTION 8)
// Distinguishes display refresh rate from in-game frame rate
// =========================================================================

@Composable
fun DisplayFpsPanel(
    currentDisplayRate: Float,
    supportedRates: List<Float>,
    targetRate: Float,
    onSelectRate: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    RedCoreCard(
        modifier = modifier,
        shape = RedCoreAngularShape,
        backgroundColor = RedCoreSurfaceElevated,
        borderColor = RedCoreBorderActive
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 12.dp)
                        .background(PrimaryGamingRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DISPLAY TELEMETRY & FPS MATRIX",
                    color = RedCoreTextPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            StatusBadge(text = "HARDWARE SYNC", state = BadgeState.INFO)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Two-column comparison: Display Refresh Rate vs Game Frame Rate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display Refresh Rate
            Surface(
                modifier = Modifier.weight(1f),
                shape = RedCoreAngularShape,
                color = RedCoreDarkCrimson,
                border = BorderStroke(1.dp, BrightActiveRed.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "DISPLAY REFRESH RATE",
                        color = BrightActiveRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentDisplayRate.toInt()} Hz",
                        color = RedCoreTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Active panel sync",
                        color = RedCoreTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Game Frame Rate (Distinguished!)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RedCoreAngularShape,
                color = RedCoreBgSecondary,
                border = BorderStroke(1.dp, RedCoreBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "GAME FRAME RATE",
                        color = RedCoreTextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NOT VERIFIED",
                        color = RedCoreWarning,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Requires in-game render hook",
                        color = RedCoreTextMuted,
                        fontSize = 9.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Supported Modes Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SUPPORTED HARDWARE MODES",
                color = RedCoreTextSecondary,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            val availableOptions = remember(supportedRates) {
                val list = mutableListOf(0f)
                if (supportedRates.any { it in 59f..61f }) list.add(60f)
                if (supportedRates.any { it in 89f..91f }) list.add(90f)
                if (supportedRates.any { it in 119f..121f }) list.add(120f)
                if (supportedRates.any { it in 143f..145f }) list.add(144f)
                list.distinct()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableOptions.forEach { rate ->
                    val isSelected = (targetRate == rate) || (rate == 0f && targetRate <= 0f)
                    val label = if (rate <= 0f) "AUTO" else "${rate.toInt()}Hz"

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (onSelectRate != null) Modifier.clickable { onSelectRate(rate) }
                                else Modifier
                            ),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        color = if (isSelected) PrimaryGamingRed else RedCoreBgSecondary,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) BrightActiveRed else RedCoreBorder
                        )
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else RedCoreTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Technical Notice
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .background(RedCoreBgSecondary, CutCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = RedCoreTextSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Notice: Display panel refresh rate (e.g. 90/120Hz) does not guarantee in-game 90/120 FPS rendering. Game engine caps still apply.",
                color = RedCoreTextSecondary,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

// =========================================================================
// 10. PERFORMANCE MODE SELECTOR (SECTION 7)
// BALANCED, PERFORMANCE, EXTREME with RedMagic styling
// =========================================================================

@Composable
fun PerformanceModeSelector(
    selectedMode: PerformanceMode,
    onModeSelected: (PerformanceMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple(PerformanceMode.SAFE, "BALANCED", "Reliable everyday gaming. Optimal thermals and battery."),
                Triple(PerformanceMode.PERFORMANCE, "PERFORMANCE", "Prioritize supported gaming performance controls."),
                Triple(PerformanceMode.AGGRESSIVE, "EXTREME", "Strongest available safe settings & process priority.")
            ).forEach { (mode, label, _) ->
                val isSelected = selectedMode == mode

                val animatedBorder by animateColorAsState(
                    targetValue = if (isSelected) BrightActiveRed else RedCoreBorder,
                    animationSpec = tween(180),
                    label = "profBorder"
                )

                val animatedBg by animateColorAsState(
                    targetValue = if (isSelected) RedCoreDarkCrimson else RedCoreSurfaceElevated,
                    animationSpec = tween(180),
                    label = "profBg"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeSelected(mode) },
                    shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                    color = animatedBg,
                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, animatedBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) BrightActiveRed else RedCoreTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .background(if (isSelected) BrightActiveRed else Color.Transparent)
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
            shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
            color = if (isThermalSelected) RedCoreDangerContainer else RedCoreSurfaceElevated,
            border = BorderStroke(
                1.dp,
                if (isThermalSelected) RedCoreDanger else RedCoreBorder
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
                        tint = if (isThermalSelected) RedCoreDanger else RedCoreWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THERMAL OVERRIDE (MAX SUSTAINED CLOCKS)",
                        color = if (isThermalSelected) RedCoreDanger else RedCoreTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                StatusBadge(
                    text = "EXPERIMENTAL",
                    state = BadgeState.ERROR
                )
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

// =========================================================================
// 11. REFRESH RATE SELECTOR
// =========================================================================

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
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { rate ->
            val isSelected = (selectedTarget == rate) || (rate == 0f && selectedTarget <= 0f)
            val label = if (rate <= 0f) "AUTO MAX" else "${rate.toInt()}Hz"

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectRate(rate) },
                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                color = if (isSelected) RedCoreDarkCrimson else RedCoreSurfaceElevated,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) BrightActiveRed else RedCoreBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) BrightActiveRed else RedCoreTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

// =========================================================================
// 12. BOTTOM NAVIGATION BAR (SECTION 11)
// 5 Destinations: HOME, GAMES, BOOST, DIAGNOSTICS, SETTINGS
// Compact, red active indicator, gaming phone look
// =========================================================================

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
        color = RedCoreBgPrimary,
        border = BorderStroke(1.dp, RedCoreBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainNavItems.forEach { item ->
                val isSelected = currentKey::class == item.key::class

                val indicatorColor by animateColorAsState(
                    targetValue = if (isSelected) BrightActiveRed else Color.Transparent,
                    animationSpec = tween(160),
                    label = "navIndicator"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clickable { onNavigate(item.key) },
                    shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                    color = if (isSelected) RedCoreDarkCrimson else Color.Transparent,
                    border = if (isSelected) BorderStroke(1.dp, BrightActiveRed.copy(alpha = 0.6f)) else null
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top active red notch
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(2.dp)
                                .background(indicatorColor)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) BrightActiveRed else RedCoreTextMuted,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            color = if (isSelected) BrightActiveRed else RedCoreTextSecondary
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 13. FEATURED GAME CARD & HERO CARD (SECTION 5 & 6)
// =========================================================================

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
        isOptimized -> RedCoreSuccess
        isSelected -> BrightActiveRed
        else -> RedCoreBorder
    }

    Card(
        modifier = modifier
            .width(260.dp)
            .clickable { onClick() },
        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) RedCoreSurfaceElevated else RedCoreSurfaceCard
        ),
        border = BorderStroke(
            width = if (isSelected || isOptimized) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                GameIconView(
                    packageName = game.activePackageName,
                    isInstalled = game.isInstalled,
                    sizeDp = 52.dp,
                    isActive = isSelected
                )

                val (badgeText, badgeState) = when {
                    !game.isInstalled -> Pair("NOT INSTALLED", BadgeState.NEUTRAL)
                    isOptimized -> Pair("ACTIVE BOOST", BadgeState.SUCCESS)
                    else -> Pair("INSTALLED", BadgeState.SUCCESS)
                }
                StatusBadge(text = badgeText, state = badgeState)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = game.displayName,
                color = RedCoreTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${game.region} • ${game.activePackageName}",
                color = RedCoreTextSecondary,
                fontSize = 10.5.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        when {
                            isOptimized -> RedCoreSuccess
                            isSelected -> BrightActiveRed
                            else -> Color.Transparent
                        }
                    )
            )
        }
    }
}

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

// =========================================================================
// 14. CAPABILITY EXECUTION ROW
// =========================================================================

enum class CapabilityStatus(val displayName: String, val badgeState: BadgeState) {
    AVAILABLE("AVAILABLE", BadgeState.INFO),
    SUPPORTED("AVAILABLE", BadgeState.INFO),
    ACTIVE("ACTIVE", BadgeState.SUCCESS),
    FAILED("FAILED", BadgeState.ERROR),
    UNAVAILABLE("UNAVAILABLE", BadgeState.WARNING),
    NOT_SUPPORTED("NOT SUPPORTED", BadgeState.NEUTRAL)
}

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
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = title,
                color = RedCoreTextPrimary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = RedCoreTextSecondary,
                    fontSize = 10.5.sp
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
                tint = if (isSuccess) RedCoreSuccess else RedCoreWarning,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = RedCoreTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = status,
            color = if (isSuccess) RedCoreTextPrimary else RedCoreWarning,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =========================================================================
// 15. BACKEND STATUS CARD
// =========================================================================

@Composable
fun ConnectionStatus(
    backendType: com.gameboost.optimizer.system.BackendType,
    isReady: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (badgeText, badgeState) = when {
        backendType == com.gameboost.optimizer.system.BackendType.SHIZUKU && isReady -> Pair("SHIZUKU CONNECTED", BadgeState.SUCCESS)
        backendType == com.gameboost.optimizer.system.BackendType.WIRELESS_ADB && isReady -> Pair("WIRELESS ADB", BadgeState.INFO)
        backendType == com.gameboost.optimizer.system.BackendType.SHIZUKU -> Pair("SHIZUKU DISCONNECTED", BadgeState.WARNING)
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

    RedCoreCard(
        modifier = modifier,
        borderColor = if (isReady) RedCoreBorder else RedCoreWarning.copy(alpha = 0.5f),
        backgroundColor = RedCoreSurfaceElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isReady) RedCoreSuccess else RedCoreWarning,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = RedCoreTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onAction,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isReady) RedCoreTextPrimary else RedCoreWarning
                ),
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, if (isReady) RedCoreBorder else RedCoreWarning)
            ) {
                Text(if (isReady) "STATUS" else "CONNECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Standard GameBoostCard compatibility alias
@Composable
fun GameBoostCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = BorderStroke(1.dp, RedCoreBorder),
    backgroundColor: Color = RedCoreSurfaceElevated,
    content: @Composable ColumnScope.() -> Unit
) {
    RedCoreCard(
        modifier = modifier,
        onClick = onClick,
        borderColor = border?.brush?.let { RedCoreBorder } ?: RedCoreBorder,
        backgroundColor = backgroundColor,
        content = content
    )
}

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

// =========================================================================
// 16. THERMAL RISK CONFIRMATION DIALOG
// =========================================================================

@Composable
fun ThermalRiskDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RedCoreSurfaceCard,
        titleContentColor = RedCoreDanger,
        textContentColor = RedCoreTextPrimary,
        shape = CutCornerShape(10.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = RedCoreDanger,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "THERMAL RISK WARNING",
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
                    color = RedCoreTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This mode may significantly increase:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedCoreTextSecondary
                )
                Text(
                    text = "• Device temperature\n• Battery wear\n• System instability\n• Power consumption",
                    fontSize = 12.sp,
                    color = RedCoreWarning,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "OEM and Android kernel thermal protection will NOT be bypassed and may still override this mode.",
                    fontSize = 11.sp,
                    color = RedCoreTextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = RedCoreDanger),
                shape = CutCornerShape(4.dp)
            ) {
                Text(text = "I Understand the Risks", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, RedCoreBorder)
            ) {
                Text(text = "Cancel", color = RedCoreTextSecondary)
            }
        }
    )
}
