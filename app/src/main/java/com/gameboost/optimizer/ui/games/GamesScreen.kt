package com.gameboost.optimizer.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.GameProfile
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
import com.gameboost.optimizer.theme.RedCoreSurfaceCard
import com.gameboost.optimizer.theme.RedCoreSurfaceElevated
import com.gameboost.optimizer.theme.RedCoreTextMuted
import com.gameboost.optimizer.theme.RedCoreTextPrimary
import com.gameboost.optimizer.theme.RedCoreTextSecondary
import com.gameboost.optimizer.theme.RedCoreWarning
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.GameIconView
import com.gameboost.optimizer.ui.components.RedCoreCard
import com.gameboost.optimizer.ui.components.SectionHeader
import com.gameboost.optimizer.ui.components.StatusBadge

private enum class GameFilterTab {
    ALL, INSTALLED, SUPPORTED
}

/**
 * GAMES: GAME SPACE LIBRARY (Section 6)
 * RedMagic Game Space inspired gaming library:
 * - Installed games & supported regional variants
 * - Featured selected game with large icon
 * - Compact gaming tiles with actual icons
 * - Real Android package info (no fake data)
 * - Restrained red frame & animated selection state
 * - Search & Filter tabs (ALL, INSTALLED, SUPPORTED)
 * - Empty state with clear scan action
 */
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
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(GameFilterTab.ALL) }
    val focusManager = LocalFocusManager.current

    // Filter games
    val filteredGames = remember(games, searchQuery, selectedFilter) {
        games.filter { game ->
            val matchesSearch = searchQuery.isBlank() ||
                game.displayName.contains(searchQuery, ignoreCase = true) ||
                game.activePackageName.contains(searchQuery, ignoreCase = true) ||
                game.region.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                GameFilterTab.ALL -> true
                GameFilterTab.INSTALLED -> game.isInstalled
                GameFilterTab.SUPPORTED -> game.supportedFeatures.isNotEmpty()
            }

            matchesSearch && matchesFilter
        }
    }

    val installedCount = games.count { it.isInstalled }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = RedCoreBgPrimary
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrowScreen = maxWidth < 360.dp
            val horizontalPadding = if (isNarrowScreen) 10.dp else 14.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // =========================================================================
                // 1. HEADER: GAME SPACE TITLE & STATS
                // =========================================================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 14.dp)
                                        .background(PrimaryGamingRed)
                                    )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GAME SPACE LIBRARY",
                                    color = RedCoreTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$installedCount INSTALLED • ${games.size} SUPPORTED TITLES",
                                color = RedCoreTextSecondary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        StatusBadge(
                            text = if (installedCount > 0) "READY" else "SCAN NEEDED",
                            state = if (installedCount > 0) BadgeState.SUCCESS else BadgeState.WARNING
                        )
                    }
                }

                // =========================================================================
                // 2. SEARCH BAR & FILTER TABS
                // =========================================================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Tactical Search Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                            color = RedCoreSurfaceElevated,
                            border = BorderStroke(1.dp, RedCoreBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = PrimaryGamingRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(
                                        color = RedCoreTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(BrightActiveRed),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                    decorationBox = { innerTextField ->
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "SEARCH BY TITLE, REGION, OR PACKAGE...",
                                                color = RedCoreTextMuted,
                                                fontSize = 11.5.sp,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filter Tabs: ALL, INSTALLED, SUPPORTED
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GameFilterTab.entries.forEach { tab ->
                                val isSelected = selectedFilter == tab
                                val label = when (tab) {
                                    GameFilterTab.ALL -> "ALL (${games.size})"
                                    GameFilterTab.INSTALLED -> "INSTALLED ($installedCount)"
                                    GameFilterTab.SUPPORTED -> "SUPPORTED"
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedFilter = tab },
                                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                    color = if (isSelected) RedCoreDarkCrimson else RedCoreSurfaceElevated,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) BrightActiveRed else RedCoreBorder
                                    )
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) BrightActiveRed else RedCoreTextSecondary,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 3. GAME TILES LIST
                // =========================================================================
                if (filteredGames.isEmpty()) {
                    item {
                        RedCoreCard(
                            backgroundColor = RedCoreSurfaceElevated,
                            borderColor = RedCoreBorder
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = RedCoreTextMuted,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "NO TITLES FOUND",
                                    color = RedCoreTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No games matched your current filter criteria.",
                                    color = RedCoreTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredGames) { game ->
                        val isSessionActive = isOptimized && activeGameProfile?.id == game.id

                        val animatedBorder by animateColorAsState(
                            targetValue = if (isSessionActive) RedCoreSuccess else if (game.isInstalled) RedCoreBorderActive else RedCoreBorder,
                            animationSpec = tween(200),
                            label = "gameTileBorder"
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGameDetail(game.id) },
                            shape = RedCoreAngularShape,
                            color = if (isSessionActive) RedCoreDarkCrimson else RedCoreSurfaceElevated,
                            border = BorderStroke(1.dp, animatedBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Real App Icon with Cut Corner Frame
                                    GameIconView(
                                        packageName = game.activePackageName,
                                        isInstalled = game.isInstalled,
                                        sizeDp = 52.dp,
                                        isActive = isSessionActive
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
                                                color = RedCoreTextPrimary,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Black,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            StatusBadge(
                                                text = if (isSessionActive) "ACTIVE" else if (game.isInstalled) "INSTALLED" else "NOT INSTALLED",
                                                state = if (isSessionActive || game.isInstalled) BadgeState.SUCCESS else BadgeState.NEUTRAL
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "${game.region} • ${game.activePackageName}",
                                            color = RedCoreTextSecondary,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Feature Pills
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Surface(
                                                shape = CutCornerShape(2.dp),
                                                color = RedCoreBgSecondary
                                            ) {
                                                Text(
                                                    text = "120Hz SUPPORTED",
                                                    color = BrightActiveRed,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }

                                            Surface(
                                                shape = CutCornerShape(2.dp),
                                                color = RedCoreBgSecondary
                                            ) {
                                                Text(
                                                    text = "GAME MODE",
                                                    color = RedCoreTextSecondary,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Quick Action Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Button 1: BOOST & PLAY
                                    Button(
                                        onClick = { onBoostAndPlay(game.id) },
                                        enabled = game.isInstalled && !isBoosting,
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(38.dp),
                                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryGamingRed,
                                            contentColor = Color.White,
                                            disabledContainerColor = RedCoreSurfaceElevated,
                                            disabledContentColor = RedCoreTextMuted
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "BOOST & PLAY",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    // Button 2: TUNE / DETAIL
                                    OutlinedButton(
                                        onClick = { onGameDetail(game.id) },
                                        modifier = Modifier
                                            .weight(0.9f)
                                            .height(38.dp),
                                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                        border = BorderStroke(1.dp, RedCoreBorder),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = RedCoreTextPrimary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "TUNE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Button 3: PLAY DIRECT
                                    if (game.isInstalled) {
                                        OutlinedButton(
                                            onClick = { onPlayOnly(game.id) },
                                            modifier = Modifier
                                                .weight(0.8f)
                                                .height(38.dp),
                                            shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                            border = BorderStroke(1.dp, RedCoreBorder),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = RedCoreTextSecondary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "PLAY",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
