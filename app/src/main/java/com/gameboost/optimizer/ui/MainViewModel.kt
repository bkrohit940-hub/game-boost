package com.gameboost.optimizer.ui

import android.graphics.drawable.Drawable
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
import com.gameboost.optimizer.models.OptimizationSession
import com.gameboost.optimizer.models.PerformanceMode
import com.gameboost.optimizer.models.RestorationResult
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
    val currentSession: StateFlow<OptimizationSession?> = repo.currentSession
    val lastRestorationResult: StateFlow<RestorationResult?> = repo.lastRestorationResult

    val userPreferences: StateFlow<AppUserPreferences> = repo.userPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AppUserPreferences()
    )

    private val _games = MutableStateFlow<List<GameProfile>>(emptyList())
    val games: StateFlow<List<GameProfile>> = _games.asStateFlow()

    private val _selectedGameId = MutableStateFlow<String?>("pubg_global")
    val selectedGameId: StateFlow<String?> = _selectedGameId.asStateFlow()

    private val _selectedMode = MutableStateFlow(PerformanceMode.PERFORMANCE)
    val selectedMode: StateFlow<PerformanceMode> = _selectedMode.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private val _hardwareStats = MutableStateFlow(repo.getHardwareStats())
    val hardwareStats: StateFlow<HardwareStats> = _hardwareStats.asStateFlow()

    val capabilities: DeviceCapabilities by lazy {
        repo.getDeviceCapabilities()
    }

    init {
        refreshGames()

        // Sync with persisted preferences
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                _selectedMode.value = prefs.selectedProfileType
                if (prefs.wasShizukuEverAuthorized) {
                    app.shizukuManager.setPreviouslyAuthorized(true)
                }
            }
        }

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

    fun selectGame(gameId: String) {
        _selectedGameId.value = gameId
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        _selectedMode.value = mode
        viewModelScope.launch {
            repo.setFirstRunCompleted(true)
            app.userPreferencesRepository.setProfileType(mode)
        }
    }

    fun refreshGames() {
        val detected = repo.getDetectedGames()
        _games.value = detected
        if (_selectedGameId.value == null || detected.none { it.id == _selectedGameId.value }) {
            val installedFirst = detected.firstOrNull { it.isInstalled } ?: detected.firstOrNull()
            _selectedGameId.value = installedFirst?.id
        }
    }

    fun getPackageIcon(packageName: String): Drawable? {
        return repo.getPackageIcon(packageName)
    }

    fun refreshShizuku() {
        repo.refreshShizukuStatus()
    }

    fun requestShizukuPermission() {
        repo.requestShizukuPermission()
    }

    fun boostAndPlay(gameId: String, thermalConfirmed: Boolean = false) {
        viewModelScope.launch {
            _isBoosting.value = true
            val targetGame = _games.value.firstOrNull { it.id == gameId }
                ?: _games.value.firstOrNull { it.isInstalled }
                ?: _games.value.firstOrNull()

            if (targetGame != null) {
                _selectedGameId.value = targetGame.id
                val profile = OptimizationProfile(
                    mode = _selectedMode.value,
                    thermalOverrideConfirmed = thermalConfirmed,
                    targetRefreshRate = 0f // automatically highest supported display mode
                )
                repo.boostAndPlay(targetGame, profile)
                refreshGames()
            }
            _isBoosting.value = false
        }
    }

    fun boostOnly(gameId: String, thermalConfirmed: Boolean = false) {
        viewModelScope.launch {
            _isBoosting.value = true
            val targetGame = _games.value.firstOrNull { it.id == gameId }
            if (targetGame != null) {
                _selectedGameId.value = targetGame.id
                val profile = OptimizationProfile(
                    mode = _selectedMode.value,
                    thermalOverrideConfirmed = thermalConfirmed,
                    targetRefreshRate = 0f
                )
                repo.applyOptimization(targetGame, profile)
                refreshGames()
            }
            _isBoosting.value = false
        }
    }

    fun launchGame(gameId: String) {
        val targetGame = _games.value.firstOrNull { it.id == gameId }
        val pkg = targetGame?.installedPackageName
        if (pkg != null) {
            repo.launchGame(pkg)
        }
    }

    fun quickBoost120Hz() {
        viewModelScope.launch {
            val gamesList = _games.value
            val firstInstalled = gamesList.firstOrNull { it.isInstalled } ?: gamesList.firstOrNull()
            if (firstInstalled != null) {
                val profile = OptimizationProfile(
                    mode = _selectedMode.value,
                    targetRefreshRate = 0f
                )
                repo.applyOptimization(firstInstalled, profile)
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
