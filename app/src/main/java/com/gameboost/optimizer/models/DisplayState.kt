package com.gameboost.optimizer.models

typealias DisplayState = DisplayCapabilities

/**
 * Backup of user's display and system settings before optimization was enabled.
 * Enables 100% reversible restoration when gaming ends or user disables optimization.
 */
data class DisplayStateBackup(
    val peakRefreshRate: String? = null,
    val minRefreshRate: String? = null,
    val userRefreshRate: String? = null,
    val windowAnimationScale: String? = null,
    val transitionAnimationScale: String? = null,
    val animatorDurationScale: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
