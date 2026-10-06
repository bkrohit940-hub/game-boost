package com.gameboost.optimizer.system

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * Detects installed game packages and identifies active foreground application.
 */
class PackageDetector(
    private val context: Context,
    private val shizukuManager: ShizukuManager
) {
    companion object {
        private const val TAG = "PackageDetector"
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
        if (!shizukuManager.hasPermission()) return null

        return try {
            val result = shizukuManager.executeCommand("dumpsys activity top | grep ACTIVITY")
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
