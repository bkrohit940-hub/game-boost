package com.gameboost.optimizer.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OptimizationResultTest {

    @Test
    fun testSuccessfulVerification() {
        val result = OptimizationResult(
            isSuccess = true,
            gameName = "PUBG Mobile",
            requestedRefreshRate = 120f,
            actualRefreshRate = 120f,
            statusMessage = "Optimization active",
            verified = true
        )

        assertTrue(result.isDisplayRateVerified)
        assertTrue(result.verificationSummary.contains("Applied successfully"))
        assertTrue(result.verificationSummary.contains("120Hz"))
    }

    @Test
    fun testRefusedVerification() {
        val result = OptimizationResult(
            isSuccess = false,
            gameName = "PUBG Mobile",
            requestedRefreshRate = 120f,
            actualRefreshRate = 60f,
            statusMessage = "Applied with limitations",
            technicalExplanation = "Android did not accept the requested display mode.",
            verified = false
        )

        assertFalse(result.isDisplayRateVerified)
        assertTrue(result.verificationSummary.contains("Device/system refused 120Hz"))
        assertTrue(result.verificationSummary.contains("current: 60Hz"))
    }

    @Test
    fun testShizukuStatusSummary() {
        val notInstalled = ShizukuStatus(isInstalled = false)
        assertFalse(notInstalled.isReady)
        assertEquals("SHIZUKU UNAVAILABLE", notInstalled.title)
        assertTrue(notInstalled.summaryText.contains("not installed", ignoreCase = true))

        val stopped = ShizukuStatus(isInstalled = true, isRunning = false)
        assertFalse(stopped.isReady)
        assertEquals("SHIZUKU NOT RUNNING", stopped.title)
        assertTrue(stopped.summaryText.contains("stopped", ignoreCase = true))

        val notAuth = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = false)
        assertFalse(notAuth.isReady)
        assertEquals("PERMISSION REQUIRED", notAuth.title)
        assertTrue(notAuth.summaryText.contains("Permission", ignoreCase = true))

        val ready = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true, version = 13)
        assertTrue(ready.isReady)
        assertEquals("SHIZUKU READY", ready.title)
        assertTrue(ready.summaryText.contains("Authorized", ignoreCase = true))
    }
}
