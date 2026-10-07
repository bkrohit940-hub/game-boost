package com.gameboost.optimizer.system

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.Display
import com.gameboost.optimizer.models.DisplayCapabilities
import com.gameboost.optimizer.models.DisplayModeInfo
import com.gameboost.optimizer.models.DisplayStateBackup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Manages display capability detection, refresh rate switching,
 * verification, and state backup/restoration.
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
     * Reads current system settings to create a reversible backup before modification.
     */
    suspend fun createBackup(): DisplayStateBackup = withContext(Dispatchers.IO) {
        val peak = runPrivilegedCommand("settings get system peak_refresh_rate").getOrNull()
        val min = runPrivilegedCommand("settings get system min_refresh_rate").getOrNull()
        val user = runPrivilegedCommand("settings get secure user_refresh_rate").getOrNull()
        val winAnim = runPrivilegedCommand("settings get global window_animation_scale").getOrNull()
        val transAnim = runPrivilegedCommand("settings get global transition_animation_scale").getOrNull()
        val durAnim = runPrivilegedCommand("settings get global animator_duration_scale").getOrNull()

        DisplayStateBackup(
            peakRefreshRate = sanitizeSettingValue(peak),
            minRefreshRate = sanitizeSettingValue(min),
            userRefreshRate = sanitizeSettingValue(user),
            windowAnimationScale = sanitizeSettingValue(winAnim),
            transitionAnimationScale = sanitizeSettingValue(transAnim),
            animatorDurationScale = sanitizeSettingValue(durAnim)
        )
    }

    fun getHighestSupportedRefreshRate(): Float {
        return getDisplayCapabilities().highestRefreshRate
    }

    /**
     * Applies target refresh rate using safe, allowlisted system commands.
     * When targetRate <= 0f, automatically detects and applies the highest supported display mode.
     */
    suspend fun applyRefreshRate(targetRate: Float = 0f): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (!isPrivilegedReady()) {
            return@withContext Pair(false, "Privileged authorization required to configure display rate")
        }

        val displayCaps = getDisplayCapabilities()
        val effectiveRate = if (targetRate <= 0f) {
            displayCaps.highestRefreshRate
        } else {
            targetRate
        }

        Log.d(TAG, "Attempting to switch refresh rate to ${effectiveRate}Hz")

        // 1. Set peak and min refresh rates in system namespace
        runPrivilegedCommand("settings put system peak_refresh_rate $effectiveRate")
        runPrivilegedCommand("settings put system min_refresh_rate $effectiveRate")

        // 2. Also update global namespace for AOSP compatibility
        runPrivilegedCommand("settings put global peak_refresh_rate $effectiveRate")
        runPrivilegedCommand("settings put global min_refresh_rate $effectiveRate")

        // 3. For OEM ROMs that use secure user_refresh_rate (e.g., ColorOS/RealmeUI)
        if (effectiveRate.toInt() == 144 || effectiveRate.toInt() == 120 || effectiveRate.toInt() == 90 || effectiveRate.toInt() == 60) {
            runPrivilegedCommand("settings put secure user_refresh_rate ${effectiveRate.toInt()}")
        }

        // Allow system compositor to enact the rate change
        delay(350)

        // Verify actual applied rate
        val updatedState = getDisplayCapabilities()
        val verified = Math.abs(updatedState.currentRefreshRate - effectiveRate) < 1.5f

        if (verified) {
            Pair(true, "Display switched to ${updatedState.currentRefreshRate.toInt()}Hz (${updatedState.currentRefreshRate.toInt()}Hz panel verified)")
        } else {
            Pair(
                false,
                "Display control restricted by device/OEM"
            )
        }
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

            // Clear any user-preferred display mode override
            runPrivilegedCommand("cmd display clear-user-preferred-display-mode")

            delay(250)
            Pair(true, "Refresh rate restored")
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
