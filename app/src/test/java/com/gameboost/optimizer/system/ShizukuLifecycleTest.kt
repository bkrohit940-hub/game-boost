package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShizukuState
import com.gameboost.optimizer.models.ShizukuStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShizukuLifecycleTest {

    @Test
    fun testShizukuUnavailableState() {
        val status = ShizukuStatus(isInstalled = false, isRunning = false, isAuthorized = false)
        assertEquals(ShizukuState.SHIZUKU_UNAVAILABLE, status.state)
        assertEquals("SHIZUKU UNAVAILABLE", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testShizukuNotRunningState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = false, isAuthorized = false)
        assertEquals(ShizukuState.SHIZUKU_NOT_RUNNING, status.state)
        assertEquals("SHIZUKU NOT RUNNING", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testPermissionRequiredState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = false, wasPreviouslyAuthorized = false)
        assertEquals(ShizukuState.PERMISSION_REQUIRED, status.state)
        assertEquals("PERMISSION REQUIRED", status.title)
        assertFalse(status.isReady)
    }

    @Test
    fun testPermissionRevokedState() {
        // Transition from authorized to revoked
        val initialStatus = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true, wasPreviouslyAuthorized = true, version = 13)
        assertTrue(initialStatus.isReady)

        // When permission is revoked by user in Shizuku
        val revokedStatus = initialStatus.copy(isAuthorized = false)
        assertEquals(ShizukuState.PERMISSION_REVOKED, revokedStatus.state)
        assertEquals("PERMISSION REVOKED", revokedStatus.title)
        assertFalse(revokedStatus.isReady)
    }

    @Test
    fun testShizukuReadyState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true, version = 13)
        assertEquals(ShizukuState.SHIZUKU_READY, status.state)
        assertEquals("SHIZUKU READY", status.title)
        assertTrue(status.isReady)
    }
}
