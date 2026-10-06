package com.gameboost.optimizer.system

import android.content.Context
import android.os.Build
import android.util.Log
import com.gameboost.optimizer.models.DisplayStateBackup
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationProfileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles legitimate Android performance and system configuration
 * via safe, allowlisted Shizuku operations.
 */
class PerformanceController(
    private val context: Context,
    private val shizukuManager: ShizukuManager
) {
    companion object {
        private const val TAG = "PerformanceController"
    }

    suspend fun applyPerformanceProfile(
        profile: OptimizationProfile,
        targetPackageName: String?
    ): Pair<List<String>, List<String>> = withContext(Dispatchers.IO) {
        val applied = mutableListOf<String>()
        val failed = mutableListOf<String>()

        if (!shizukuManager.hasPermission()) {
            failed.add("Shizuku unauthorized")
            return@withContext Pair(applied, failed)
        }

        // 1. Animation Scale Optimization
        if (profile.optimizeAnimations) {
            val scale = when (profile.type) {
                OptimizationProfileType.BALANCED -> "1.0"
                OptimizationProfileType.PERFORMANCE -> "0.5"
                OptimizationProfileType.EXTREME -> "0.0"
            }

            val curWin = shizukuManager.executeCommand("settings get global window_animation_scale").getOrNull()?.trim()
            if (curWin != scale) {
                val winRes = shizukuManager.executeCommand("settings put global window_animation_scale $scale")
                val transRes = shizukuManager.executeCommand("settings put global transition_animation_scale $scale")
                val durRes = shizukuManager.executeCommand("settings put global animator_duration_scale $scale")

                if (winRes.isSuccess && transRes.isSuccess && durRes.isSuccess) {
                    applied.add("UI Animation Scale ($scale x)")
                } else {
                    failed.add("Animation scale reduction failed")
                }
            } else {
                applied.add("UI Animation Scale ($scale x already active)")
            }
        }

        // 2. Android 12+ Game Mode API
        if (profile.setGameModePerformance && !targetPackageName.isNullOrEmpty() && Build.VERSION.SDK_INT >= 31) {
            if (CommandAllowlist.validatePackageName(targetPackageName)) {
                val modeArg = when (profile.type) {
                    OptimizationProfileType.BALANCED -> "standard"
                    OptimizationProfileType.PERFORMANCE,
                    OptimizationProfileType.EXTREME -> "performance"
                }

                val cmd = "cmd game mode $modeArg $targetPackageName"
                val res = shizukuManager.executeCommand(cmd)
                if (res.isSuccess) {
                    applied.add("Game Mode API: $modeArg ($targetPackageName)")
                } else {
                    failed.add("Game Mode API rejected by system: ${res.exceptionOrNull()?.message}")
                }
            } else {
                failed.add("Invalid package name for Game Mode API")
            }
        }

        Pair(applied, failed)
    }

    suspend fun restorePerformanceSettings(
        backup: DisplayStateBackup?,
        targetPackageName: String?
    ): Pair<List<String>, List<String>> = withContext(Dispatchers.IO) {
        val restored = mutableListOf<String>()
        val failed = mutableListOf<String>()

        if (!shizukuManager.hasPermission()) {
            return@withContext Pair(restored, failed)
        }

        // Restore animation scales
        val winScale = backup?.windowAnimationScale ?: "1.0"
        val transScale = backup?.transitionAnimationScale ?: "1.0"
        val durScale = backup?.animatorDurationScale ?: "1.0"

        val winRes = shizukuManager.executeCommand("settings put global window_animation_scale $winScale")
        val transRes = shizukuManager.executeCommand("settings put global transition_animation_scale $transScale")
        val durRes = shizukuManager.executeCommand("settings put global animator_duration_scale $durScale")

        if (winRes.isSuccess && transRes.isSuccess && durRes.isSuccess) {
            restored.add("Restored animation scales ($winScale, $transScale, $durScale)")
        } else {
            failed.add("Could not restore animation scales")
        }

        // Reset game mode to standard if previously set
        if (!targetPackageName.isNullOrEmpty() && Build.VERSION.SDK_INT >= 31) {
            if (CommandAllowlist.validatePackageName(targetPackageName)) {
                val res = shizukuManager.executeCommand("cmd game mode standard $targetPackageName")
                if (res.isSuccess) {
                    restored.add("Restored Game Mode: standard")
                }
            }
        }

        Pair(restored, failed)
    }
}
