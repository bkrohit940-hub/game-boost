package com.gameboost.optimizer.system

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.gameboost.optimizer.models.ShizukuStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Dedicated abstraction layer for all Shizuku interactions.
 * Isolates Shizuku API from the rest of the application.
 */
class ShizukuManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "ShizukuManager"
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        const val REQUEST_CODE_PERMISSION = 9001
    }

    private val _statusFlow = MutableStateFlow(ShizukuStatus())
    val statusFlow: StateFlow<ShizukuStatus> = _statusFlow.asStateFlow()

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        Log.d(TAG, "Shizuku binder received")
        refreshStatus()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        Log.w(TAG, "Shizuku binder died")
        refreshStatus()
    }

    private val requestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == REQUEST_CODE_PERMISSION) {
                Log.d(TAG, "Shizuku permission result: $grantResult")
                refreshStatus()
            }
        }

    init {
        try {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            Log.e(TAG, "Error registering Shizuku listeners", e)
        }
        refreshStatus()
    }

    fun isInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Throwable) {
            false
        }
    }

    fun isRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (_: Throwable) {
            false
        }
    }

    fun hasPermission(): Boolean {
        return try {
            if (!isRunning()) return false
            if (Shizuku.isPreV11()) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (_: Throwable) {
            false
        }
    }

    fun requestPermission() {
        if (!isRunning()) {
            Log.w(TAG, "Cannot request permission: Shizuku service is not running")
            return
        }
        try {
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(REQUEST_CODE_PERMISSION)
            } else {
                refreshStatus()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to request Shizuku permission", e)
        }
    }

    var wasPreviouslyAuthorized: Boolean = false

    fun setPreviouslyAuthorized(authorized: Boolean) {
        if (authorized && !wasPreviouslyAuthorized) {
            wasPreviouslyAuthorized = true
            refreshStatus()
        }
    }

    fun getStatus(): ShizukuStatus {
        val installed = isInstalled()
        val running = isRunning()
        var authorized = false
        var version = 0
        var uid = -1
        var error: String? = null

        if (running) {
            try {
                version = Shizuku.getVersion()
                uid = Shizuku.getUid()
                authorized = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                if (authorized) {
                    wasPreviouslyAuthorized = true
                }
            } catch (e: Throwable) {
                error = e.localizedMessage
                Log.e(TAG, "Error querying Shizuku metadata", e)
            }
        }

        return ShizukuStatus(
            isInstalled = installed,
            isRunning = running,
            isAuthorized = authorized,
            wasPreviouslyAuthorized = wasPreviouslyAuthorized,
            version = version,
            uid = uid,
            errorMessage = error
        )
    }

    fun refreshStatus() {
        _statusFlow.value = getStatus()
    }

    /**
     * Executes an allowlisted shell command via Shizuku.
     * Rejects any command not matching the security allowlist.
     */
    suspend fun executeCommand(command: String): Result<String> = withContext(Dispatchers.IO) {
        if (!CommandAllowlist.isAllowed(command)) {
            val err = "Security violation: Command rejected by allowlist: '$command'"
            Log.e(TAG, err)
            return@withContext Result.failure(SecurityException(err))
        }

        if (!hasPermission()) {
            val err = "Cannot execute command: Shizuku is not running or not authorized"
            Log.w(TAG, err)
            return@withContext Result.failure(IllegalStateException(err))
        }

        try {
            val newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcessMethod.isAccessible = true
            val process = newProcessMethod.invoke(
                null,
                arrayOf("sh", "-c", command),
                null,
                null
            ) as java.lang.Process

            val output = StringBuilder()
            val error = StringBuilder()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))


            reader.useLines { lines -> lines.forEach { output.appendLine(it) } }
            errorReader.useLines { lines -> lines.forEach { error.appendLine(it) } }

            val finished = process.waitFor(5, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                return@withContext Result.failure(RuntimeException("Command timed out: $command"))
            }

            val exitCode = process.exitValue()
            if (exitCode == 0) {
                Result.success(output.toString().trim())
            } else {
                val errMsg = error.toString().trim().ifEmpty { "Exit code $exitCode" }
                Result.failure(RuntimeException("Command failed ($exitCode): $errMsg"))
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed executing Shizuku command: $command", e)
            Result.failure(e)
        }
    }

    fun cleanup() {
        try {
            Shizuku.removeBinderReceivedListener(binderReceivedListener)
            Shizuku.removeBinderDeadListener(binderDeadListener)
            Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            Log.w(TAG, "Error cleaning up Shizuku listeners", e)
        }
    }
}
