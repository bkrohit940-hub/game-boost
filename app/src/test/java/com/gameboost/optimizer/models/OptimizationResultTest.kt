package com.gameboost.optimizer.models

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
        assertTrue(notInstalled.summaryText.contains("Not Installed"))

        val stopped = ShizukuStatus(isInstalled = true, isRunning = false)
        assertFalse(stopped.isReady)
        assertTrue(stopped.summaryText.contains("Service Stopped"))

        val notAuth = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = false)
        assertFalse(notAuth.isReady)
        assertTrue(notAuth.summaryText.contains("Permission Required"))

        val ready = ShizukuStatus(isInstalled = true, isRunning = true, isAuthorized = true, version = 13)
        assertTrue(ready.isReady)
        assertTrue(ready.summaryText.contains("Authorized (v13)"))
    }
}
