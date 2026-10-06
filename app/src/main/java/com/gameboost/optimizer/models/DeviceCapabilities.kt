package com.gameboost.optimizer.models

data class CapabilityItem(
    val name: String,
    val isSupported: Boolean,
    val detail: String
)

data class DeviceCapabilities(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val socHardware: String,
    val cpuCores: Int,
    val totalRamBytes: Long,
    val androidVersion: String,
    val apiLevel: Int,
    val displayState: DisplayState,
    val oemSkin: String
) {
    val formattedRam: String
        get() {
            val gb = totalRamBytes.toDouble() / (1024 * 1024 * 1024)
            return String.format(java.util.Locale.US, "%.1f GB", gb)
        }

    val capabilityMatrix: List<CapabilityItem>
        get() = listOf(
            CapabilityItem(
                name = "120Hz Display Support",
                isSupported = displayState.supports120Hz,
                detail = if (displayState.supports120Hz) "Device hardware panel supports 120Hz" else "Display max is ${displayState.maxRefreshRate.toInt()}Hz"
            ),
            CapabilityItem(
                name = "System Refresh-Rate Control",
                isSupported = true,
                detail = "Supported via Shizuku system & display shell controls"
            ),
            CapabilityItem(
                name = "Android Game Mode API",
                isSupported = apiLevel >= 31,
                detail = if (apiLevel >= 31) "Android 12+ GameMode API available" else "Requires Android 12+ (API 31+)"
            ),
            CapabilityItem(
                name = "Animation Scale Optimization",
                isSupported = true,
                detail = "Window & animator duration scale reduction supported"
            ),
            CapabilityItem(
                name = "Reversible Settings Restoration",
                isSupported = true,
                detail = "Captures baseline state and restores after session"
            )
        )
}
