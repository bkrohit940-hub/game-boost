package com.gameboost.optimizer.system

import android.app.ActivityManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.Display
import com.gameboost.optimizer.models.CapabilityStatus
import com.gameboost.optimizer.models.DisplayCapabilities
import com.gameboost.optimizer.models.DisplayDiagnosticData
import com.gameboost.optimizer.models.DisplayModeInfo
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.OptimizationStepResult
import com.gameboost.optimizer.models.StepExecutionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Manages display capability detection, refresh rate switching,
 * verification, and state backup/restoration.
 * Implements capability-based Phase 3 refresh rate architecture.
 */
class DisplayController(
    private val context: Context,
    private val shizukuManager: ShizukuManager,
    private val privilegedEngine: PrivilegedExecutionEngine? = null
) {
    companion object {
        private const val TAG = "DisplayController"
    }

    private suspend fun runPrivilegedCommand(command: String): Result<String> {
        return privilegedEngine?.executeCommand(command) ?: shizukuManager.executeCommand(command)
    }

    private fun isPrivilegedReady(): Boolean {
        return privilegedEngine?.isReady ?: shizukuManager.hasPermission()
    }

    private val displayManager =
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun getDisplayCapabilities(): DisplayCapabilities {
        val defaultDisplay = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
            ?: return DisplayCapabilities(
                currentRefreshRate = 60f,
                supportedRefreshRates = listOf(60f),
                maximumRefreshRate = 60f,
                supports120Hz = false,
                supports90Hz = false,
                supports60Hz = true,
                supportedModes = emptyList()
            )

        val currentRate = defaultDisplay.refreshRate
        val supportedModes = defaultDisplay.supportedModes.map { mode ->
            DisplayModeInfo(
                modeId = mode.modeId,
                width = mode.physicalWidth,
                height = mode.physicalHeight,
                refreshRate = mode.refreshRate
            )
        }

        val supportedRates = supportedModes
            .map { Math.round(it.refreshRate).toFloat() }
            .distinct()
            .sorted()

        val maxRate = supportedRates.maxOrNull() ?: currentRate
        val activeMode = defaultDisplay.mode

        val supports60 = supportedRates.any { it in 59f..61f } || supportedRates.isEmpty()
        val supports90 = supportedRates.any { it in 89f..91f }
        val supports120 = supportedRates.any { it in 119f..121f }

        val isHdr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultDisplay.isHdr
        } else false

        return DisplayCapabilities(
            currentRefreshRate = currentRate,
            supportedRefreshRates = if (supportedRates.isNotEmpty()) supportedRates else listOf(60f),
            maximumRefreshRate = maxRate,
            supports120Hz = supports120,
            supports90Hz = supports90,
            supports60Hz = supports60,
            supportedModes = supportedModes,
            resolutionWidth = activeMode?.physicalWidth ?: 1080,
            resolutionHeight = activeMode?.physicalHeight ?: 2400,
            supportsHdr = isHdr
        )
    }

    fun getDisplayState(): DisplayCapabilities = getDisplayCapabilities()

    /**
     * Captures real-time diagnostic data matching Phase 2 engineering requirements.
     */
    suspend fun getDisplayDiagnostics(targetPackageName: String? = null): DisplayDiagnosticData = withContext(Dispatchers.IO) {
        val caps = getDisplayCapabilities()
        val defaultDisplay = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val activeMode = defaultDisplay?.mode
        val activeModeStr = if (activeMode != null) {
            "${activeMode.physicalWidth}x${activeMode.physicalHeight} @ ${activeMode.refreshRate.toInt()}Hz (Mode ${activeMode.modeId})"
        } else {
            "Standard Display Mode"
        }

        val supportedModesStr = caps.supportedModes.map { it.label }
        val density = context.resources.displayMetrics.densityDpi

        // Read real system settings values
        val peak = runPrivilegedCommand("settings get system peak_refresh_rate").getOrNull()
        val min = runPrivilegedCommand("settings get system min_refresh_rate").getOrNull()
        val user = runPrivilegedCommand("settings get secure user_refresh_rate").getOrNull()

        // Read OEM settings
        val miui = runPrivilegedCommand("settings get secure miui_refresh_rate").getOrNull()
        val samsung = runPrivilegedCommand("settings get system refresh_rate_mode").getOrNull()
        val oemValue = miui ?: samsung

        // Read Preferred display mode if set
        val prefModeRes = runPrivilegedCommand("dumpsys display | grep -E \"mUserPreferredMode|mOverrideDisplayInfo\"").getOrNull()

        // Game status
        val pkg = targetPackageName ?: "com.tencent.ig"
        val runningProcesses = activityManager.runningAppProcesses
        val isRunning = runningProcesses?.any { it.processName == pkg } == true

        var gameModeApi = "Not checked"
        var gameConfigs: String? = null
        if (Build.VERSION.SDK_INT >= 31) {
            val modeRes = runPrivilegedCommand("cmd game mode $pkg").getOrNull()
            gameModeApi = modeRes?.trim() ?: "Unavailable"

            val configsRes = runPrivilegedCommand("cmd game list-configs $pkg").getOrNull()
            gameConfigs = configsRes?.trim()
        }

        // Frame rate override status
        val frameRateOverride = if (gameConfigs != null && gameConfigs.contains("fps=")) {
            gameConfigs
        } else if (Build.VERSION.SDK_INT >= 33) {
            "Supported (cmd game fps)"
        } else {
            "Standard SurfaceFlinger compositor"
        }

        // Backend status
        val backendTest = privilegedEngine?.testActiveBackend() ?: shizukuManager.testConnection()

        DisplayDiagnosticData(
            currentRefreshRate = caps.currentRefreshRate,
            supportedRefreshRates = caps.supportedRefreshRates,
            currentDisplayMode = activeModeStr,
            supportedModes = supportedModesStr,
            resolutionWidth = caps.resolutionWidth,
            resolutionHeight = caps.resolutionHeight,
            densityDpi = density,
            systemPeakRefreshRate = sanitizeSettingValue(peak),
            systemMinRefreshRate = sanitizeSettingValue(min),
            userRefreshRate = sanitizeSettingValue(user),
            oemRefreshRate = sanitizeSettingValue(oemValue),
            preferredDisplayMode = sanitizeSettingValue(prefModeRes),
            selectedPackageName = pkg,
            isGameRunning = isRunning,
            gameModeApiStatus = gameModeApi,
            gameConfigs = gameConfigs,
            frameRateOverrideState = frameRateOverride,
            shizukuInstalled = shizukuManager.isInstalled(),
            shizukuRunning = shizukuManager.isRunning(),
            shizukuAuthorized = shizukuManager.hasPermission(),
            shizukuBinderConnected = shizukuManager.getStatus().isBinderConnected,
            wirelessAdbConnected = privilegedEngine?.systemState?.value?.wirelessAdbState?.status ==
                    com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED,
            backendTestExitCode = backendTest.exitCode,
            backendTestOutput = backendTest.stdout.ifEmpty { backendTest.stderr }
        )
    }

    /**
     * Reads current system settings to create a reversible backup before modification.
     */
    suspend fun createBackup(): DisplayStateBackup = withContext(Dispatchers.IO) {
        val peak = runPrivilegedCommand("settings get system peak_refresh_rate").getOrNull()
        val min = runPrivilegedCommand("settings get system min_refresh_rate").getOrNull()
        val user = runPrivilegedCommand("settings get secure user_refresh_rate").getOrNull()
        val winAnim = runPrivilegedCommand("settings get global window_animation_scale").getOrNull()
        val transAnim = runPrivilegedCommand("settings get global transition_animation_scale").getOrNull()
        val durAnim = runPrivilegedCommand("settings get global animator_duration_scale").getOrNull()

        // OEM settings
        val miui = runPrivilegedCommand("settings get secure miui_refresh_rate").getOrNull()
        val samsung = runPrivilegedCommand("settings get system refresh_rate_mode").getOrNull()

        DisplayStateBackup(
            peakRefreshRate = sanitizeSettingValue(peak),
            minRefreshRate = sanitizeSettingValue(min),
            userRefreshRate = sanitizeSettingValue(user),
            windowAnimationScale = sanitizeSettingValue(winAnim),
            transitionAnimationScale = sanitizeSettingValue(transAnim),
            animatorDurationScale = sanitizeSettingValue(durAnim),
            miuiRefreshRate = sanitizeSettingValue(miui),
            refreshRateMode = sanitizeSettingValue(samsung)
        )
    }

    fun getHighestSupportedRefreshRate(): Float {
        return getDisplayCapabilities().highestRefreshRate
    }

    /**
     * Detailed Phase 3 & 5 refresh rate application.
     * Evaluates hardware capabilities, applies allowlisted system & OEM commands,
     * and physically verifies the updated display state via DisplayManager.
     */
    suspend fun applyRefreshRateDetailed(
        targetRate: Float = 0f
    ): Pair<Boolean, List<OptimizationStepResult>> = withContext(Dispatchers.IO) {
        val steps = mutableListOf<OptimizationStepResult>()

        if (!isPrivilegedReady()) {
            val step = OptimizationStepResult(
                stepName = "Privileged Authorization",
                command = "shizuku/adb permission check",
                capability = CapabilityStatus.UNAVAILABLE,
                status = StepExecutionStatus.UNAVAILABLE,
                details = "Privileged authorization required to configure display refresh rate"
            )
            steps.add(step)
            return@withContext Pair(false, steps)
        }

        val displayCaps = getDisplayCapabilities()
        val effectiveRate = if (targetRate <= 0f) {
            displayCaps.highestRefreshRate
        } else {
            targetRate
        }

        // 1. CAPABILITY CHECK
        val isHardwareSupported = displayCaps.supportedRefreshRates.any {
            Math.abs(it - effectiveRate) < 1.5f
        }

        if (!isHardwareSupported) {
            val step = OptimizationStepResult(
                stepName = "Display Hardware Mode Capability",
                command = "Display.supportedModes query",
                capability = CapabilityStatus.NOT_SUPPORTED,
                status = StepExecutionStatus.NOT_SUPPORTED,
                details = "${effectiveRate.toInt()}Hz is not supported by display hardware (Available: ${displayCaps.supportedRefreshRates.map { it.toInt() }.joinToString()}Hz)"
            )
            steps.add(step)
            return@withContext Pair(false, steps)
        }

        Log.d(TAG, "Attempting capability-based switch to ${effectiveRate.toInt()}Hz")

        // 2. Method A: Android 11+ DisplayManager preferred mode command
        val matchingMode = displayCaps.supportedModes.firstOrNull {
            Math.abs(it.refreshRate - effectiveRate) < 1.5f
        }
        if (matchingMode != null) {
            val modeCmd = "cmd display set-user-preferred-display-mode ${matchingMode.width} ${matchingMode.height} ${effectiveRate.toInt()}"
            val modeRes = runPrivilegedCommand(modeCmd)
            steps.add(
                OptimizationStepResult(
                    stepName = "DisplayManager Preferred Mode",
                    command = modeCmd,
                    capability = CapabilityStatus.SUPPORTED,
                    status = if (modeRes.isSuccess) StepExecutionStatus.ACTIVE else StepExecutionStatus.FAILED,
                    exitCode = if (modeRes.isSuccess) 0 else -1,
                    output = modeRes.getOrNull(),
                    details = "Requested ${matchingMode.width}x${matchingMode.height} @ ${effectiveRate.toInt()}Hz display mode"
                )
            )
        }

        // 3. Method B: AOSP System & Global peak/min refresh rate
        val rateStr = String.format(Locale.US, "%.1f", effectiveRate)
        val rateInt = effectiveRate.toInt().toString()

        val sysPeakRes = runPrivilegedCommand("settings put system peak_refresh_rate $rateStr")
        val sysMinRes = runPrivilegedCommand("settings put system min_refresh_rate $rateStr")
        runPrivilegedCommand("settings put system peak_refresh_rate $rateInt")
        runPrivilegedCommand("settings put system min_refresh_rate $rateInt")

        val globPeakRes = runPrivilegedCommand("settings put global peak_refresh_rate $rateStr")
        val globMinRes = runPrivilegedCommand("settings put global min_refresh_rate $rateStr")
        runPrivilegedCommand("settings put global peak_refresh_rate $rateInt")
        runPrivilegedCommand("settings put global min_refresh_rate $rateInt")

        steps.add(
            OptimizationStepResult(
                stepName = "System Refresh Compositor (system & global)",
                command = "settings put system/global peak/min_refresh_rate $rateStr",
                capability = CapabilityStatus.SUPPORTED,
                status = if (sysPeakRes.isSuccess || globPeakRes.isSuccess) StepExecutionStatus.ACTIVE else StepExecutionStatus.FAILED,
                exitCode = 0,
                details = "Configured peak and min refresh rates to $rateStr"
            )
        )

        // 4. Method C: OEM Skin Settings (Xiaomi / Samsung / OnePlus / ColorOS)
        val mfr = Build.MANUFACTURER.lowercase()
        if (mfr.contains("xiaomi") || mfr.contains("redmi") || mfr.contains("poco")) {
            val miuiCmd = "settings put secure miui_refresh_rate $rateInt"
            val miuiRes = runPrivilegedCommand(miuiCmd)
            steps.add(
                OptimizationStepResult(
                    stepName = "Xiaomi MIUI/HyperOS Refresh Hook",
                    command = miuiCmd,
                    capability = CapabilityStatus.SUPPORTED,
                    status = if (miuiRes.isSuccess) StepExecutionStatus.ACTIVE else StepExecutionStatus.FAILED,
                    exitCode = if (miuiRes.isSuccess) 0 else -1,
                    details = "Applied MIUI refresh rate: $rateInt"
                )
            )
        } else if (mfr.contains("samsung")) {
            val samCmd = "settings put system refresh_rate_mode 2"
            val samRes = runPrivilegedCommand(samCmd)
            steps.add(
                OptimizationStepResult(
                    stepName = "Samsung OneUI High Refresh Mode",
                    command = samCmd,
                    capability = CapabilityStatus.SUPPORTED,
                    status = if (samRes.isSuccess) StepExecutionStatus.ACTIVE else StepExecutionStatus.FAILED,
                    exitCode = if (samRes.isSuccess) 0 else -1,
                    details = "Set refresh_rate_mode=2 (High/Adaptive)"
                )
            )
        } else if (mfr.contains("oppo") || mfr.contains("realme") || mfr.contains("oneplus")) {
            val oplusVal = if (effectiveRate >= 119f) 2 else if (effectiveRate >= 89f) 1 else 0
            val oplusCmd = "settings put secure user_refresh_rate $oplusVal"
            val oplusRes = runPrivilegedCommand(oplusCmd)
            runPrivilegedCommand("settings put secure refresh_rate_setting 2")
            steps.add(
                OptimizationStepResult(
                    stepName = "ColorOS / OxygenOS Refresh Hook",
                    command = oplusCmd,
                    capability = CapabilityStatus.SUPPORTED,
                    status = if (oplusRes.isSuccess) StepExecutionStatus.ACTIVE else StepExecutionStatus.FAILED,
                    exitCode = if (oplusRes.isSuccess) 0 else -1,
                    details = "Set user_refresh_rate=$oplusVal"
                )
            )
        }

        // Allow system compositor to enact the rate change
        delay(400)

        // 5. PHYSICAL DISPLAY VERIFICATION (Phase 3 requirement)
        val updatedState = getDisplayCapabilities()
        val verified = Math.abs(updatedState.currentRefreshRate - effectiveRate) < 1.5f

        val verifyStep = OptimizationStepResult(
            stepName = "Display Compositor Verification",
            command = "Display.getRefreshRate()",
            capability = CapabilityStatus.SUPPORTED,
            status = if (verified) StepExecutionStatus.ACTIVE else StepExecutionStatus.FAILED,
            exitCode = if (verified) 0 else -1,
            verified = verified,
            details = if (verified) {
                "${updatedState.currentRefreshRate.toInt()}Hz ACTIVE (Verified via DisplayManager compositor)"
            } else {
                "${effectiveRate.toInt()}Hz REQUESTED - NOT VERIFIED (Display remains at ${updatedState.currentRefreshRate.toInt()}Hz. OEM framework or thermal policy restricted mode change)"
            }
        )
        steps.add(verifyStep)

        Pair(verified, steps)
    }

    /**
     * Backward-compatible helper returning simple (success, message).
     */
    suspend fun applyRefreshRate(targetRate: Float = 0f): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val (success, steps) = applyRefreshRateDetailed(targetRate)
        val lastStep = steps.lastOrNull()
        val msg = lastStep?.details ?: if (success) "Refresh rate applied" else "Failed to switch refresh rate"
        Pair(success, msg)
    }

    /**
     * Restores previous display and system state from backup.
     */
    suspend fun restoreDefault(backup: DisplayStateBackup?): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (!isPrivilegedReady()) {
            return@withContext Pair(false, "Privileged authorization required to restore settings")
        }

        try {
            if (backup?.peakRefreshRate != null && backup.peakRefreshRate != "null") {
                runPrivilegedCommand("settings put system peak_refresh_rate ${backup.peakRefreshRate}")
                runPrivilegedCommand("settings put global peak_refresh_rate ${backup.peakRefreshRate}")
            } else {
                runPrivilegedCommand("settings delete system peak_refresh_rate")
                runPrivilegedCommand("settings delete global peak_refresh_rate")
            }

            if (backup?.minRefreshRate != null && backup.minRefreshRate != "null") {
                runPrivilegedCommand("settings put system min_refresh_rate ${backup.minRefreshRate}")
                runPrivilegedCommand("settings put global min_refresh_rate ${backup.minRefreshRate}")
            } else {
                runPrivilegedCommand("settings delete system min_refresh_rate")
                runPrivilegedCommand("settings delete global min_refresh_rate")
            }

            if (backup?.userRefreshRate != null && backup.userRefreshRate != "null") {
                runPrivilegedCommand("settings put secure user_refresh_rate ${backup.userRefreshRate}")
            } else {
                runPrivilegedCommand("settings delete secure user_refresh_rate")
            }

            // Restore OEM settings if backed up
            if (backup?.miuiRefreshRate != null) {
                runPrivilegedCommand("settings put secure miui_refresh_rate ${backup.miuiRefreshRate}")
            } else {
                runPrivilegedCommand("settings delete secure miui_refresh_rate")
            }

            if (backup?.refreshRateMode != null) {
                runPrivilegedCommand("settings put system refresh_rate_mode ${backup.refreshRateMode}")
            }

            // Clear any user-preferred display mode override
            runPrivilegedCommand("cmd display clear-user-preferred-display-mode")

            delay(300)
            Pair(true, "Refresh rate restored to system defaults")
        } catch (e: Throwable) {
            Log.e(TAG, "Error restoring display settings", e)
            Pair(false, "Failed to restore display settings: ${e.localizedMessage}")
        }
    }

    private fun sanitizeSettingValue(raw: String?): String? {
        val trimmed = raw?.trim()
        return if (trimmed.isNullOrEmpty() || trimmed == "null") null else trimmed
    }
}
