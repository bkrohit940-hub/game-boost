package com.gameboost.optimizer.models

import java.util.Locale

data class HardwareStats(
    val batteryPercentage: Int = 100,
    val batteryTemperatureCelsius: Float = 30.0f,
    val isCharging: Boolean = false,
    val ramUsedBytes: Long = 0L,
    val ramTotalBytes: Long = 0L,
    val currentRefreshRate: Float = 60f,
    val estimatedFpsText: String = "Not available",
    val cpuFrequencyInfo: String = "Normal",
    val thermalStatus: String = "NORMAL",
    val thermalHeadroom: String? = null
) {
    val formattedRam: String
        get() {
            if (ramTotalBytes <= 0) return "-- / -- GB"
            val usedGb = ramUsedBytes.toDouble() / (1024 * 1024 * 1024)
            val totalGb = ramTotalBytes.toDouble() / (1024 * 1024 * 1024)
            return String.format(Locale.US, "%.1f / %.1f GB", usedGb, totalGb)
        }

    val formattedTemp: String
        get() = String.format(Locale.US, "%.0f°C", batteryTemperatureCelsius)
}

