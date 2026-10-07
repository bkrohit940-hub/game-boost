package com.gameboost.optimizer.domain.optimizer

import android.util.Log
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.OptimizationSession
import com.gameboost.optimizer.models.RestorationResult
import com.gameboost.optimizer.models.SessionStatus
import com.gameboost.optimizer.system.DisplayController
import com.gameboost.optimizer.system.PerformanceController
import com.gameboost.optimizer.system.PrivilegedExecutionEngine
import com.gameboost.optimizer.system.ShizukuManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orchestrates gaming optimization sessions, verification, backup, and restoration.
 * Implements the lifecycle specification:
 * GAME START -> CREATE SESSION -> CAPTURE BASELINE -> APPLY -> VERIFY -> ACTIVE -> EXIT -> RESTORE
 */
class OptimizationEngine(
    private val shizukuManager: ShizukuManager,
    private val displayController: DisplayController,
    private val performanceController: PerformanceController,
    private val privilegedEngine: PrivilegedExecutionEngine? = null
) {
    companion object {
        private const val TAG = "OptimizationEngine"
    }

    private val _isOptimized = MutableStateFlow(false)
    val isOptimized: StateFlow<Boolean> = _isOptimized.asStateFlow()

    private val _activeGameProfile = MutableStateFlow<GameProfile?>(null)
    val activeGameProfile: StateFlow<GameProfile?> = _activeGameProfile.asStateFlow()

    private val _lastResult = MutableStateFlow<OptimizationResult?>(null)
    val lastResult: StateFlow<OptimizationResult?> = _lastResult.asStateFlow()

    private val _currentSession = MutableStateFlow<OptimizationSession?>(null)
    val currentSession: StateFlow<OptimizationSession?> = _currentSession.asStateFlow()

    private val _lastRestorationResult = MutableStateFlow<RestorationResult?>(null)
    val lastRestorationResult: StateFlow<RestorationResult?> = _lastRestorationResult.asStateFlow()

    private var activeBackup: DisplayStateBackup? = null

    suspend fun applyOptimization(
        gameProfile: GameProfile,
        profile: OptimizationProfile
    ): OptimizationResult {
        Log.i(TAG, "Starting optimization session for ${gameProfile.displayName} with target ${profile.targetRefreshRate}Hz")

        val hasPrivilege = privilegedEngine?.isReady == true || shizukuManager.hasPermission()
        if (!hasPrivilege) {
            val res = OptimizationResult(
                isSuccess = false,
                gameName = gameProfile.displayName,
                requestedRefreshRate = profile.targetRefreshRate,
                actualRefreshRate = displayController.getDisplayState().currentRefreshRate,
                statusMessage = "Privileged authorization required",
                technicalExplanation = "GameBoost needs user-authorized Shizuku or Wireless Debugging to adjust display and system configuration."
            )
            _lastResult.value = res
            return res
        }

        // 1. CAPTURE BASELINE
        val baseline = activeBackup ?: displayController.createBackup().also {
            activeBackup = it
            Log.d(TAG, "Captured display and system baseline backup: $it")
        }

        // 2. CREATE SESSION
        var session = OptimizationSession(
            gamePackage = gameProfile.activePackageName,
            gameName = gameProfile.displayName,
            selectedProfile = profile,
            requestedRefreshRate = profile.targetRefreshRate,
            originalSettings = baseline,
            sessionStatus = SessionStatus.BASELINE_CAPTURED
        )
        _currentSession.value = session

        val applied = mutableListOf<String>()
        val failed = mutableListOf<String>()

        val effectiveTargetRate = if (profile.targetRefreshRate <= 0f) {
            displayController.getHighestSupportedRefreshRate()
        } else {
            profile.targetRefreshRate
        }

        // 3. APPLY REFRESH RATE
        val (displaySuccess, displayMsg) = displayController.applyRefreshRate(effectiveTargetRate)
        if (displaySuccess) {
            applied.add(displayMsg)
        } else {
            failed.add(displayMsg)
        }

        // 4. APPLY PERFORMANCE SETTINGS
        val (perfApplied, perfFailed) = performanceController.applyPerformanceProfile(
            profile = profile,
            targetPackageName = gameProfile.installedPackageName
        )
        applied.addAll(perfApplied)
        failed.addAll(perfFailed)

        // 5. VERIFY
        val finalDisplayState = displayController.getDisplayState()
        val verified = displaySuccess && Math.abs(finalDisplayState.currentRefreshRate - effectiveTargetRate) < 1.5f

        val success = (displaySuccess || perfApplied.isNotEmpty()) && failed.isEmpty()
        val partialSuccess = displaySuccess || perfApplied.isNotEmpty()
        val techExplanation = if (!verified && !displaySuccess) {
            "Display control restricted by device/OEM"
        } else null

        val result = OptimizationResult(
            isSuccess = partialSuccess,
            gameName = gameProfile.displayName,
            requestedRefreshRate = effectiveTargetRate,
            actualRefreshRate = finalDisplayState.currentRefreshRate,
            appliedSettings = applied,
            failedSettings = failed,
            statusMessage = if (verified && failed.isEmpty()) "Optimization active" else if (partialSuccess) "Applied with limitations" else "Optimization failed",
            technicalExplanation = techExplanation,
            verified = verified
        )

        // 6. ACTIVE
        session = session.copy(
            modifiedSettings = applied,
            verificationResult = result,
            sessionStatus = if (success) SessionStatus.ACTIVE else SessionStatus.FAILED
        )
        _currentSession.value = session
        _isOptimized.value = success
        _activeGameProfile.value = gameProfile.copy(optimizationActive = success, lastOptimizedTime = System.currentTimeMillis())
        _lastResult.value = result

        return result
    }

    suspend fun restoreDefaults(): RestorationResult {
        Log.i(TAG, "Restoring previous system configuration")

        val backup = activeBackup
        val activePkg = _activeGameProfile.value?.installedPackageName

        val restoredItems = mutableListOf<String>()
        val failedItems = mutableListOf<String>()
        var failureReason: String? = null

        val (displayRestored, displayMsg) = displayController.restoreDefault(backup)
        if (displayRestored) {
            restoredItems.add("Refresh rate restored")
        } else {
            failedItems.add("Refresh rate could not be restored")
            failureReason = displayMsg
        }

        val (perfRestored, perfFailed) = performanceController.restorePerformanceSettings(backup, activePkg)
        restoredItems.addAll(perfRestored)
        failedItems.addAll(perfFailed)
        if (perfFailed.isNotEmpty() && failureReason == null) {
            failureReason = "System rejected the requested value."
        }

        val isComplete = failedItems.isEmpty() && restoredItems.isNotEmpty()

        val restorationResult = RestorationResult(
            isCompleteSuccess = isComplete,
            restoredItems = restoredItems,
            failedItems = failedItems,
            failureReason = failureReason
        )

        _currentSession.value = _currentSession.value?.copy(
            sessionStatus = SessionStatus.REVERTED,
            restorationResult = restorationResult
        )

        activeBackup = null
        _isOptimized.value = false
        _activeGameProfile.value = null
        _lastRestorationResult.value = restorationResult

        return restorationResult
    }

    fun getActiveBackup(): DisplayStateBackup? = activeBackup
}
