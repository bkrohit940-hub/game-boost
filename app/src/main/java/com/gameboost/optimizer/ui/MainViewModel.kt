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
    val privilegedState: StateFlow<com.gameboost.optimizer.system.PrivilegedSystemState>? = repo.privilegedState
    val wirelessAdbState: StateFlow<com.gameboost.optimizer.system.adb.WirelessAdbState>? = repo.wirelessAdbState
    val isOptimized: StateFlow<Boolean> = repo.isOptimized
    val activeGameProfile: StateFlow<GameProfile?> = repo.activeGameProfile
    val activeGame: StateFlow<GameProfile?> = repo.activeGameProfile
    val lastResult: StateFlow<OptimizationResult?> = repo.lastResult
    val currentSession: StateFlow<OptimizationSession?> = repo.currentSession
    val lastRestorationResult: StateFlow<RestorationResult?> = repo.lastRestorationResult

    private val _testResult = MutableStateFlow<com.gameboost.optimizer.models.ShellCommandResult?>(null)
    val testResult: StateFlow<com.gameboost.optimizer.models.ShellCommandResult?> = _testResult.asStateFlow()

    private val _isTestingBackend = MutableStateFlow(false)
    val isTestingBackend: StateFlow<Boolean> = _isTestingBackend.asStateFlow()

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

    private val _selectedRefreshRate = MutableStateFlow(0f)
    val selectedRefreshRate: StateFlow<Float> = _selectedRefreshRate.asStateFlow()

    private val _hardwareStats = MutableStateFlow(repo.getHardwareStats())
    val hardwareStats: StateFlow<HardwareStats> = _hardwareStats.asStateFlow()

    private val _displayDiagnostics = MutableStateFlow<com.gameboost.optimizer.models.DisplayDiagnosticData?>(null)
    val displayDiagnostics: StateFlow<com.gameboost.optimizer.models.DisplayDiagnosticData?> = _displayDiagnostics.asStateFlow()

    val capabilities: DeviceCapabilities by lazy {
        repo.getDeviceCapabilities()
    }

    init {
        refreshGames()
        refreshDisplayDiagnostics()

        // Sync with persisted preferences
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                _selectedMode.value = prefs.selectedProfileType
                _selectedRefreshRate.value = prefs.targetRefreshRate
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

    fun setTargetRefreshRate(rate: Float) {
        _selectedRefreshRate.value = rate
        viewModelScope.launch {
            app.userPreferencesRepository.setTargetRefreshRate(rate)
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

    fun refreshDisplayDiagnostics(targetPackageName: String? = null) {
        viewModelScope.launch {
            val pkg = targetPackageName ?: _games.value.firstOrNull { it.id == _selectedGameId.value }?.activePackageName
            _displayDiagnostics.value = repo.getDisplayDiagnostics(pkg)
        }
    }

    fun refreshShizuku() {
        repo.refreshShizukuStatus()
        refreshDisplayDiagnostics()
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
                    targetRefreshRate = _selectedRefreshRate.value
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
                    targetRefreshRate = _selectedRefreshRate.value
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

    fun toggleSessionMonitoring(enabled: Boolean) {
        viewModelScope.launch {
            repo.setSessionMonitoring(enabled)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repo.setNotifications(enabled)
        }
    }

    fun toggleShowStartupScreen(enabled: Boolean) {
        viewModelScope.launch {
            repo.setShowStartupScreen(enabled)
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

    fun testBackend() {
        viewModelScope.launch {
            _isTestingBackend.value = true
            val res = repo.testPrivilegedBackend()
            _testResult.value = res
            _isTestingBackend.value = false
        }
    }

    fun clearTestResult() {
        _testResult.value = null
    }

    fun openShizuku() {
        repo.openShizukuApp()
    }

    private val _adbOperationStatus = MutableStateFlow<String?>(null)
    val adbOperationStatus: StateFlow<String?> = _adbOperationStatus.asStateFlow()

    fun pairWirelessAdb(pairingCode: String, port: Int) {
        viewModelScope.launch {
            _adbOperationStatus.value = "Pairing with 127.0.0.1:$port..."
            val res = repo.pairWirelessAdb(pairingCode, port)
            if (res.isSuccess) {
                _adbOperationStatus.value = "Paired successfully. Enter connect port."
            } else {
                _adbOperationStatus.value = "Pairing failed: ${res.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun connectWirelessAdb(port: Int) {
        viewModelScope.launch {
            _adbOperationStatus.value = "Connecting to 127.0.0.1:$port..."
            val res = repo.connectWirelessAdb(port)
            if (res.isSuccess) {
                _adbOperationStatus.value = "Connected to Wireless ADB"
            } else {
                _adbOperationStatus.value = "Connection failed: ${res.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun disconnectWirelessAdb() {
        repo.disconnectWirelessAdb()
        _adbOperationStatus.value = "Disconnected"
    }
}
