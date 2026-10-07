package com.gameboost.optimizer.system

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.gameboost.optimizer.models.PerformanceMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Result of honest gaming memory optimization.
 * Avoids fabricated numbers and reports actual system memory changes.
 */
data class MemoryOptimizationResult(
    val ramUsedBeforeBytes: Long,
    val ramUsedAfterBytes: Long,
    val ramTotalBytes: Long,
    val ramAvailableBytes: Long,
    val optimizedPackages: List<String>,
    val statusMessage: String
) {
    val formattedRamUsed: String
        get() = String.format(Locale.US, "%.1f GB", ramUsedAfterBytes.toDouble() / (1024 * 1024 * 1024))

    val formattedRamAvailable: String
        get() = String.format(Locale.US, "%.1f GB", ramAvailableBytes.toDouble() / (1024 * 1024 * 1024))

    val formattedRamTotal: String
        get() = String.format(Locale.US, "%.1f GB", ramTotalBytes.toDouble() / (1024 * 1024 * 1024))
}

/**
 * Legitimate Gaming Memory Optimization System.
 *
 * Rules:
 * - Never claims fake memory cleaning or exaggerated FPS boosts.
 * - Respects an explicit, hardcoded list of protected system and critical packages.
 * - Never targets Game Boost or Shizuku.
 * - Uses legitimate Android memory trim mechanisms (am trim-memory) on eligible non-critical packages.
 */
class MemoryOptimizer(
    private val context: Context,
    private val shizukuManager: ShizukuManager,
    private val privilegedEngine: PrivilegedExecutionEngine? = null
) {
    companion object {
        private const val TAG = "MemoryOptimizer"

        /**
         * System-critical packages that must NEVER be terminated or disrupted.
         */
        val PROTECTED_PACKAGES: Set<String> = setOf(
            "com.gameboost.optimizer",               // Game Boost (self)
            "moe.shizuku.privileged.api",           // Shizuku manager & service
            "android",                              // Core Android framework
            "com.android.systemui",                 // System UI, status bar, navigation
            "com.google.android.gms",               // Google Play Services
            "com.google.android.gsf",               // Google Services Framework
            "com.android.vending",                  // Play Store
            "com.android.phone",                    // Cellular/Telephony
            "com.android.server.telecom",           // Telecom server
            "com.android.bluetooth",                // Bluetooth audio & controllers
            "com.android.nfc",                      // NFC
            "com.android.inputmethod.latin",        // AOSP Keyboard
            "com.google.android.inputmethod.latin", // Gboard
            "com.samsung.android.honeyboard",       // Samsung Keyboard
            "com.android.launcher",                 // Default launchers
            "com.android.launcher3",
            "com.google.android.apps.nexuslauncher",// Pixel Launcher
            "com.sec.android.app.launcher",         // One UI Launcher
            "com.miui.home",                        // HyperOS / MIUI Launcher
            "com.oppo.launcher"                     // ColorOS Launcher
        )
    }

    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    /**
     * Reads current hardware memory state from Android ActivityManager.
     */
    fun getCurrentMemoryInfo(): ActivityManager.MemoryInfo {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo
    }

    /**
     * Identifies eligible non-system, non-protected background user applications.
     */
    fun getEligibleBackgroundPackages(excludePackage: String? = null): List<String> {
        val pm = context.packageManager
        val installed = try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (_: Throwable) {
            emptyList<ApplicationInfo>()
        }

        return installed.filter { appInfo ->
            val pkg = appInfo.packageName
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            !isSystem &&
                    !isUpdatedSystem &&
                    !PROTECTED_PACKAGES.contains(pkg) &&
                    pkg != excludePackage &&
                    pkg != context.packageName
        }.map { it.packageName }
    }

    private suspend fun runPrivilegedCommand(command: String): Result<String> {
        return privilegedEngine?.executeCommand(command) ?: shizukuManager.executeCommand(command)
    }

    private fun isPrivilegedReady(): Boolean {
        return privilegedEngine?.isReady ?: shizukuManager.hasPermission()
    }

    /**
     * Optimizes background memory workload cleanly without arbitrary process killing.
     */
    suspend fun optimizeMemory(
        targetGamePackage: String?,
        mode: PerformanceMode = PerformanceMode.PERFORMANCE
    ): MemoryOptimizationResult = withContext(Dispatchers.IO) {
        val beforeMem = getCurrentMemoryInfo()
        val ramUsedBefore = beforeMem.totalMem - beforeMem.availMem

        if (!isPrivilegedReady()) {
            val afterMem = getCurrentMemoryInfo()
            return@withContext MemoryOptimizationResult(
                ramUsedBeforeBytes = ramUsedBefore,
                ramUsedAfterBytes = afterMem.totalMem - afterMem.availMem,
                ramTotalBytes = afterMem.totalMem,
                ramAvailableBytes = afterMem.availMem,
                optimizedPackages = emptyList(),
                statusMessage = "Privileged authorization required for background memory trimming"
            )
        }

        val eligiblePackages = getEligibleBackgroundPackages(targetGamePackage)
        val trimmedPackages = mutableListOf<String>()

        // Select trim level based on mode
        val trimLevel = when (mode) {
            PerformanceMode.SAFE -> "RUNNING_MODERATE"
            PerformanceMode.PERFORMANCE -> "RUNNING_LOW"
            PerformanceMode.AGGRESSIVE,
            PerformanceMode.THERMAL_OVERRIDE -> "COMPLETE"
        }

        // Limit to reasonable batch to avoid battery/I-O churn
        val batch = eligiblePackages.take(15)

        for (pkg in batch) {
            if (PROTECTED_PACKAGES.contains(pkg) || pkg == targetGamePackage) {
                continue
            }
            if (CommandAllowlist.validatePackageName(pkg)) {
                val cmd = "am trim-memory $pkg $trimLevel"
                val res = runPrivilegedCommand(cmd)
                if (res.isSuccess) {
                    trimmedPackages.add(pkg)
                }
            }
        }

        val afterMem = getCurrentMemoryInfo()
        val ramUsedAfter = afterMem.totalMem - afterMem.availMem

        val summary = if (trimmedPackages.isNotEmpty()) {
            "Trimmed background memory allocations across ${trimmedPackages.size} applications"
        } else {
            "No eligible background memory allocations required trimming"
        }

        MemoryOptimizationResult(
            ramUsedBeforeBytes = ramUsedBefore,
            ramUsedAfterBytes = ramUsedAfter,
            ramTotalBytes = afterMem.totalMem,
            ramAvailableBytes = afterMem.availMem,
            optimizedPackages = trimmedPackages,
            statusMessage = summary
        )
    }
}
