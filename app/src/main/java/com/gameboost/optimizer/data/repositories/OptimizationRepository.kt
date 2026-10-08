package com.gameboost.optimizer.data.repositories

import com.gameboost.optimizer.data.datastore.AppUserPreferences
import com.gameboost.optimizer.data.datastore.UserPreferencesRepository
import com.gameboost.optimizer.data.gameprofiles.GameRegistry
import com.gameboost.optimizer.domain.optimizer.OptimizationEngine
import com.gameboost.optimizer.models.DeviceCapabilities
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.HardwareStats
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.DeviceCapabilityDetector
import com.gameboost.optimizer.system.HardwareMonitor
import com.gameboost.optimizer.system.PackageDetector
import com.gameboost.optimizer.system.ShizukuManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class OptimizationRepository(
    private val shizukuManager: ShizukuManager,
    private val packageDetector: PackageDetector,
    private val deviceCapabilityDetector: DeviceCapabilityDetector,
    private val hardwareMonitor: HardwareMonitor,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val optimizationEngine: OptimizationEngine,
    val privilegedEngine: com.gameboost.optimizer.system.PrivilegedExecutionEngine? = null,
    val adbConnectionManager: com.gameboost.optimizer.system.adb.AdbConnectionManager? = null,
    val displayController: com.gameboost.optimizer.system.DisplayController? = null
) {
    val shizukuStatus: StateFlow<ShizukuStatus> = shizukuManager.statusFlow
    val wirelessAdbState: StateFlow<com.gameboost.optimizer.system.adb.WirelessAdbState>? = adbConnectionManager?.stateFlow
    val privilegedState: StateFlow<com.gameboost.optimizer.system.PrivilegedSystemState>? = privilegedEngine?.systemState
    val isOptimized: StateFlow<Boolean> = optimizationEngine.isOptimized
    val activeGameProfile: StateFlow<GameProfile?> = optimizationEngine.activeGameProfile
    val lastResult: StateFlow<OptimizationResult?> = optimizationEngine.lastResult
    val userPreferences: Flow<AppUserPreferences> = userPreferencesRepository.userPreferencesFlow

    suspend fun getDisplayDiagnostics(targetPackageName: String? = null): com.gameboost.optimizer.models.DisplayDiagnosticData? {
        return displayController?.getDisplayDiagnostics(targetPackageName)
    }

    fun getDeviceCapabilities(): DeviceCapabilities {
        return deviceCapabilityDetector.detectCapabilities()
    }

    fun getDetectedGames(): List<GameProfile> {
        val baseGames = GameRegistry.getDefaultSupportedGames()
        return baseGames.map { game ->
            val installedPkg = game.packageNames.firstOrNull { packageDetector.isPackageInstalled(it) }
            val isInstalled = installedPkg != null
            val version = if (installedPkg != null) packageDetector.getPackageVersion(installedPkg) else null

            game.copy(
                isInstalled = isInstalled,
                installedPackageName = installedPkg,
                appVersion = version,
                optimizationActive = optimizationEngine.isOptimized.value &&
                        optimizationEngine.activeGameProfile.value?.id == game.id
            )
        }
    }

    fun getHardwareStats(): HardwareStats {
        return hardwareMonitor.getHardwareStats()
    }

    fun monitorHardware(): Flow<HardwareStats> {
        return hardwareMonitor.monitorFlow()
    }

    fun getPackageIcon(packageName: String): android.graphics.drawable.Drawable? {
        return packageDetector.getApplicationIcon(packageName)
    }

    fun launchGame(packageName: String): Boolean {
        return packageDetector.launchPackage(packageName)
    }

    suspend fun applyOptimization(gameProfile: GameProfile, profile: OptimizationProfile): OptimizationResult {
        return optimizationEngine.applyOptimization(gameProfile, profile)
    }

    suspend fun boostAndPlay(gameProfile: GameProfile, profile: OptimizationProfile): Pair<OptimizationResult, Boolean> {
        val result = applyOptimization(gameProfile, profile)
        val launched = if (gameProfile.isInstalled && gameProfile.installedPackageName != null) {
            launchGame(gameProfile.installedPackageName)
        } else false
        return Pair(result, launched)
    }

    val currentSession: StateFlow<com.gameboost.optimizer.models.OptimizationSession?> = optimizationEngine.currentSession
    val lastRestorationResult: StateFlow<com.gameboost.optimizer.models.RestorationResult?> = optimizationEngine.lastRestorationResult

    suspend fun restoreDefaults(): com.gameboost.optimizer.models.RestorationResult {
        return optimizationEngine.restoreDefaults()
    }


    fun requestShizukuPermission() {
        shizukuManager.requestPermission()
    }

    fun refreshShizukuStatus() {
        shizukuManager.refreshStatus()
    }

    suspend fun setFirstRunCompleted(completed: Boolean) {
        userPreferencesRepository.setFirstRunCompleted(completed)
    }

    suspend fun setTargetRefreshRate(rate: Float) {
        userPreferencesRepository.setTargetRefreshRate(rate)
    }

    suspend fun setAutoBoost(enabled: Boolean) {
        userPreferencesRepository.setAutoBoost(enabled)
    }

    suspend fun setRestoreOnExit(enabled: Boolean) {
        userPreferencesRepository.setRestoreOnExit(enabled)
    }

    suspend fun setSessionMonitoring(enabled: Boolean) {
        userPreferencesRepository.setSessionMonitoring(enabled)
    }

    suspend fun setNotifications(enabled: Boolean) {
        userPreferencesRepository.setNotifications(enabled)
    }

    suspend fun setShowStartupScreen(enabled: Boolean) {
        userPreferencesRepository.setShowStartupScreen(enabled)
    }

    suspend fun setShizukuEverAuthorized(auth: Boolean) {
        userPreferencesRepository.setShizukuEverAuthorized(auth)
    }

    suspend fun pairWirelessAdb(pairingCode: String, port: Int): Result<Boolean> {
        return adbConnectionManager?.pair(pairingCode, port)
            ?: Result.failure(IllegalStateException("Wireless ADB not available"))
    }

    suspend fun connectWirelessAdb(port: Int): Result<Boolean> {
        return adbConnectionManager?.connect(port)
            ?: Result.failure(IllegalStateException("Wireless ADB not available"))
    }

    fun disconnectWirelessAdb() {
        adbConnectionManager?.disconnect()
    }

    suspend fun testPrivilegedBackend(): com.gameboost.optimizer.models.ShellCommandResult {
        return privilegedEngine?.testActiveBackend() ?: shizukuManager.testConnection()
    }

    fun openShizukuApp(): Boolean {
        return shizukuManager.openShizukuApp()
    }
}
