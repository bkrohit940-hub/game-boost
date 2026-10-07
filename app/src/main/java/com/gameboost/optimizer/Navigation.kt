package com.gameboost.optimizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.gameboost.optimizer.theme.DarkBg
import com.gameboost.optimizer.ui.MainViewModel
import com.gameboost.optimizer.ui.boost.BoostScreen
import com.gameboost.optimizer.ui.components.GameBoostNavBar
import com.gameboost.optimizer.ui.dashboard.DashboardScreen
import com.gameboost.optimizer.ui.diagnostics.DiagnosticsScreen
import com.gameboost.optimizer.ui.firstlaunch.FirstLaunchScreen
import com.gameboost.optimizer.ui.games.GameDetailScreen
import com.gameboost.optimizer.ui.games.GamesScreen
import com.gameboost.optimizer.ui.permission.ShizukuSetupScreen
import com.gameboost.optimizer.ui.settings.SettingsScreen
import com.gameboost.optimizer.ui.startup.StartupScreen

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
    val selectedGameId by viewModel.selectedGameId.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val isBoosting by viewModel.isBoosting.collectAsState()
    val privilegedState by (viewModel.privilegedState ?: kotlinx.coroutines.flow.MutableStateFlow(com.gameboost.optimizer.system.PrivilegedSystemState())).collectAsState()
    val wirelessAdbState by (viewModel.wirelessAdbState ?: kotlinx.coroutines.flow.MutableStateFlow(com.gameboost.optimizer.system.adb.WirelessAdbState())).collectAsState()
    val testResult by viewModel.testResult.collectAsState()
    val isTestingBackend by viewModel.isTestingBackend.collectAsState()
    val adbOperationStatus by viewModel.adbOperationStatus.collectAsState()

    // Clean, fast startup branding: starts with StartupKey
    val backStack = rememberNavBackStack(StartupKey)
    val currentKey = backStack.lastOrNull() ?: HomeKey

    val isTopLevelDestination = currentKey is HomeKey ||
            currentKey is DashboardKey ||
            currentKey is GamesKey ||
            currentKey is BoostKey ||
            currentKey is DiagnosticsKey ||
            currentKey is SettingsKey

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            if (isTopLevelDestination) {
                GameBoostNavBar(
                    currentKey = currentKey,
                    onNavigate = { targetKey ->
                        if (currentKey != targetKey) {
                            backStack.clear()
                            backStack.add(HomeKey)
                            if (targetKey != HomeKey) {
                                backStack.add(targetKey)
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .safeDrawingPadding()
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<StartupKey> {
                        StartupScreen(
                            onFinished = {
                                backStack.clear()
                                if (!userPrefs.isFirstRunCompleted) {
                                    backStack.add(FirstLaunchKey)
                                } else {
                                    backStack.add(HomeKey)
                                }
                            }
                        )
                    }

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
                                backStack.add(HomeKey)
                            }
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
                                backStack.add(HomeKey)
                            }
                        )
                    }

                    entry<HomeKey> {
                        DashboardScreen(
                            shizukuStatus = shizukuStatus,
                            isOptimized = isOptimized,
                            activeGameProfile = activeGame,
                            lastResult = lastResult,
                            lastRestorationResult = lastRestorationResult,
                            games = games,
                            hardwareStats = hardwareStats,
                            userPreferences = userPrefs,
                            selectedMode = selectedMode,
                            selectedGameId = selectedGameId,
                            isBoosting = isBoosting,
                            onSelectGame = { viewModel.selectGame(it) },
                            onSelectMode = { viewModel.setPerformanceMode(it) },
                            onBoostAndPlay = { id, thermal -> viewModel.boostAndPlay(id, thermal) },
                            onBoostOnly = { id, thermal -> viewModel.boostOnly(id, thermal) },
                            onPlayOnly = { id -> viewModel.launchGame(id) },
                            onRestoreDefault = { viewModel.restoreDefaults() },
                            onGameClick = { gameId ->
                                backStack.add(GameDetailKey(gameId))
                            },
                            onOpenSettings = {
                                backStack.clear()
                                backStack.add(SettingsKey)
                            },
                            onOpenDiagnostics = {
                                backStack.clear()
                                backStack.add(DiagnosticsKey)
                            },
                            onFixShizuku = {
                                backStack.add(ShizukuSetupKey)
                            }
                        )
                    }

                    entry<DashboardKey> {
                        // Compatibility redirect to Home
                        DashboardScreen(
                            shizukuStatus = shizukuStatus,
                            isOptimized = isOptimized,
                            activeGameProfile = activeGame,
                            lastResult = lastResult,
                            lastRestorationResult = lastRestorationResult,
                            games = games,
                            hardwareStats = hardwareStats,
                            userPreferences = userPrefs,
                            selectedMode = selectedMode,
                            selectedGameId = selectedGameId,
                            isBoosting = isBoosting,
                            onSelectGame = { viewModel.selectGame(it) },
                            onSelectMode = { viewModel.setPerformanceMode(it) },
                            onBoostAndPlay = { id, thermal -> viewModel.boostAndPlay(id, thermal) },
                            onBoostOnly = { id, thermal -> viewModel.boostOnly(id, thermal) },
                            onPlayOnly = { id -> viewModel.launchGame(id) },
                            onRestoreDefault = { viewModel.restoreDefaults() },
                            onGameClick = { gameId ->
                                backStack.add(GameDetailKey(gameId))
                            },
                            onOpenSettings = {
                                backStack.clear()
                                backStack.add(SettingsKey)
                            },
                            onOpenDiagnostics = {
                                backStack.clear()
                                backStack.add(DiagnosticsKey)
                            },
                            onFixShizuku = {
                                backStack.add(ShizukuSetupKey)
                            }
                        )
                    }

                    entry<GamesKey> {
                        GamesScreen(
                            games = games,
                            isOptimized = isOptimized,
                            activeGameProfile = activeGame,
                            isBoosting = isBoosting,
                            onBoostAndPlay = { viewModel.boostAndPlay(it, false) },
                            onBoostOnly = { viewModel.boostOnly(it, false) },
                            onPlayOnly = { viewModel.launchGame(it) },
                            onGameDetail = { gameId ->
                                backStack.add(GameDetailKey(gameId))
                            }
                        )
                    }

                    entry<BoostKey> {
                        BoostScreen(
                            capabilities = viewModel.capabilities,
                            shizukuStatus = shizukuStatus,
                            isOptimized = isOptimized,
                            activeGameProfile = activeGame,
                            lastResult = lastResult,
                            hardwareStats = hardwareStats,
                            selectedMode = selectedMode,
                            games = games,
                            selectedGameId = selectedGameId,
                            onSelectMode = { viewModel.setPerformanceMode(it) },
                            onApplyOptimization = { id, thermal -> viewModel.boostOnly(id, thermal) },
                            onRestoreDefaults = { viewModel.restoreDefaults() }
                        )
                    }

                    entry<DiagnosticsKey> {
                        DiagnosticsScreen(
                            capabilities = viewModel.capabilities,
                            shizukuStatus = shizukuStatus,
                            lastResult = lastResult,
                            activeBackup = viewModel.getActiveBackup(),
                            privilegedState = privilegedState,
                            testResult = testResult,
                            isTestingBackend = isTestingBackend,
                            onTestBackend = { viewModel.testBackend() },
                            onClearTestResult = { viewModel.clearTestResult() },
                            onBack = {
                                backStack.clear()
                                backStack.add(HomeKey)
                            }
                        )
                    }

                    entry<SettingsKey> {
                        SettingsScreen(
                            userPreferences = userPrefs,
                            shizukuStatus = shizukuStatus,
                            wirelessAdbState = wirelessAdbState,
                            adbOperationStatus = adbOperationStatus,
                            onPairWirelessAdb = { code, port -> viewModel.pairWirelessAdb(code, port) },
                            onConnectWirelessAdb = { port -> viewModel.connectWirelessAdb(port) },
                            onDisconnectWirelessAdb = { viewModel.disconnectWirelessAdb() },
                            onToggleAutoBoost = { viewModel.toggleAutoBoost(it) },
                            onToggleRestoreOnExit = { viewModel.toggleRestoreOnExit(it) },
                            onRecheckShizuku = { viewModel.refreshShizuku() },
                            onOpenShizukuSetup = { backStack.add(ShizukuSetupKey) },
                            onRestoreDefault = { viewModel.restoreDefaults() },
                            onBack = {
                                backStack.clear()
                                backStack.add(HomeKey)
                            }
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
                            onLaunch = if (game.isInstalled) {
                                { viewModel.launchGame(game.id) }
                            } else null,
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
            )
        }
    }
}
