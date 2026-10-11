package com.gameboost.optimizer.ui.startup

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.R
import com.gameboost.optimizer.theme.BrightActiveRed
import com.gameboost.optimizer.theme.DeepRed
import com.gameboost.optimizer.theme.PrimaryGamingRed
import com.gameboost.optimizer.theme.RedCoreBgPrimary
import com.gameboost.optimizer.theme.RedCoreBorder
import com.gameboost.optimizer.theme.RedCoreBorderActive
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * GameBoost Red Core Startup Sequence:
 * 1. Fast logo fade-in with subtle scaling.
 * 2. Restrained red light sweep across the emblem.
 * 3. Title reveal with quick opacity transition.
 * 4. Creator attribution: "Created by Rohit B.K".
 * 5. Instant and responsive handoff to the command center (no fake percentage loaders).
 */
@Composable
fun StartupScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.92f) }
    val lightSweep = remember { Animatable(-1f) }
    val textAlpha = remember { Animatable(0f) }
    val totalExit = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Step 1: Logo fade-in (220ms)
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(220, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = tween(220, easing = FastOutSlowInEasing))
        }

        delay(120)

        // Step 2: Red light sweep & title reveal concurrently (320ms)
        launch {
            textAlpha.animateTo(1f, animationSpec = tween(240, easing = FastOutSlowInEasing))
        }
        lightSweep.animateTo(1.5f, animationSpec = tween(380, easing = LinearEasing))

        // Short pause to appreciate the brand (140ms)
        delay(140)

        // Step 3: Fast smooth exit transition (160ms)
        totalExit.animateTo(0f, animationSpec = tween(160, easing = FastOutSlowInEasing))
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RedCoreBgPrimary)
            .graphicsLayer { alpha = totalExit.value },
        contentAlignment = Alignment.Center
    ) {
        // Subtle HUD corner brackets for RedMagic aesthetic
        HudCornerBrackets()

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tactical GameBoost Logo Container with Red Light Sweep
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
                    .size(92.dp),
                contentAlignment = Alignment.Center
            ) {
                // Subtle red glowing aura behind the logo
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PrimaryGamingRed.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // The Official GameBoost Emblem Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_game_boost_emblem),
                    contentDescription = "GameBoost Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .drawWithContent {
                            drawContent()
                            // Restrained red light sweep overlay
                            val sweepPos = lightSweep.value
                            if (sweepPos in -0.5f..1.5f) {
                                val brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BrightActiveRed.copy(alpha = 0.55f),
                                        Color.White.copy(alpha = 0.35f),
                                        Color.Transparent
                                    ),
                                    start = Offset(size.width * (sweepPos - 0.3f), 0f),
                                    end = Offset(size.width * (sweepPos + 0.3f), size.height)
                                )
                                drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Application Title & Technical Tag
            Column(
                modifier = Modifier.alpha(textAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(PrimaryGamingRed, CutCornerShape(1.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RED CORE // PERFORMANCE CONTROL",
                        color = RedCoreTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(PrimaryGamingRed, CutCornerShape(1.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Creator Attribution: "Created by Rohit B.K"
            Row(
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .clip(RoundedCornerShape(20.dp))
                    .background(RedCoreSurfaceElevated)
                    .border(1.dp, RedCoreBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(1.dp, RedCoreBorderActive, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.creator_profile),
                        contentDescription = "Rohit B.K",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Created by Rohit B.K",
                    color = RedCoreTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun HudCornerBrackets() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top Left Bracket
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .size(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 2.dp)
                    .background(DeepRed)
            )
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 16.dp)
                    .background(DeepRed)
            )
        }

        // Top Right Bracket
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(width = 16.dp, height = 2.dp)
                    .background(DeepRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(width = 2.dp, height = 16.dp)
                    .background(DeepRed)
            )
        }

        // Bottom Left Bracket
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .size(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(width = 16.dp, height = 2.dp)
                    .background(DeepRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(width = 2.dp, height = 16.dp)
                    .background(DeepRed)
            )
        }

        // Bottom Right Bracket
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(width = 16.dp, height = 2.dp)
                    .background(DeepRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(width = 2.dp, height = 16.dp)
                    .background(DeepRed)
            )
        }
    }
}
