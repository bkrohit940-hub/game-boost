package com.gameboost.optimizer.service

import android.os.Bundle
import android.util.Log
import com.gameboost.optimizer.system.CommandAllowlist
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/**
 * Official Shizuku UserService implementation.
 * Runs inside the Shizuku elevated shell process (UID 2000).
 * Executes strictly allowlisted commands with real execution measurement.
 */
class ShizukuUserService : IShizukuUserService.Stub {

    constructor() : super()
    constructor(context: android.content.Context) : super()

    companion object {
        private const val TAG = "GB_ShizukuUserService"
        private const val COMMAND_TIMEOUT_SECONDS = 5L
    }

    override fun destroy() {
        Log.i(TAG, "Shizuku UserService destroy requested")
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    override fun executeCommand(command: String): Bundle {
        val bundle = Bundle()
        val startTime = System.currentTimeMillis()

        if (!CommandAllowlist.isAllowed(command)) {
            val err = "Security violation: Command rejected by allowlist: '$command'"
            Log.e(TAG, err)
            bundle.putInt("exitCode", -1)
            bundle.putString("stdout", "")
            bundle.putString("stderr", err)
            bundle.putLong("durationMs", System.currentTimeMillis() - startTime)
            bundle.putBoolean("isSuccess", false)
            return bundle
        }

        try {
            val process = ProcessBuilder("sh", "-c", command)
                .redirectErrorStream(false)
                .start()

            val stdoutBuilder = StringBuilder()
            val stderrBuilder = StringBuilder()

            val outThread = Thread {
                try {
                    BufferedReader(InputStreamReader(process.inputStream)).useLines { lines ->
                        lines.forEach { stdoutBuilder.appendLine(it) }
                    }
                } catch (_: Throwable) {}
            }

            val errThread = Thread {
                try {
                    BufferedReader(InputStreamReader(process.errorStream)).useLines { lines ->
                        lines.forEach { stderrBuilder.appendLine(it) }
                    }
                } catch (_: Throwable) {}
            }

            outThread.start()
            errThread.start()

            val finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            val duration = System.currentTimeMillis() - startTime

            if (!finished) {
                process.destroyForcibly()
                bundle.putInt("exitCode", -1)
                bundle.putString("stdout", stdoutBuilder.toString().trim())
                bundle.putString("stderr", "Execution timed out after ${COMMAND_TIMEOUT_SECONDS}s")
                bundle.putLong("durationMs", duration)
                bundle.putBoolean("isSuccess", false)
                return bundle
            }

            outThread.join(500)
            errThread.join(500)

            val exitCode = process.exitValue()
            val stdOut = stdoutBuilder.toString().trim()
            val stdErr = stderrBuilder.toString().trim()

            bundle.putInt("exitCode", exitCode)
            bundle.putString("stdout", stdOut)
            bundle.putString("stderr", stdErr)
            bundle.putLong("durationMs", duration)
            bundle.putBoolean("isSuccess", exitCode == 0)
            return bundle
        } catch (e: Throwable) {
            val duration = System.currentTimeMillis() - startTime
            Log.e(TAG, "Exception executing shell command: $command", e)
            bundle.putInt("exitCode", -1)
            bundle.putString("stdout", "")
            bundle.putString("stderr", e.localizedMessage ?: "Unknown execution error")
            bundle.putLong("durationMs", duration)
            bundle.putBoolean("isSuccess", false)
            return bundle
        }
    }
}
