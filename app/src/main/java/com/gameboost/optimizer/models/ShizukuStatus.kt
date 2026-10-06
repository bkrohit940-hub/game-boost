package com.gameboost.optimizer.models

enum class ShizukuState {
    NOT_INSTALLED,
    NOT_RUNNING,
    PERMISSION_REQUIRED,
    AUTHORIZED,
    ERROR
}

/**
 * Encapsulates the runtime status of Shizuku service and permission.
 */
data class ShizukuStatus(
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false,
    val isAuthorized: Boolean = false,
    val version: Int = 0,
    val uid: Int = -1,
    val errorMessage: String? = null
) {
    val state: ShizukuState
        get() = when {
            errorMessage != null && !isAuthorized -> ShizukuState.ERROR
            !isInstalled -> ShizukuState.NOT_INSTALLED
            !isRunning -> ShizukuState.NOT_RUNNING
            !isAuthorized -> ShizukuState.PERMISSION_REQUIRED
            else -> ShizukuState.AUTHORIZED
        }

    val isReady: Boolean
        get() = state == ShizukuState.AUTHORIZED

    val summaryText: String
        get() = when (state) {
            ShizukuState.NOT_INSTALLED -> "Not Installed"
            ShizukuState.NOT_RUNNING -> "Installed (Service Stopped)"
            ShizukuState.PERMISSION_REQUIRED -> "Service Running (Permission Required)"
            ShizukuState.AUTHORIZED -> "Authorized (v$version)"
            ShizukuState.ERROR -> errorMessage ?: "Shizuku Error"
        }
}
