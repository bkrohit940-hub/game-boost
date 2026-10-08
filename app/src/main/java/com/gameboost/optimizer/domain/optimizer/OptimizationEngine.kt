package com.gameboost.optimizer.domain.optimizer

import android.util.Log
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.GameProfile
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationResult
import com.gameboost.optimizer.models.OptimizationSession
import com.gameboost.optimizer.models.OptimizationStepResult
import com.gameboost.optimizer.models.RestorationResult
import com.gameboost.optimizer.models.SessionStatus
import com.gameboost.optimizer.models.StepExecutionStatus
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
 * Enforces Phase 4 & Phase 7 distinction between DISPLAY REFRESH RATE and GAME FRAME RATE.
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
        val targetPackage = gameProfile.activePackageName
        Log.i(TAG, "Starting optimization session for ${gameProfile.displayName} ($targetPackage) with target ${profile.targetRefreshRate}Hz")

        val hasPrivilege = privilegedEngine?.isReady == true || shizukuManager.hasPermission()
        if (!hasPrivilege) {
            val res = OptimizationResult(
                isSuccess = false,
                gameName = gameProfile.displayName,
                requestedRefreshRate = profile.targetRefreshRate,
                actualRefreshRate = displayController.getDisplayState().currentRefreshRate,
                statusMessage = "Privileged authorization required",
                technicalExplanation = "GameBoost needs user-authorized Shizuku or Wireless Debugging to adjust display and system configuration.",
                displayRefreshVerified = false,
                displayRefreshStatusText = "DISCONNECTED",
                gameFpsVerified = false,
                gameFpsStatusText = "Privileged connection required to check frame rate",
                gameModeStatusText = "Unavailable"
            )
            _lastResult.value = res
            return res
        }

        // 1. CAPTURE BASELINE (Phase 6)
        val baseline = (activeBackup ?: displayController.createBackup()).copy(
            targetGamePackage = targetPackage
        ).also {
            activeBackup = it
            Log.d(TAG, "Captured display and system baseline backup: $it")
        }

        // 2. CREATE SESSION
        var session = OptimizationSession(
            gamePackage = targetPackage,
            gameName = gameProfile.displayName,
            selectedProfile = profile,
            requestedRefreshRate = profile.targetRefreshRate,
            originalSettings = baseline,
            sessionStatus = SessionStatus.BASELINE_CAPTURED
        )
        _currentSession.value = session

        val allSteps = mutableListOf<OptimizationStepResult>()
        val applied = mutableListOf<String>()
        val failed = mutableListOf<String>()

        val effectiveTargetRate = if (profile.targetRefreshRate <= 0f) {
            displayController.getHighestSupportedRefreshRate()
        } else {
            profile.targetRefreshRate
        }

        // 3. APPLY REFRESH RATE (Phase 3 & Phase 5)
        val (displaySuccess, displaySteps) = displayController.applyRefreshRateDetailed(effectiveTargetRate)
        allSteps.addAll(displaySteps)
        displaySteps.forEach { step ->
            if (step.status == StepExecutionStatus.ACTIVE) {
                applied.add(step.details.ifEmpty { step.stepName })
            } else if (step.status == StepExecutionStatus.FAILED) {
                failed.add(step.details.ifEmpty { step.stepName })
            }
        }

        // 4. APPLY PERFORMANCE & GAME MODE SETTINGS (Phase 4 & Phase 5)
        val (perfSteps, perfSummaries) = performanceController.applyPerformanceProfileDetailed(
            profile = profile,
            targetPackageName = targetPackage,
            targetRefreshRate = effectiveTargetRate
        )
        allSteps.addAll(perfSteps)
        applied.addAll(perfSummaries)
        perfSteps.filter { it.status == StepExecutionStatus.FAILED }.forEach {
            failed.add("${it.stepName}: ${it.details}")
        }

        // 5. PHYSICAL DISPLAY & STATE VERIFICATION (Phase 3, Phase 4, Phase 7)
        val finalDisplayState = displayController.getDisplayState()
        val displayVerified = Math.abs(finalDisplayState.currentRefreshRate - effectiveTargetRate) < 1.5f

        val displayStatusText = if (displayVerified) {
            "${finalDisplayState.currentRefreshRate.toInt()}Hz ACTIVE (Verified)"
        } else {
            "${effectiveTargetRate.toInt()}Hz REQUESTED (NOT VERIFIED - Display at ${finalDisplayState.currentRefreshRate.toInt()}Hz)"
        }

        // Explicit distinction between DISPLAY REFRESH RATE and GAME FRAME RATE (Phase 4 & Phase 7)
        val gameFpsStatusText = if (displayVerified) {
            "90 FPS NOT VERIFIED: Display panel confirmed at ${finalDisplayState.currentRefreshRate.toInt()}Hz. In-game Graphics setting must be set to '90 FPS' or 'Extreme+'. Anti-cheat and game binaries are never modified."
        } else {
            "60 FPS (Display locked at ${finalDisplayState.currentRefreshRate.toInt()}Hz by OEM compositor policy)"
        }

        val gameModeStatusText = if (profile.setGameModePerformance) {
            "Game Mode Performance (GameManager)"
        } else {
            "Standard"
        }

        val overallSuccess = displaySuccess || perfSummaries.isNotEmpty()
        val techExplanation = if (!displayVerified) {
            "Display refresh rate could not be locked to ${effectiveTargetRate.toInt()}Hz. Device OEM framework, dynamic thermal limits, or display power manager restricted mode transition."
        } else null

        val result = OptimizationResult(
            isSuccess = overallSuccess,
            gameName = gameProfile.displayName,
            requestedRefreshRate = effectiveTargetRate,
            actualRefreshRate = finalDisplayState.currentRefreshRate,
            appliedSettings = applied,
            failedSettings = failed,
            statusMessage = if (displayVerified && failed.isEmpty()) "Optimization active" else if (overallSuccess) "Applied with limitations" else "Optimization failed",
            technicalExplanation = techExplanation,
            verified = displayVerified,
            displayRefreshVerified = displayVerified,
            displayRefreshStatusText = displayStatusText,
            gameFpsVerified = false, // Never fake 90 FPS claim without in-game measurement
            gameFpsStatusText = gameFpsStatusText,
            gameModeStatusText = gameModeStatusText,
            optimizationSteps = allSteps
        )

        // 6. ACTIVE SESSION
        session = session.copy(
            modifiedSettings = applied,
            verificationResult = result,
            sessionStatus = if (overallSuccess) SessionStatus.ACTIVE else SessionStatus.FAILED
        )
        _currentSession.value = session
        _isOptimized.value = overallSuccess
        _activeGameProfile.value = gameProfile.copy(optimizationActive = overallSuccess, lastOptimizedTime = System.currentTimeMillis())
        _lastResult.value = result

        return result
    }

    suspend fun restoreDefaults(): RestorationResult {
        Log.i(TAG, "Restoring previous system configuration")

        val backup = activeBackup
        val targetPkg = backup?.targetGamePackage ?: _activeGameProfile.value?.activePackageName

        val restoredItems = mutableListOf<String>()
        val failedItems = mutableListOf<String>()
        var failureReason: String? = null

        val (displayRestored, displayMsg) = displayController.restoreDefault(backup)
        if (displayRestored) {
            restoredItems.add("Refresh rate restored to system defaults")
        } else {
            failedItems.add("Refresh rate could not be restored")
            failureReason = displayMsg
        }

        val (perfRestored, perfFailed) = performanceController.restorePerformanceSettings(backup, targetPkg)
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
