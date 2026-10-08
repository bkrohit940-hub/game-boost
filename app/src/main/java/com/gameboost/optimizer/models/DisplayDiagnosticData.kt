package com.gameboost.optimizer.models

/**
 * Diagnostic data model holding genuine system, display, and game telemetry (Phase 2).
 */
data class DisplayDiagnosticData(
    // Display
    val currentRefreshRate: Float,
    val supportedRefreshRates: List<Float>,
    val currentDisplayMode: String,
    val supportedModes: List<String>,
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val densityDpi: Int,
    val systemPeakRefreshRate: String?,
    val systemMinRefreshRate: String?,
    val userRefreshRate: String?,
    val oemRefreshRate: String?,
    val preferredDisplayMode: String?,

    // Game
    val selectedPackageName: String,
    val isGameRunning: Boolean,
    val gameModeApiStatus: String,
    val gameConfigs: String?,
    val frameRateOverrideState: String,

    // Backend
    val shizukuInstalled: Boolean,
    val shizukuRunning: Boolean,
    val shizukuAuthorized: Boolean,
    val shizukuBinderConnected: Boolean,
    val wirelessAdbConnected: Boolean,
    val backendTestExitCode: Int?,
    val backendTestOutput: String?
)
