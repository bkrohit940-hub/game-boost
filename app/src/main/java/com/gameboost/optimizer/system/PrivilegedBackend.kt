package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShellCommandResult

enum class BackendType(val displayName: String) {
    SHIZUKU("Shizuku"),
    WIRELESS_ADB("Wireless ADB"),
    NONE("No Privileged Connection")
}

/**
 * Unified interface for privileged shell execution backends.
 * Allows transparent routing between Shizuku IPC and direct Wireless Debugging.
 */
interface PrivilegedBackend {
    val type: BackendType
    val isAvailable: Boolean
    val isAuthorized: Boolean
    val isReady: Boolean

    suspend fun executeCommand(command: String): Result<String>
    suspend fun testConnection(): ShellCommandResult
}
