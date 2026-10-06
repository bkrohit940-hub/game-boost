package com.gameboost.optimizer.system

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.gameboost.optimizer.models.DeviceCapabilities

/**
 * Scans hardware and OS capabilities to populate the device capability matrix.
 */
class DeviceCapabilityDetector(
    private val context: Context,
    private val displayController: DisplayController
) {
    fun detectCapabilities(): DeviceCapabilities {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val displayState = displayController.getDisplayState()
        val oemSkin = detectOemSkin()

        val soc = if (Build.VERSION.SDK_INT >= 31) {
            Build.SOC_MODEL.ifEmpty { Build.HARDWARE }
        } else {
            Build.HARDWARE
        }

        return DeviceCapabilities(
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            brand = Build.BRAND.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            socHardware = soc,
            cpuCores = Runtime.getRuntime().availableProcessors(),
            totalRamBytes = memInfo.totalMem,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            displayState = displayState,
            oemSkin = oemSkin
        )
    }

    private fun detectOemSkin(): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("samsung") -> "Samsung One UI"
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> "Xiaomi HyperOS / MIUI"
            manufacturer.contains("oppo") || manufacturer.contains("realme") -> "ColorOS / RealmeUI"
            manufacturer.contains("oneplus") -> "OxygenOS"
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> "OriginOS / FuntouchOS"
            manufacturer.contains("google") -> "Google Pixel Android"
            manufacturer.contains("motorola") -> "Motorola MyUX"
            manufacturer.contains("nothing") -> "Nothing OS"
            else -> "AOSP / Stock Android"
        }
    }
}
