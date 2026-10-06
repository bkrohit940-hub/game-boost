package com.gameboost.optimizer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.gameboost.optimizer.ui.MainViewModel
import com.gameboost.optimizer.ui.dashboard.DashboardScreen
import com.gameboost.optimizer.ui.diagnostics.DiagnosticsScreen
import com.gameboost.optimizer.ui.firstlaunch.FirstLaunchScreen
import com.gameboost.optimizer.ui.games.GameDetailScreen
import com.gameboost.optimizer.ui.permission.ShizukuSetupScreen
import com.gameboost.optimizer.ui.settings.SettingsScreen

@Composable
fun MainNavigation(
    viewModel: MainViewModel = viewModel()
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val shizukuStatus by viewModel.shizukuStatus.collectAsState()
    val isOptimized by viewModel.isOptimized.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()
    val lastRestorationResult by viewModel.lastRestorationResult.collectAsState()
    val games by viewModel.games.collectAsState()
    val hardwareStats by viewModel.hardwareStats.collectAsState()


    // Determine initial destination: if first run not completed -> FirstLaunchKey; else DashboardKey
    val initialKey = if (!userPrefs.isFirstRunCompleted) {
        FirstLaunchKey
    } else {
        DashboardKey
    }

    val backStack = rememberNavBackStack(initialKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<FirstLaunchKey> {
                FirstLaunchScreen(
                    capabilities = viewModel.capabilities,
                    shizukuStatus = shizukuStatus,
                    onContinueToShizuku = {
                        backStack.add(ShizukuSetupKey)
                    },
                    onContinueToDashboard = {
                        viewModel.completeFirstRun()
                        backStack.clear()
                        backStack.add(DashboardKey)
                    },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            entry<ShizukuSetupKey> {
                ShizukuSetupScreen(
                    status = shizukuStatus,
                    onRequestPermission = { viewModel.requestShizukuPermission() },
                    onRefreshStatus = { viewModel.refreshShizuku() },
                    onBack = { backStack.removeLastOrNull() },
                    onContinue = {
                        viewModel.completeFirstRun()
                        backStack.clear()
                        backStack.add(DashboardKey)
                    },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            entry<DashboardKey> {
                DashboardScreen(
                    shizukuStatus = shizukuStatus,
                    isOptimized = isOptimized,
                    activeGameProfile = activeGame,
                    lastResult = lastResult,
                    lastRestorationResult = lastRestorationResult,
                    games = games,

                    hardwareStats = hardwareStats,
                    userPreferences = userPrefs,
                    onQuickBoost = { viewModel.quickBoost120Hz() },
                    onRestoreDefault = { viewModel.restoreDefaults() },
                    onGameClick = { gameId ->
                        backStack.add(GameDetailKey(gameId))
                    },
                    onOpenSettings = {
                        backStack.add(SettingsKey)
                    },
                    onOpenDiagnostics = {
                        backStack.add(DiagnosticsKey)
                    },
                    onFixShizuku = {
                        backStack.add(ShizukuSetupKey)
                    },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            entry<GameDetailKey> { key ->
                val game = games.firstOrNull { it.id == key.gameId }
                    ?: games.firstOrNull()
                    ?: return@entry

                GameDetailScreen(
                    game = game,
                    displayState = viewModel.capabilities.displayState,
                    isOptimized = isOptimized && activeGame?.id == game.id,
                    lastResult = lastResult,
                    onApply = { profile ->
                        viewModel.applyGameOptimization(game.id, profile)
                    },
                    onRestore = {
                        viewModel.restoreDefaults()
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            entry<SettingsKey> {
                SettingsScreen(
                    userPreferences = userPrefs,
                    shizukuStatus = shizukuStatus,
                    onToggleAutoBoost = { viewModel.toggleAutoBoost(it) },
                    onToggleRestoreOnExit = { viewModel.toggleRestoreOnExit(it) },
                    onRecheckShizuku = { viewModel.refreshShizuku() },
                    onOpenShizukuSetup = { backStack.add(ShizukuSetupKey) },
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            entry<DiagnosticsKey> {
                DiagnosticsScreen(
                    capabilities = viewModel.capabilities,
                    shizukuStatus = shizukuStatus,
                    lastResult = lastResult,
                    activeBackup = viewModel.getActiveBackup(),
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        }
    )
}
