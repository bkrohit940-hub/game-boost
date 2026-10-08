package com.gameboost.optimizer.ui.games

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.theme.AccentPrimary
import com.gameboost.optimizer.theme.BorderActive
import com.gameboost.optimizer.theme.BorderSubtle
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.theme.StatusDanger
import com.gameboost.optimizer.theme.StatusReady
import com.gameboost.optimizer.theme.StatusReadyContainer
import com.gameboost.optimizer.theme.StatusWarning
import com.gameboost.optimizer.theme.SurfaceCard
import com.gameboost.optimizer.theme.SurfaceElevated
import com.gameboost.optimizer.theme.TextPrimary
import com.gameboost.optimizer.theme.TextSecondary
import com.gameboost.optimizer.theme.TextTertiary
import com.gameboost.optimizer.ui.components.GameIconView

private enum class GameFilterTab {
    ALL, INSTALLED, CUSTOM_PROFILE
}

/**
 * GameBoost Games Screen — Faithfully recreating the Google Stitch design reference
 * for the Games screen (Stitch Project 14872102965839926777 / screen 25b1714a93f1406190d5addc2701e482).
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
                GameFilterTab.CUSTOM_PROFILE -> game.supportedFeatures.isNotEmpty()
            }

            matchesSearch && matchesFilter
        }
    }

    val installedCount = games.count { it.isInstalled }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBg
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrowScreen = maxWidth < 360.dp
            val horizontalPadding = if (isNarrowScreen) 12.dp else 16.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ==========================================
                // 1. SCREEN TITLE & SYNC PACKAGES ACTION
                // ==========================================
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(AccentPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GAMES",
                                    color = TextPrimary,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF072B38),
                                    border = BorderStroke(1.dp, Color(0xFF00E5FF))
                                ) {
                                    Text(
                                        text = "V2.4.MIUI",
                                        color = AccentPrimary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your mission-ready gaming library",
                                color = TextTertiary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // SYNC PACKAGES Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.clickable {
                                // Refresh query
                                searchQuery = ""
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sync",
                                    tint = AccentPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SYNC PACKAGES",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 2. SEARCH & FILTER BAR
                // ==========================================
                item {
                    // Search Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0E131A),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "FILTER PACKAGE OR TITLE...",
                                        color = TextTertiary,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    cursorBrush = SolidColor(AccentPrimary),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF161F2C)
                            ) {
                                Text(
                                    text = "${filteredGames.size} LOADED",
                                    color = TextSecondary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // ALL chip
                        item {
                            StitchFilterChip(
                                label = "ALL (${games.size})",
                                isSelected = selectedFilter == GameFilterTab.ALL,
                                onClick = { selectedFilter = GameFilterTab.ALL }
                            )
                        }

                        // INSTALLED chip
                        item {
                            StitchFilterChip(
                                label = "INSTALLED ($installedCount)",
                                isSelected = selectedFilter == GameFilterTab.INSTALLED,
                                onClick = { selectedFilter = GameFilterTab.INSTALLED }
                            )
                        }

                        // CUSTOM PROFILE chip
                        item {
                            StitchFilterChip(
                                label = "CUSTOM PROFILE (${games.size})",
                                isSelected = selectedFilter == GameFilterTab.CUSTOM_PROFILE,
                                onClick = { selectedFilter = GameFilterTab.CUSTOM_PROFILE }
                            )
                        }
                    }
                }

                // ==========================================
                // 3. GAME CARDS LIST
                // ==========================================
                items(filteredGames, key = { it.id }) { game ->
                    val isActive = isOptimized && activeGameProfile?.id == game.id

                    StitchGameListItem(
                        game = game,
                        isActive = isActive,
                        isBoosting = isBoosting,
                        onBoostAndPlay = { onBoostAndPlay(game.id) },
                        onPlayOnly = { onPlayOnly(game.id) },
                        onDetail = { onGameDetail(game.id) }
                    )
                }

                // Empty state if search has no results
                if (filteredGames.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SurfaceCard,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "NO MATCHING GAMES FOUND",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try clearing search filter",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 4. AUTO-DETECTION SYSTEM BANNER
                // ==========================================
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0E131A),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF161F2C))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⍈ ", color = AccentPrimary, fontSize = 12.sp)
                                Column {
                                    Text(
                                        text = "AUTO-DETECTION ENABLED",
                                        color = TextPrimary,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Hooked via Android PackageManager API",
                                        color = TextTertiary,
                                        fontSize = 8.5.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF16202C)
                            ) {
                                Text(
                                    text = "READY",
                                    color = AccentPrimary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 5. TACTICAL FOOTER
                // ==========================================
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "● TACTICAL ENGINE CORE // REF 4.09 ●",
                            color = TextTertiary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Created by Rohit B.K",
                            color = Color(0xFF434E60),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Filter Chip matching Stitch styling
 */
@Composable
private fun StitchFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AccentPrimary else Color(0xFF111720),
        border = BorderStroke(
            1.dp,
            if (isSelected) AccentPrimary else BorderSubtle
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Black else AccentPrimary)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = if (isSelected) Color.Black else TextSecondary,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

/**
 * Game List Item Card matching Stitch visual design
 */
@Composable
private fun StitchGameListItem(
    game: GameProfile,
    isActive: Boolean,
    isBoosting: Boolean,
    onBoostAndPlay: () -> Unit,
    onPlayOnly: () -> Unit,
    onDetail: () -> Unit
) {
    val isInstalled = game.isInstalled

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (isActive) 1.5.dp else 1.dp,
            if (isActive) BorderActive else BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Section: Icon, Titles, Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Thumbnail with bottom-left Hz overlay
                Box(modifier = Modifier.size(52.dp)) {
                    GameIconView(
                        packageName = game.activePackageName,
                        isInstalled = isInstalled,
                        sizeDp = 52.dp
                    )
                    Surface(
                        modifier = Modifier.align(Alignment.BottomStart),
                        shape = RoundedCornerShape(topEnd = 6.dp, bottomStart = 10.dp),
                        color = Color(0xFF0F1A26),
                        border = BorderStroke(1.dp, Color(0xFF1E2E40))
                    ) {
                        Text(
                            text = if (game.id.contains("global") || game.id.contains("bgmi")) "120Hz" else "90Hz",
                            color = AccentPrimary,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Title and Active/Ready Status Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.displayName.uppercase(),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Top right status badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isActive) Color(0xFF072B38) else Color(0xFF131A24),
                            border = BorderStroke(1.dp, if (isActive) AccentPrimary else BorderSubtle)
                        ) {
                            Text(
                                text = if (isActive) "● ACTIVE" else if (isInstalled) "READY" else "STANDBY",
                                color = if (isActive) AccentPrimary else TextTertiary,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = game.activePackageName,
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // Badges row: INSTALLED + PROFILE
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = if (isInstalled) StatusReadyContainer else Color(0xFF1A1F26)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isInstalled) StatusReady else TextTertiary)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isInstalled) "INSTALLED" else "UNINSTALLED",
                                    color = if (isInstalled) StatusReady else TextTertiary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = if (game.id.contains("kr")) Color(0xFF2E0F14) else Color(0xFF092E3B)
                        ) {
                            Text(
                                text = if (game.id.contains("kr")) "EXTREME PROFILE" else "PERFORMANCE PROFILE",
                                color = if (game.id.contains("kr")) StatusDanger else AccentPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spec / Capabilities Strip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0D1219),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFF161F2C))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = AccentPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Refresh Lock: 120Hz",
                            color = TextSecondary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(text = "•", color = Color(0xFF222D3D), fontSize = 9.sp)

                    Text(
                        text = "Priority: High",
                        color = AccentPrimary,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(text = "•", color = Color(0xFF222D3D), fontSize = 9.sp)

                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color(0xFF131D28)
                    ) {
                        Text(
                            text = "CPU MAX",
                            color = AccentPrimary,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: SELECT / CONFIGURE + LAUNCH / BOOST
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left Button: SELECT / DETAILS
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable(onClick = onDetail),
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, if (isActive) AccentPrimary else BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isActive) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AccentPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SELECTED",
                                color = AccentPrimary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        } else {
                            Text(
                                text = "○ SELECT",
                                color = TextPrimary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Right Button: LAUNCH / BOOST & PLAY
                Button(
                    onClick = {
                        if (isInstalled) {
                            onBoostAndPlay()
                        } else {
                            onPlayOnly()
                        }
                    },
                    enabled = isInstalled && !isBoosting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) AccentPrimary else Color(0xFF00E5FF),
                        contentColor = Color(0xFF040A0F),
                        disabledContainerColor = Color(0xFF161E28),
                        disabledContentColor = TextTertiary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LAUNCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
