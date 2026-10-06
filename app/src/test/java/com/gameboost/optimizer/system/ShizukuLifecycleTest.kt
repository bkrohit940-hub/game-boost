package com.gameboost.optimizer.system

import com.gameboost.optimizer.models.ShizukuState
import com.gameboost.optimizer.models.ShizukuStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShizukuLifecycleTest {

    @Test
    fun testNotInstalledState() {
        val status = ShizukuStatus(isInstalled = false, isRunning = false, isAuthorized = false)
        assertEquals(ShizukuState.NOT_INSTALLED, status.state)
        assertFalse(status.isReady)
    }

    @Test
    fun testNotRunningState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = false, isAuthorized = false)
        assertEquals(ShizukuState.NOT_RUNNING, status.state)
        assertFalse(status.isReady)
    }

    @Test
    fun testPermissionRequiredState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = false)
        assertEquals(ShizukuState.PERMISSION_REQUIRED, status.state)
        assertFalse(status.isReady)
    }

    @Test
    fun testAuthorizedState() {
        val status = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true, version = 13)
        assertEquals(ShizukuState.AUTHORIZED, status.state)
        assertTrue(status.isReady)
    }

    @Test
    fun testPermissionRevokedState() {
        // Transition from authorized to revoked
        val initialStatus = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true, version = 13)
        assertTrue(initialStatus.isReady)

        // When permission is revoked by user in Shizuku
        val revokedStatus = initialStatus.copy(isAuthorized = false)
        assertEquals(ShizukuState.PERMISSION_REQUIRED, revokedStatus.state)
        assertFalse(revokedStatus.isReady)
    }

    @Test
    fun testErrorState() {
        val errorStatus = ShizukuStatus(isInstalled = true, isRunning = false, errorMessage = "Binder transaction error")
        assertEquals(ShizukuState.ERROR, errorStatus.state)
        assertFalse(errorStatus.isReady)
    }
}
