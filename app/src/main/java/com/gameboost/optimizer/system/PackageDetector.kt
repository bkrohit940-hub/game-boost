package com.gameboost.optimizer.system

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * Detects installed game packages and identifies active foreground application.
 */
class PackageDetector(
    private val context: Context,
    private val shizukuManager: ShizukuManager,
    private val privilegedEngine: PrivilegedExecutionEngine? = null
) {
    companion object {
        private const val TAG = "PackageDetector"
    }

    private suspend fun runPrivilegedCommand(command: String): Result<String> {
        return privilegedEngine?.executeCommand(command) ?: shizukuManager.executeCommand(command)
    }

    private fun isPrivilegedReady(): Boolean {
        return privilegedEngine?.isReady ?: shizukuManager.hasPermission()
    }

    private val packageManager: PackageManager = context.packageManager

    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Throwable) {
            false
        }
    }

    fun getPackageVersion(packageName: String): String? {
        return try {
            val info = packageManager.getPackageInfo(packageName, 0)
            info.versionName
        } catch (_: Throwable) {
            null
        }
    }

    fun getApplicationIcon(packageName: String): android.graphics.drawable.Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (_: Throwable) {
            null
        }
    }

    fun getApplicationLabel(packageName: String): String? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Retrieves all installed launchable applications for BOBO Launcher.
     * Complies with Android package visibility via <queries> intent filter.
     */
    fun getAllInstalledApps(favoritePackages: Set<String> = emptySet()): List<com.gameboost.optimizer.models.InstalledAppItem> {
        val mainIntent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = try {
            packageManager.queryIntentActivities(mainIntent, 0)
        } catch (_: Throwable) {
            emptyList()
        }
        val myPackage = context.packageName

        return resolveInfos
            .filter { it.activityInfo != null && it.activityInfo.packageName != myPackage }
            .mapNotNull { resolveInfo ->
                try {
                    val pkgName = resolveInfo.activityInfo.packageName
                    val label = resolveInfo.loadLabel(packageManager).toString()
                    val icon = resolveInfo.loadIcon(packageManager)
                    val appInfo = resolveInfo.activityInfo.applicationInfo
                    val isGame = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        (appInfo.category == android.content.pm.ApplicationInfo.CATEGORY_GAME) ||
                        ((appInfo.flags and android.content.pm.ApplicationInfo.FLAG_IS_GAME) != 0)
                    } else {
                        ((appInfo.flags and android.content.pm.ApplicationInfo.FLAG_IS_GAME) != 0)
                    } || com.gameboost.optimizer.data.gameprofiles.GameRegistry.findGameByPackage(pkgName) != null

                    val version = getPackageVersion(pkgName)
                    com.gameboost.optimizer.models.InstalledAppItem(
                        packageName = pkgName,
                        label = label,
                        icon = icon,
                        isGame = isGame,
                        isFavorite = favoritePackages.contains(pkgName),
                        versionName = version
                    )
                } catch (_: Throwable) {
                    null
                }
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun launchPackage(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to launch package: $packageName", e)
            false
        }
    }

    /**
     * Determines which package is currently in the foreground using Shizuku dumpsys activity.
     */
    suspend fun getForegroundPackage(): String? {
        if (!isPrivilegedReady()) return null

        return try {
            val result = runPrivilegedCommand("dumpsys activity top | grep ACTIVITY")
            if (result.isSuccess) {
                val output = result.getOrNull() ?: return null
                // Sample line: "ACTIVITY com.tencent.ig/com.epicgames.ue4.SplashActivity 78af18e pid=1234"
                val match = Regex("ACTIVITY\\s+([a-zA-Z0-9_.]+)/").find(output)
                match?.groupValues?.getOrNull(1)
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Error checking foreground package", e)
            null
        }
    }
}
