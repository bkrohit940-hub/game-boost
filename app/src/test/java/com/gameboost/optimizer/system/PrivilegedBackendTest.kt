package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.models.ShizukuStatus
import com.gameboost.optimizer.system.adb.AdbConnectionStatus
import com.gameboost.optimizer.system.adb.WirelessAdbState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivilegedBackendTest {

    private class FakeBackend(
        override val type: BackendType,
        override var isAvailable: Boolean = true,
        override var isAuthorized: Boolean = true,
        override var isReady: Boolean = true
    ) : PrivilegedBackend {
        var lastExecutedCommand: String? = null

        override suspend fun executeCommand(command: String): Result<String> {
            lastExecutedCommand = command
            return Result.success("output")
        }

        override suspend fun testConnection(): ShellCommandResult {
            return ShellCommandResult("id", 0, "uid=2000", "", 10L, true)
        }
    }

    @Test
    fun testPriorityRoutingPrefersShizukuWhenBothReady() {
        val fakeShizuku = FakeBackend(BackendType.SHIZUKU, isReady = true)
        val fakeAdb = FakeBackend(BackendType.WIRELESS_ADB, isReady = true)

        val engine = PrivilegedExecutionEngine(
            shizukuBackend = fakeShizuku,
            wirelessAdbBackend = fakeAdb
        )

        engine.updateStates(
            shizukuStatus = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true),
            adbState = WirelessAdbState(status = AdbConnectionStatus.CONNECTED)
        )

        assertEquals(BackendType.SHIZUKU, engine.systemState.value.activeBackendType)
        assertTrue(engine.systemState.value.isPrivilegedReady)
        assertEquals(fakeShizuku, engine.activeBackend)
    }

    @Test
    fun testPriorityRoutingFallsBackToWirelessAdbWhenShizukuNotReady() {
        val fakeShizuku = FakeBackend(BackendType.SHIZUKU, isReady = false)
        val fakeAdb = FakeBackend(BackendType.WIRELESS_ADB, isReady = true)

        val engine = PrivilegedExecutionEngine(
            shizukuBackend = fakeShizuku,
            wirelessAdbBackend = fakeAdb
        )

        engine.updateStates(
            shizukuStatus = ShizukuStatus(isInstalled = true, isRunning = false, isAuthorized = false),
            adbState = WirelessAdbState(status = AdbConnectionStatus.CONNECTED)
        )

        assertEquals(BackendType.WIRELESS_ADB, engine.systemState.value.activeBackendType)
        assertTrue(engine.systemState.value.isPrivilegedReady)
        assertEquals(fakeAdb, engine.activeBackend)
    }

    @Test
    fun testPriorityRoutingNoneWhenNeitherReady() {
        val fakeShizuku = FakeBackend(BackendType.SHIZUKU, isReady = false)
        val fakeAdb = FakeBackend(BackendType.WIRELESS_ADB, isReady = false)

        val engine = PrivilegedExecutionEngine(
            shizukuBackend = fakeShizuku,
            wirelessAdbBackend = fakeAdb
        )

        engine.updateStates(
            shizukuStatus = ShizukuStatus(isInstalled = false),
            adbState = WirelessAdbState(status = AdbConnectionStatus.DISCONNECTED)
        )

        assertEquals(BackendType.NONE, engine.systemState.value.activeBackendType)
        assertFalse(engine.systemState.value.isPrivilegedReady)
        assertNull(engine.activeBackend)
    }

    @Test
    fun testCommandAllowlistRejectsArbitraryCommands() {
        assertFalse(CommandAllowlist.isAllowed("rm -rf /"))
        assertFalse(CommandAllowlist.isAllowed("reboot"))
        assertFalse(CommandAllowlist.isAllowed("cat /data/system/users/0/settings_system.xml"))
        assertFalse(CommandAllowlist.isAllowed("pm uninstall com.tencent.ig"))

        // Legitimate allowlisted commands must be allowed
        assertTrue(CommandAllowlist.isAllowed("id"))
        assertTrue(CommandAllowlist.isAllowed("settings put system peak_refresh_rate 120.0"))
        assertTrue(CommandAllowlist.isAllowed("settings put global window_animation_scale 0.5"))
        assertTrue(CommandAllowlist.isAllowed("cmd power set-mode 1"))
    }
}
