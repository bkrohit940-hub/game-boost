package com.gameboost.optimizer.models

/**
 * Explicit Shizuku verification states matching section 7 specification:
 * SHIZUKU READY, SHIZUKU NOT RUNNING, PERMISSION REQUIRED, PERMISSION REVOKED, SHIZUKU UNAVAILABLE
 */
enum class ShizukuState {
    SHIZUKU_READY,
    SHIZUKU_NOT_RUNNING,
    PERMISSION_REQUIRED,
    PERMISSION_REVOKED,
    SHIZUKU_UNAVAILABLE;

    companion object {
        // Compatibility aliases for legacy tests and call sites
        val NOT_INSTALLED get() = SHIZUKU_UNAVAILABLE
        val NOT_RUNNING get() = SHIZUKU_NOT_RUNNING
        val AUTHORIZED get() = SHIZUKU_READY
        val ERROR get() = SHIZUKU_UNAVAILABLE
    }
}

/**
 * Encapsulates the verified runtime status of Shizuku service and authorization.
 */
data class ShizukuStatus(
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false,
    val isAuthorized: Boolean = false,
    val wasPreviouslyAuthorized: Boolean = false,
    val version: Int = 0,
    val uid: Int = -1,
    val errorMessage: String? = null
) {
    val state: ShizukuState
        get() = when {
            !isInstalled -> ShizukuState.SHIZUKU_UNAVAILABLE
            !isRunning -> ShizukuState.SHIZUKU_NOT_RUNNING
            !isAuthorized && wasPreviouslyAuthorized -> ShizukuState.PERMISSION_REVOKED
            !isAuthorized -> ShizukuState.PERMISSION_REQUIRED
            else -> ShizukuState.SHIZUKU_READY
        }

    val isReady: Boolean
        get() = state == ShizukuState.SHIZUKU_READY

    val title: String
        get() = when (state) {
            ShizukuState.SHIZUKU_READY -> "SHIZUKU READY"
            ShizukuState.SHIZUKU_NOT_RUNNING -> "SHIZUKU NOT RUNNING"
            ShizukuState.PERMISSION_REQUIRED -> "PERMISSION REQUIRED"
            ShizukuState.PERMISSION_REVOKED -> "PERMISSION REVOKED"
            ShizukuState.SHIZUKU_UNAVAILABLE -> "SHIZUKU UNAVAILABLE"
        }

    val summaryText: String
        get() = when (state) {
            ShizukuState.SHIZUKU_READY -> "Authorized & Active (API v$version)"
            ShizukuState.SHIZUKU_NOT_RUNNING -> "Service stopped. Start Shizuku via Wireless Debugging or ADB."
            ShizukuState.PERMISSION_REQUIRED -> "Service active. Permission authorization required."
            ShizukuState.PERMISSION_REVOKED -> "Permission revoked. Re-grant access in Shizuku app."
            ShizukuState.SHIZUKU_UNAVAILABLE -> "Shizuku is not installed on this device."
        }

    val actionPrompt: String
        get() = when (state) {
            ShizukuState.SHIZUKU_READY -> "All system and display controls are ready."
            ShizukuState.SHIZUKU_NOT_RUNNING -> "Open the Shizuku app and start the service."
            ShizukuState.PERMISSION_REQUIRED -> "Grant Shizuku permission to allow Game Boost optimization."
            ShizukuState.PERMISSION_REVOKED -> "Open Shizuku > Authorized Applications and re-enable Game Boost."
            ShizukuState.SHIZUKU_UNAVAILABLE -> "Install Shizuku to enable non-root system optimizations."
        }
}
