package com.gameboost.optimizer.models

data class DisplayModeInfo(
    val modeId: Int,
    val width: Int,
    val height: Int,
    val refreshRate: Float
) {
    val label: String
        get() = "${width}x${height} @ ${refreshRate.toInt()}Hz"
}

/**
 * Formal display capability model matching specification section 5.
 */
data class DisplayCapabilities(
    val currentRefreshRate: Float,
    val supportedRefreshRates: List<Float>,
    val maximumRefreshRate: Float = supportedRefreshRates.maxOrNull() ?: currentRefreshRate,
    val supports120Hz: Boolean = supportedRefreshRates.any { it in 119.0f..121.0f },
    val supports90Hz: Boolean = supportedRefreshRates.any { it in 89.0f..91.0f },
    val supports60Hz: Boolean = supportedRefreshRates.any { it in 59.0f..61.0f } || supportedRefreshRates.isEmpty(),
    val supportedModes: List<DisplayModeInfo> = emptyList(),
    val resolutionWidth: Int = 1080,
    val resolutionHeight: Int = 2400,
    val supportsHdr: Boolean = false
) {
    val highestRefreshRate: Float
        get() = maximumRefreshRate

    val maxRefreshRate: Float
        get() = maximumRefreshRate
}


enum class RefreshRateTarget(val targetRate: Float, val displayName: String) {
    SYSTEM_DEFAULT(0f, "System Default"),
    HZ_60(60f, "60Hz"),
    HZ_90(90f, "90Hz"),
    HZ_120(120f, "120Hz"),
    HIGHEST_AVAILABLE(-1f, "Highest Available");

    companion object {
        fun getAvailableForDisplay(capabilities: DisplayCapabilities): List<RefreshRateTarget> {
            val list = mutableListOf(SYSTEM_DEFAULT)
            if (capabilities.supports60Hz) list.add(HZ_60)
            if (capabilities.supports90Hz) list.add(HZ_90)
            if (capabilities.supports120Hz) list.add(HZ_120)
            list.add(HIGHEST_AVAILABLE)
            return list
        }
    }
}
