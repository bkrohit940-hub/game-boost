package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShellCommandResult

/**
 * PrivilegedBackend implementation routing commands through official Shizuku IPC.
 */
class ShizukuBackend(
    val shizukuManager: ShizukuManager
) : PrivilegedBackend {
    override val type: BackendType = BackendType.SHIZUKU

    override val isAvailable: Boolean
        get() = shizukuManager.isInstalled()

    override val isAuthorized: Boolean
        get() = shizukuManager.hasPermission()

    override val isReady: Boolean
        get() = shizukuManager.getStatus().isReady

    override suspend fun executeCommand(command: String): Result<String> {
        return shizukuManager.executeCommand(command)
    }

    override suspend fun testConnection(): ShellCommandResult {
        return shizukuManager.testConnection()
    }
}
