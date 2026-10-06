package com.gameboost.optimizer.models

/**
 * Performance Modes specified in Section 5:
 * SAFE, PERFORMANCE, AGGRESSIVE, THERMAL OVERRIDE
 */
enum class PerformanceMode {
    SAFE,
    PERFORMANCE,
    AGGRESSIVE,
    THERMAL_OVERRIDE;

    val displayName: String
        get() = when (this) {
            SAFE -> "Safe"
            PERFORMANCE -> "Performance"
            AGGRESSIVE -> "Aggressive"
            THERMAL_OVERRIDE -> "Thermal Override (Experimental)"
        }

    val description: String
        get() = when (this) {
            SAFE -> "Minimal system changes prioritizing stability. Optimizes supported display and standard game settings."
            PERFORMANCE -> "Recommended. Reduces UI animation latency, optimizes eligible background workload, activates Android Game Mode Performance."
            AGGRESSIVE -> "Maximum supported performance. Aggressively minimizes background workload and prioritizes sustained gaming over battery."
            THERMAL_OVERRIDE -> "EXPERIMENTAL & DANGEROUS. Requests maximum SoC profile using supported APIs. Subject to hardware safety protection."
        }

    val isDangerous: Boolean
        get() = this == THERMAL_OVERRIDE

    companion object {
        // Compatibility aliases for existing references
        val BALANCED get() = SAFE
        val EXTREME get() = AGGRESSIVE
    }
}

// Backward-compatibility alias
typealias OptimizationProfileType = PerformanceMode

data class OptimizationProfile(
    val mode: PerformanceMode = PerformanceMode.PERFORMANCE,
    val targetRefreshRate: Float = 0f, // 0f means automatic highest supported display mode
    val autoBoostOnLaunch: Boolean = true,
    val restoreOnExit: Boolean = true,
    val optimizeAnimations: Boolean = true,
    val setGameModePerformance: Boolean = true,
    val optimizeMemory: Boolean = true,
    val thermalOverrideConfirmed: Boolean = false
) {
    val type: PerformanceMode
        get() = mode

    val displayName: String
        get() = mode.displayName

    val description: String
        get() = mode.description
}
