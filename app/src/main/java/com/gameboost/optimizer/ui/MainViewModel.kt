package com.gameboost.optimizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameboost.optimizer.GameBoostApp
import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.ShizukuStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val app = GameBoostApp.instance
    private val repo = app.optimizationRepository

    val shizukuStatus: StateFlow<ShizukuStatus> = repo.shizukuStatus
    val isOptimized: StateFlow<Boolean> = repo.isOptimized
    val activeGame: StateFlow<GameProfile?> = repo.activeGameProfile
    val lastResult: StateFlow<OptimizationResult?> = repo.lastResult
    val currentSession: StateFlow<com.gameboost.optimizer.models.OptimizationSession?> = repo.currentSession
    val lastRestorationResult: StateFlow<com.gameboost.optimizer.models.RestorationResult?> = repo.lastRestorationResult

    val userPreferences: StateFlow<AppUserPreferences> = repo.userPreferences.stateIn(

        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AppUserPreferences()
    )

    private val _games = MutableStateFlow<List<GameProfile>>(emptyList())
    val games: StateFlow<List<GameProfile>> = _games.asStateFlow()

    private val _hardwareStats = MutableStateFlow(repo.getHardwareStats())
    val hardwareStats: StateFlow<HardwareStats> = _hardwareStats.asStateFlow()

    val capabilities: DeviceCapabilities by lazy {
        repo.getDeviceCapabilities()
    }

    init {
        refreshGames()

        // Track Shizuku permission change to record persistent authorization
        viewModelScope.launch {
            shizukuStatus.collect { status ->
                if (status.isAuthorized) {
                    repo.setShizukuEverAuthorized(true)
                }
            }
        }

        // Live hardware monitoring flow
        viewModelScope.launch {
            repo.monitorHardware().collect { stats ->
                _hardwareStats.value = stats
            }
        }
    }

    fun refreshGames() {
        _games.value = repo.getDetectedGames()
    }

    fun refreshShizuku() {
        repo.refreshShizukuStatus()
    }

    fun requestShizukuPermission() {
        repo.requestShizukuPermission()
    }

    fun quickBoost120Hz() {
        viewModelScope.launch {
            val gamesList = _games.value
            val firstInstalled = gamesList.firstOrNull { it.isInstalled } ?: gamesList.firstOrNull()
            if (firstInstalled != null) {
                val targetRate = if (capabilities.displayState.supports120Hz) 120f else capabilities.displayState.maxRefreshRate
                repo.applyOptimization(firstInstalled, OptimizationProfile(targetRefreshRate = targetRate))
                refreshGames()
            }
        }
    }

    fun applyGameOptimization(gameId: String, profile: OptimizationProfile) {
        viewModelScope.launch {
            val targetGame = _games.value.firstOrNull { it.id == gameId }
            if (targetGame != null) {
                repo.applyOptimization(targetGame, profile)
                refreshGames()
            }
        }
    }

    fun restoreDefaults() {
        viewModelScope.launch {
            repo.restoreDefaults()
            refreshGames()
        }
    }

    fun toggleAutoBoost(enabled: Boolean) {
        viewModelScope.launch {
            repo.setAutoBoost(enabled)
        }
    }

    fun toggleRestoreOnExit(enabled: Boolean) {
        viewModelScope.launch {
            repo.setRestoreOnExit(enabled)
        }
    }

    fun completeFirstRun() {
        viewModelScope.launch {
            repo.setFirstRunCompleted(true)
        }
    }

    fun getActiveBackup(): DisplayStateBackup? {
        return app.optimizationEngine.getActiveBackup()
    }
}
