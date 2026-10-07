package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.system.adb.AdbConnectionManager

/**
 * PrivilegedBackend implementation routing commands directly through Wireless Debugging over TLS.
 */
class WirelessAdbBackend(
    val adbManager: AdbConnectionManager
) : PrivilegedBackend {
    override val type: BackendType = BackendType.WIRELESS_ADB

    override val isAvailable: Boolean
        get() = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R // Android 11+ Wireless Debugging

    override val isAuthorized: Boolean
        get() = adbManager.isConnected

    override val isReady: Boolean
        get() = adbManager.isConnected

    override suspend fun executeCommand(command: String): Result<String> {
        return adbManager.executeCommand(command)
    }

    override suspend fun testConnection(): ShellCommandResult {
        return adbManager.testConnection()
    }
}
