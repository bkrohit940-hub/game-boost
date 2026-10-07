package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShellCommandResult
import com.gameboost.optimizer.models.ShizukuState
import com.gameboost.optimizer.models.ShizukuStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShizukuLifecycleTest {

    @Test
    fun testShizukuNotInstalledState() {
        val status = ShizukuStatus(isInstalled = false, isRunning = false, isAuthorized = false)
        assertEquals(ShizukuState.NotInstalled, status.state)
        assertEquals("SHIZUKU UNAVAILABLE", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testShizukuInstalledServiceStoppedState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = false, isAuthorized = false)
        assertEquals(ShizukuState.InstalledServiceStopped, status.state)
        assertEquals("SHIZUKU NOT RUNNING", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testServiceRunningPermissionMissingState() {
        val status = ShizukuStatus(
            isInstalled = true,
            isRunning = true,
            isAuthorized = false,
            wasPreviouslyAuthorized = false
        )
        assertEquals(ShizukuState.ServiceRunningPermissionMissing, status.state)
        assertEquals("PERMISSION REQUIRED", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testPermissionRevokedState() {
        // Transition from authorized to revoked
        val initialStatus = ShizukuStatus(
            isInstalled = true,
            isRunning = true,
            isAuthorized = true,
            wasPreviouslyAuthorized = true,
            version = 13
        )

        // When permission is revoked by user in Shizuku
        val revokedStatus = initialStatus.copy(isAuthorized = false)
        assertEquals(ShizukuState.ServiceRunningPermissionMissing, revokedStatus.state)
        assertEquals("PERMISSION REVOKED", revokedStatus.title)
        assertFalse(revokedStatus.isReady)
    }

    @Test
    fun testPermissionGrantedState() {
        val status = ShizukuStatus(
            isInstalled = true,
            isRunning = true,
            isAuthorized = true,
            isBinderConnected = false,
            lastTestResult = null
        )
        assertEquals(ShizukuState.PermissionGranted, status.state)
        assertEquals("SHIZUKU READY", status.title)
    }

    @Test
    fun testBinderConnectedState() {
        val status = ShizukuStatus(
            isInstalled = true,
            isRunning = true,
            isAuthorized = true,
            isBinderConnected = true,
            lastTestResult = null
        )
        assertEquals(ShizukuState.BinderConnected, status.state)
        assertEquals("SHIZUKU READY", status.title)
    }

    @Test
    fun testShellVerifiedState() {
        val testResult = ShellCommandResult(
            command = "id",
            exitCode = 0,
            stdout = "uid=2000(shell) gid=2000(shell)",
            stderr = "",
            durationMs = 12L,
            isSuccess = true
        )
        val status = ShizukuStatus(
            isInstalled = true,
            isRunning = true,
            isAuthorized = true,
            isBinderConnected = true,
            version = 13,
            uid = 2000,
            isShellTested = true,
            isShellWorking = true,
            lastTestResult = testResult
        )
        assertTrue(status.state is ShizukuState.ShellVerified)
        assertEquals("SHIZUKU CONNECTED", status.title)
        assertTrue(status.isReady)
        val verifiedState = status.state as ShizukuState.ShellVerified
        assertEquals(12L, verifiedState.durationMs)
        assertEquals(2000, verifiedState.uid)
    }

    @Test
    fun testBinderDeadState() {
        val status = ShizukuStatus(
            isInstalled = true,
            isRunning = false,
            isBinderDead = true,
            isAuthorized = true
        )
        assertEquals(ShizukuState.BinderDead, status.state)
        assertEquals("SHIZUKU BINDER DEAD", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testShellExecutionFailedState() {
        val failedResult = ShellCommandResult(
            command = "id",
            exitCode = 1,
            stdout = "",
            stderr = "Permission denied",
            durationMs = 5L,
            isSuccess = false,
            failureReason = "Permission denied"
        )
        val status = ShizukuStatus(
            isInstalled = true,
            isRunning = true,
            isAuthorized = true,
            isBinderConnected = true,
            isShellTested = true,
            isShellWorking = false,
            lastTestResult = failedResult
        )
        assertTrue(status.state is ShizukuState.ShellFailed)
        assertEquals("SHELL EXECUTION FAILED", status.title)
        assertFalse(status.isReady)
        val failedState = status.state as ShizukuState.ShellFailed
        assertEquals("Permission denied", failedState.reason)
        assertEquals(1, failedState.exitCode)
    }
}
