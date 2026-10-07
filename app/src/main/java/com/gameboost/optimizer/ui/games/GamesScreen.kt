package com.gameboost.optimizer.ui.games

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderActive
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.GlassCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

@Composable
fun GamesScreen(
    games: List<GameProfile>,
    isOptimized: Boolean,
    activeGameProfile: GameProfile?,
    isBoosting: Boolean,
    onBoostAndPlay: (String) -> Unit,
    onBoostOnly: (String) -> Unit,
    onPlayOnly: (String) -> Unit,
    onGameDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Supported Games",
                    subtitle = "Official launcher center for supported battle royale titles"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val installed = games.filter { it.isInstalled }
            val notInstalled = games.filter { !it.isInstalled }

            if (installed.isNotEmpty()) {
                item {
                    Text(
                        text = "INSTALLED GAMES (${installed.size})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                items(installed, key = { it.id }) { game ->
                    val isActive = isOptimized && activeGameProfile?.id == game.id
                    GameItemRow(
                        game = game,
                        isActive = isActive,
                        isBoosting = isBoosting,
                        onBoostAndPlay = { onBoostAndPlay(game.id) },
                        onBoostOnly = { onBoostOnly(game.id) },
                        onPlayOnly = { onPlayOnly(game.id) },
                        onDetail = { onGameDetail(game.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (notInstalled.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AVAILABLE SUPPORTED TITLES (${notInstalled.size})",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                items(notInstalled, key = { it.id }) { game ->
                    GameItemRow(
                        game = game,
                        isActive = false,
                        isBoosting = false,
                        onBoostAndPlay = {},
                        onBoostOnly = {},
                        onPlayOnly = {},
                        onDetail = { onGameDetail(game.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GameItemRow(
    game: GameProfile,
    isActive: Boolean,
    isBoosting: Boolean,
    onBoostAndPlay: () -> Unit,
    onBoostOnly: () -> Unit,
    onPlayOnly: () -> Unit,
    onDetail: () -> Unit
) {
    GlassCard(
        border = BorderStroke(
            1.dp,
            if (isActive) StatusReady else BorderSubtle
        ),
        backgroundColor = if (isActive) SurfaceElevated else SurfaceCard,
        onClick = onDetail
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameIconView(
                packageName = game.activePackageName,
                isInstalled = game.isInstalled,
                sizeDp = 48.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = game.displayName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val (badgeText, badgeState) = when {
                        !game.isInstalled -> Pair("NOT INSTALLED", BadgeState.NEUTRAL)
                        isActive -> Pair("BOOST ACTIVE", BadgeState.SUCCESS)
                        else -> Pair("READY", BadgeState.SUCCESS)
                    }
                    StatusBadge(text = badgeText, state = badgeState)
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${game.region} • ${game.activePackageName}",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
                if (game.appVersion != null) {
                    Text(
                        text = "Version: ${game.appVersion}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        if (game.isInstalled) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBoostAndPlay,
                    enabled = !isBoosting,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = DarkBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isBoosting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = DarkBg,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = "BOOST & PLAY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onBoostOnly,
                    enabled = !isBoosting,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("BOOST ONLY", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onPlayOnly,
                    modifier = Modifier
                        .weight(0.7f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
