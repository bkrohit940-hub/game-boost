package com.gameboost.optimizer.models

/**
 * Sealed Shizuku state machine representing the verified 8-state IPC lifecycle:
 * 1. NotInstalled
 * 2. InstalledServiceStopped
 * 3. ServiceRunningPermissionMissing
 * 4. PermissionGranted
 * 5. BinderConnected
 * 6. ShellVerified
 * 7. BinderDead
 * 8. ShellFailed
 */
sealed interface ShizukuState {
    data object NotInstalled : ShizukuState
    data object InstalledServiceStopped : ShizukuState
    data object ServiceRunningPermissionMissing : ShizukuState
    data object PermissionGranted : ShizukuState
    data object BinderConnected : ShizukuState
    data class ShellVerified(val durationMs: Long = 0L, val uid: Int = 2000) : ShizukuState
    data object BinderDead : ShizukuState
    data class ShellFailed(val reason: String, val exitCode: Int = -1) : ShizukuState

    companion object {
        // Compatibility aliases for legacy tests and callers
        val NOT_INSTALLED: ShizukuState = NotInstalled
        val NOT_RUNNING: ShizukuState = InstalledServiceStopped
        val AUTHORIZED: ShizukuState = ShellVerified(0L, 2000)
        val ERROR: ShizukuState = ShellFailed("Error", -1)
        val SHIZUKU_READY: ShizukuState = ShellVerified(0L, 2000)
        val SHIZUKU_NOT_RUNNING: ShizukuState = InstalledServiceStopped
        val PERMISSION_REQUIRED: ShizukuState = ServiceRunningPermissionMissing
        val PERMISSION_REVOKED: ShizukuState = ServiceRunningPermissionMissing
        val SHIZUKU_UNAVAILABLE: ShizukuState = NotInstalled
    }
}

/**
 * Granular 8-state connection lifecycle enum for UI displays and legacy references.
 */
enum class ShizukuDetailedState {
    NOT_INSTALLED,
    SERVICE_STOPPED,
    SERVICE_RUNNING,
    PERMISSION_REQUIRED,
    PERMISSION_GRANTED,
    BINDER_CONNECTED,
    SHELL_WORKING,
    SHELL_EXECUTION_FAILED,
    DISCONNECTED
}

/**
 * Structured result of an executed privileged shell command.
 */
data class ShellCommandResult(
    val command: String,
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val durationMs: Long,
    val isSuccess: Boolean,
    val failureReason: String? = null
)

/**
 * Encapsulates the verified runtime status of Shizuku service, IPC binder, and authorization.
 */
data class ShizukuStatus(
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false,
    val isBinderConnected: Boolean = false,
    val isAuthorized: Boolean = false,
    val wasPreviouslyAuthorized: Boolean = false,
    val isBinderDead: Boolean = false,
    val version: Int = 0,
    val uid: Int = -1,
    val isShellTested: Boolean = false,
    val isShellWorking: Boolean = false,
    val lastTestResult: ShellCommandResult? = null,
    val errorMessage: String? = null
) {
    val state: ShizukuState
        get() = when {
            !isInstalled -> ShizukuState.NotInstalled
            isBinderDead -> ShizukuState.BinderDead
            !isRunning -> ShizukuState.InstalledServiceStopped
            !isAuthorized -> ShizukuState.ServiceRunningPermissionMissing
            lastTestResult != null && !lastTestResult.isSuccess ->
                ShizukuState.ShellFailed(lastTestResult.failureReason ?: lastTestResult.stderr, lastTestResult.exitCode)
            lastTestResult != null && lastTestResult.isSuccess ->
                ShizukuState.ShellVerified(lastTestResult.durationMs, if (uid > 0) uid else 2000)
            isShellTested && !isShellWorking ->
                ShizukuState.ShellFailed("Shell verification failed", -1)
            isShellTested && isShellWorking ->
                ShizukuState.ShellVerified(lastTestResult?.durationMs ?: 0L, if (uid > 0) uid else 2000)
            isBinderConnected -> ShizukuState.BinderConnected
            else -> ShizukuState.PermissionGranted
        }

    val detailedState: ShizukuDetailedState
        get() = when (state) {
            is ShizukuState.NotInstalled -> ShizukuDetailedState.NOT_INSTALLED
            is ShizukuState.InstalledServiceStopped,
            is ShizukuState.BinderDead -> ShizukuDetailedState.SERVICE_STOPPED
            is ShizukuState.ServiceRunningPermissionMissing -> ShizukuDetailedState.PERMISSION_REQUIRED
            is ShizukuState.PermissionGranted -> ShizukuDetailedState.PERMISSION_GRANTED
            is ShizukuState.BinderConnected -> ShizukuDetailedState.BINDER_CONNECTED
            is ShizukuState.ShellVerified -> ShizukuDetailedState.SHELL_WORKING
            is ShizukuState.ShellFailed -> ShizukuDetailedState.SHELL_EXECUTION_FAILED
        }

    val isReady: Boolean
        get() = state is ShizukuState.ShellVerified || (isAuthorized && isRunning && (!isShellTested || isShellWorking))

    val title: String
        get() = when (val s = state) {
            is ShizukuState.NotInstalled -> "SHIZUKU UNAVAILABLE"
            is ShizukuState.InstalledServiceStopped -> "SHIZUKU NOT RUNNING"
            is ShizukuState.BinderDead -> "SHIZUKU BINDER DEAD"
            is ShizukuState.ServiceRunningPermissionMissing -> {
                if (wasPreviouslyAuthorized) "PERMISSION REVOKED" else "PERMISSION REQUIRED"
            }
            is ShizukuState.PermissionGranted,
            is ShizukuState.BinderConnected -> "SHIZUKU READY"
            is ShizukuState.ShellVerified -> "SHIZUKU CONNECTED"
            is ShizukuState.ShellFailed -> "SHELL EXECUTION FAILED"
        }

    val summaryText: String
        get() = when (val s = state) {
            is ShizukuState.NotInstalled -> "Shizuku is not installed on this device."
            is ShizukuState.InstalledServiceStopped -> "Service stopped. Start Shizuku via Wireless Debugging or ADB."
            is ShizukuState.BinderDead -> "Shizuku service binder died. Service terminated or killed."
            is ShizukuState.ServiceRunningPermissionMissing -> {
                if (wasPreviouslyAuthorized) "Permission revoked. Re-grant access in Shizuku app."
                else "Service active. Permission authorization required."
            }
            is ShizukuState.PermissionGranted,
            is ShizukuState.BinderConnected -> "Authorized & Active (API v$version)"
            is ShizukuState.ShellVerified -> "Binder & Shell Verified (API v$version • ${s.durationMs}ms)"
            is ShizukuState.ShellFailed -> "Shell execution test failed: ${s.reason} (exit code ${s.exitCode})"
        }

    val actionPrompt: String
        get() = when (state) {
            is ShizukuState.NotInstalled -> "Install Shizuku to enable non-root system optimizations."
            is ShizukuState.InstalledServiceStopped -> "Open the Shizuku app and start the service."
            is ShizukuState.BinderDead -> "Restart the Shizuku service via Wireless Debugging or ADB."
            is ShizukuState.ServiceRunningPermissionMissing -> {
                if (wasPreviouslyAuthorized) "Open Shizuku > Authorized Applications and re-enable Game Boost."
                else "Grant Shizuku permission to allow Game Boost optimization."
            }
            is ShizukuState.PermissionGranted -> "Establishing IPC connection to Shizuku privileged shell..."
            is ShizukuState.BinderConnected -> "Run diagnostic test to verify shell commands."
            is ShizukuState.ShellVerified -> "All system and display controls are ready."
            is ShizukuState.ShellFailed -> "Check Shizuku daemon status and SELinux configuration."
        }
}
