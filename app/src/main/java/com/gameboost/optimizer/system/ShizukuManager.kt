package com.gameboost.optimizer.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.service.IShizukuUserService
import com.gameboost.optimizer.service.ShizukuUserService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import rikka.shizuku.Shizuku

/**
 * Dedicated abstraction layer for all Shizuku interactions.
 * Implements official Shizuku API with robust IPC lifecycle tracking,
 * verified binder diagnostics, official UserService (AIDL) command execution,
 * and zero private-API reflection.
 */
class ShizukuManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "ShizukuManager"
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        const val REQUEST_CODE_PERMISSION = 9001
        private const val BIND_TIMEOUT_MS = 3500L
    }

    private val _statusFlow = MutableStateFlow(ShizukuStatus())
    val statusFlow: StateFlow<ShizukuStatus> = _statusFlow.asStateFlow()

    private var lastTestResult: ShellCommandResult? = null
    private var wasPreviouslyAuthorized: Boolean = false
    private var isBinderDeadFlag: Boolean = false

    private val userServiceLock = Any()
    private var userService: IShizukuUserService? = null
    private var userServiceConnectingDeferred: CompletableDeferred<IShizukuUserService>? = null

    private val userServiceArgs by lazy {
        Shizuku.UserServiceArgs(ComponentName(context.packageName, ShizukuUserService::class.java.name))
            .daemon(false)
            .processNameSuffix("privileged_service")
            .debuggable(false)
            .version(1)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "Shizuku UserService connected successfully: $name")
            val proxy = IShizukuUserService.Stub.asInterface(service)
            synchronized(userServiceLock) {
                userService = proxy
                userServiceConnectingDeferred?.complete(proxy)
                userServiceConnectingDeferred = null
            }

            try {
                service?.linkToDeath({
                    Log.w(TAG, "Shizuku UserService IBinder died")
                    synchronized(userServiceLock) {
                        userService = null
                    }
                    refreshStatus()
                }, 0)
            } catch (e: Throwable) {
                Log.w(TAG, "Failed linking to UserService death", e)
            }

            refreshStatus()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w(TAG, "Shizuku UserService disconnected: $name")
            synchronized(userServiceLock) {
                userService = null
            }
            refreshStatus()
        }
    }

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        isBinderDeadFlag = false
        Log.i(TAG, "Shizuku binder received from service daemon. Binder is alive: ${Shizuku.pingBinder()}")
        refreshStatus()
        if (hasPermission()) {
            bindUserServiceSafely()
        }
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        Log.w(TAG, "Shizuku binder died. Service terminated or killed.")
        isBinderDeadFlag = true
        synchronized(userServiceLock) {
            userService = null
            userServiceConnectingDeferred?.cancel()
            userServiceConnectingDeferred = null
        }
        lastTestResult = null
        refreshStatus()
    }

    private val requestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == REQUEST_CODE_PERMISSION) {
                val granted = grantResult == PackageManager.PERMISSION_GRANTED
                Log.i(TAG, "Shizuku permission response received: granted=$granted (resultCode=$grantResult)")
                if (granted) {
                    wasPreviouslyAuthorized = true
                    bindUserServiceSafely()
                }
                refreshStatus()
            }
        }

    init {
        try {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
            Log.d(TAG, "Registered Shizuku lifecycle listeners successfully")
        } catch (e: Throwable) {
            Log.e(TAG, "Error registering Shizuku lifecycle listeners", e)
        }
        refreshStatus()
        if (hasPermission()) {
            bindUserServiceSafely()
        }
    }

    fun isInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (e: Throwable) {
            Log.w(TAG, "Exception querying Shizuku package info: ${e.message}")
            false
        }
    }

    fun isRunning(): Boolean {
        return try {
            !isBinderDeadFlag && Shizuku.pingBinder()
        } catch (e: Throwable) {
            Log.w(TAG, "pingBinder threw exception: ${e.message}")
            false
        }
    }

    fun hasPermission(): Boolean {
        return try {
            if (!isRunning()) return false
            if (Shizuku.isPreV11()) {
                Log.w(TAG, "Shizuku pre-v11 is deprecated and unsupported")
                false
            } else {
                val granted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    wasPreviouslyAuthorized = true
                }
                granted
            }
        } catch (e: Throwable) {
            Log.w(TAG, "checkSelfPermission failed: ${e.message}")
            false
        }
    }

    fun requestPermission() {
        if (!isRunning()) {
            Log.w(TAG, "Cannot request permission: Shizuku binder is not active")
            return
        }
        try {
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Requesting Shizuku authorization dialog via API")
                Shizuku.requestPermission(REQUEST_CODE_PERMISSION)
            } else {
                Log.i(TAG, "Permission already granted, refreshing status and binding UserService")
                wasPreviouslyAuthorized = true
                bindUserServiceSafely()
                refreshStatus()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to request Shizuku permission", e)
        }
    }

    private fun bindUserServiceSafely() {
        try {
            if (isRunning() && hasPermission() && userService == null) {
                Log.d(TAG, "Binding Shizuku UserService...")
                Shizuku.bindUserService(userServiceArgs, userServiceConnection)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "bindUserServiceSafely encountered error: ${e.message}")
        }
    }

    private suspend fun getOrBindUserService(): IShizukuUserService? {
        if (!isRunning() || !hasPermission()) {
            return null
        }
        var deferredToAwait: CompletableDeferred<IShizukuUserService>? = null
        synchronized(userServiceLock) {
            userService?.let { return it }
            val existingDeferred = userServiceConnectingDeferred
            if (existingDeferred != null && existingDeferred.isActive) {
                deferredToAwait = existingDeferred
            } else {
                val newDeferred = CompletableDeferred<IShizukuUserService>()
                userServiceConnectingDeferred = newDeferred
                deferredToAwait = newDeferred
                try {
                    Shizuku.bindUserService(userServiceArgs, userServiceConnection)
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed binding Shizuku UserService", e)
                    userServiceConnectingDeferred = null
                    return null
                }
            }
        }

        return try {
            withTimeoutOrNull(BIND_TIMEOUT_MS) {
                deferredToAwait?.await()
            } ?: synchronized(userServiceLock) { userService }
        } catch (e: Throwable) {
            Log.w(TAG, "Timeout or error awaiting Shizuku UserService", e)
            synchronized(userServiceLock) { userService }
        }
    }

    fun openShizukuApp(): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to open Shizuku application", e)
            false
        }
    }

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
                Log.e(TAG, "Error querying Shizuku metadata: ${e.message}")
            }
        }

        val isBound = synchronized(userServiceLock) { userService != null }

        return ShizukuStatus(
            isInstalled = installed,
            isRunning = running,
            isBinderConnected = isBound,
            isAuthorized = authorized,
            wasPreviouslyAuthorized = wasPreviouslyAuthorized,
            isBinderDead = isBinderDeadFlag,
            version = version,
            uid = uid,
            isShellTested = lastTestResult != null,
            isShellWorking = lastTestResult?.isSuccess == true,
            lastTestResult = lastTestResult,
            errorMessage = error
        )
    }

    fun refreshStatus() {
        _statusFlow.value = getStatus()
    }

    /**
     * Executes a non-destructive read-only identity query ("id") to physically verify
     * Shizuku's remote binder and shell execution subsystem via the official UserService.
     */
    suspend fun testConnection(): ShellCommandResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val testCommand = "id"

        if (!isRunning()) {
            val result = ShellCommandResult(
                command = testCommand,
                exitCode = -1,
                stdout = "",
                stderr = "Shizuku service is not running or binder is disconnected",
                durationMs = System.currentTimeMillis() - startTime,
                isSuccess = false,
                failureReason = "Binder disconnected"
            )
            lastTestResult = result
            refreshStatus()
            return@withContext result
        }

        if (!hasPermission()) {
            val result = ShellCommandResult(
                command = testCommand,
                exitCode = -1,
                stdout = "",
                stderr = "Game Boost has not been granted Shizuku permission",
                durationMs = System.currentTimeMillis() - startTime,
                isSuccess = false,
                failureReason = "Permission required"
            )
            lastTestResult = result
            refreshStatus()
            return@withContext result
        }

        val service = getOrBindUserService()
        if (service == null) {
            val result = ShellCommandResult(
                command = testCommand,
                exitCode = -1,
                stdout = "",
                stderr = "Failed to bind Shizuku UserService IPC channel",
                durationMs = System.currentTimeMillis() - startTime,
                isSuccess = false,
                failureReason = "UserService binding failed"
            )
            lastTestResult = result
            refreshStatus()
            return@withContext result
        }

        try {
            val bundle = service.executeCommand(testCommand)
            val exitCode = bundle.getInt("exitCode", -1)
            val stdout = bundle.getString("stdout", "").trim()
            val stderr = bundle.getString("stderr", "").trim()
            val duration = bundle.getLong("durationMs", System.currentTimeMillis() - startTime)
            val isSuccess = bundle.getBoolean("isSuccess", exitCode == 0)

            val result = ShellCommandResult(
                command = testCommand,
                exitCode = exitCode,
                stdout = stdout,
                stderr = stderr,
                durationMs = duration,
                isSuccess = isSuccess,
                failureReason = if (!isSuccess) stderr.ifEmpty { "Exit code $exitCode" } else null
            )
            lastTestResult = result
            refreshStatus()
            Log.i(TAG, "Shizuku diagnostic test completed: exit=$exitCode in ${duration}ms, out='$stdout'")
            result
        } catch (e: Throwable) {
            val duration = System.currentTimeMillis() - startTime
            Log.e(TAG, "Shizuku diagnostic test failed with exception", e)
            val result = ShellCommandResult(
                command = testCommand,
                exitCode = -1,
                stdout = "",
                stderr = e.localizedMessage ?: "Exception during execution",
                durationMs = duration,
                isSuccess = false,
                failureReason = e.message
            )
            lastTestResult = result
            refreshStatus()
            result
        }
    }

    /**
     * Executes an allowlisted shell command via Shizuku's official UserService.
     * Rejects any command not matching the security allowlist.
     * Uses zero reflection.
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

        val service = getOrBindUserService()
        if (service == null) {
            val err = "Shizuku UserService is not available or failed to bind"
            Log.e(TAG, err)
            return@withContext Result.failure(IllegalStateException(err))
        }

        try {
            val bundle = service.executeCommand(command)
            val exitCode = bundle.getInt("exitCode", -1)
            val stdout = bundle.getString("stdout", "").trim()
            val stderr = bundle.getString("stderr", "").trim()

            if (exitCode == 0) {
                Result.success(stdout)
            } else {
                val errMsg = stderr.ifEmpty { "Exit code $exitCode" }
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
            try {
                Shizuku.unbindUserService(userServiceArgs, userServiceConnection, true)
            } catch (_: Throwable) {}
            Log.d(TAG, "Cleaned up Shizuku lifecycle listeners and unbind UserService")
        } catch (e: Throwable) {
            Log.w(TAG, "Error cleaning up Shizuku listeners", e)
        }
    }
}
