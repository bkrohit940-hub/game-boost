package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.adb.AdbConnectionManager
import com.gameboost.optimizer.system.adb.WirelessAdbState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrivilegedSystemState(
    val activeBackendType: BackendType = BackendType.NONE,
    val isPrivilegedReady: Boolean = false,
    val shizukuStatus: ShizukuStatus = ShizukuStatus(),
    val wirelessAdbState: WirelessAdbState = WirelessAdbState(),
    val lastExecutionResult: ShellCommandResult? = null
)

/**
 * High-performance orchestrator for privileged shell operations.
 * Enforces priority order: Shizuku -> Wireless Debugging -> Disconnected.
 */
class PrivilegedExecutionEngine(
    val shizukuBackend: PrivilegedBackend,
    val wirelessAdbBackend: PrivilegedBackend
) {
    private val _systemState = MutableStateFlow(PrivilegedSystemState())
    val systemState: StateFlow<PrivilegedSystemState> = _systemState.asStateFlow()

    val activeBackend: PrivilegedBackend?
        get() = when {
            shizukuBackend.isReady -> shizukuBackend
            wirelessAdbBackend.isReady -> wirelessAdbBackend
            else -> null
        }

    val isReady: Boolean
        get() = activeBackend != null

    fun updateStates(shizukuStatus: ShizukuStatus, adbState: WirelessAdbState) {
        val active = when {
            shizukuStatus.isReady -> BackendType.SHIZUKU
            adbState.status == com.gameboost.optimizer.system.adb.AdbConnectionStatus.CONNECTED -> BackendType.WIRELESS_ADB
            else -> BackendType.NONE
        }

        _systemState.value = _systemState.value.copy(
            activeBackendType = active,
            isPrivilegedReady = active != BackendType.NONE,
            shizukuStatus = shizukuStatus,
            wirelessAdbState = adbState
        )
    }

    suspend fun executeCommand(command: String): Result<String> {
        val backend = activeBackend
            ?: return Result.failure(IllegalStateException("No privileged connection available (Shizuku not running and Wireless ADB not connected)"))
        return backend.executeCommand(command)
    }

    suspend fun testActiveBackend(): ShellCommandResult {
        val backend = activeBackend
        if (backend == null) {
            val emptyResult = ShellCommandResult(
                command = "id",
                exitCode = -1,
                stdout = "",
                stderr = "Neither Shizuku nor Wireless ADB is connected",
                durationMs = 0L,
                isSuccess = false,
                failureReason = "No backend active"
            )
            _systemState.value = _systemState.value.copy(lastExecutionResult = emptyResult)
            return emptyResult
        }
        val result = backend.testConnection()
        _systemState.value = _systemState.value.copy(lastExecutionResult = result)
        return result
    }
}
