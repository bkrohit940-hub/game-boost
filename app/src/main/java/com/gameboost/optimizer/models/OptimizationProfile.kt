package com.gameboost.optimizer.models

enum class OptimizationProfileType {
    BALANCED,
    PERFORMANCE,
    EXTREME
}

data class OptimizationProfile(
    val type: OptimizationProfileType = OptimizationProfileType.PERFORMANCE,
    val targetRefreshRate: Float = 120f,
    val autoBoostOnLaunch: Boolean = true,
    val restoreOnExit: Boolean = true,
    val optimizeAnimations: Boolean = true,
    val setGameModePerformance: Boolean = true
) {
    val displayName: String
        get() = when (type) {
            OptimizationProfileType.BALANCED -> "Balanced"
            OptimizationProfileType.PERFORMANCE -> "Performance"
            OptimizationProfileType.EXTREME -> "Extreme (Safe)"
        }

    val description: String
        get() = when (type) {
            OptimizationProfileType.BALANCED -> "Highest supported refresh rate with default system animations and normal battery usage."
            OptimizationProfileType.PERFORMANCE -> "Target 120Hz display, reduced UI animation overhead, Android Game Mode Performance API opt-in."
            OptimizationProfileType.EXTREME -> "Max refresh rate, minimal animation latency, background sync throttling during active gaming."
        }
}
