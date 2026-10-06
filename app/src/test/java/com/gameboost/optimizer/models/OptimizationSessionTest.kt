package com.gameboost.optimizer.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OptimizationSessionTest {

    @Test
    fun testSuccessfulOptimizationSession() {
        val baseline = DisplayStateBackup(
            peakRefreshRate = "60.0",
            minRefreshRate = "60.0",
            windowAnimationScale = "1.0"
        )
        val session = OptimizationSession(
            gamePackage = "com.tencent.ig",
            gameName = "PUBG Mobile",
            selectedProfile = OptimizationProfile(type = OptimizationProfileType.PERFORMANCE),
            requestedRefreshRate = 120f,
            originalSettings = baseline,
            modifiedSettings = listOf("peak_refresh_rate: 120.0", "window_animation_scale: 0.5"),
            verificationResult = OptimizationResult(
                isSuccess = true,
                gameName = "PUBG Mobile",
                requestedRefreshRate = 120f,
                actualRefreshRate = 120f,
                statusMessage = "120Hz display active",
                verified = true
            ),
            sessionStatus = SessionStatus.ACTIVE
        )

        assertEquals(SessionStatus.ACTIVE, session.sessionStatus)
        assertTrue(session.verificationResult?.isSuccess == true)
        assertTrue(session.verificationResult?.isDisplayRateVerified == true)
        assertEquals(120f, session.verificationResult?.actualRefreshRate ?: 0f, 0.01f)
    }

    @Test
    fun testFailedOptimizationSession() {
        val baseline = DisplayStateBackup(
            peakRefreshRate = "60.0",
            minRefreshRate = "60.0"
        )
        val session = OptimizationSession(
            gamePackage = "com.tencent.ig",
            gameName = "PUBG Mobile",
            selectedProfile = OptimizationProfile(type = OptimizationProfileType.PERFORMANCE),
            requestedRefreshRate = 120f,
            originalSettings = baseline,
            verificationResult = OptimizationResult(
                isSuccess = false,
                gameName = "PUBG Mobile",
                requestedRefreshRate = 120f,
                actualRefreshRate = 60f,
                statusMessage = "Mode rejected",
                technicalExplanation = "Android did not accept the requested display mode.",
                verified = false
            ),
            sessionStatus = SessionStatus.FAILED
        )

        assertEquals(SessionStatus.FAILED, session.sessionStatus)
        assertFalse(session.verificationResult?.isSuccess == true)
        assertFalse(session.verificationResult?.isDisplayRateVerified == true)
        assertTrue(session.verificationResult?.verificationSummary?.contains("refused") == true)
    }

    @Test
    fun testUnsupportedOperationGracefulHandling() {
        // When a feature like Android Game Mode API is unsupported, optimizer records limitation without aborting entire session
        val verification = OptimizationResult(
            isSuccess = true,
            gameName = "PUBG Mobile",
            requestedRefreshRate = 120f,
            actualRefreshRate = 120f,
            statusMessage = "Applied with Game Mode limitations",
            failedSettings = listOf("cmd game mode performance"),
            technicalExplanation = "Game Mode API: Not supported on this device/firmware.",
            verified = true
        )

        assertTrue(verification.isSuccess)
        assertTrue(verification.failedSettings.contains("cmd game mode performance"))
        assertNotNull(verification.technicalExplanation)
        assertTrue(verification.technicalExplanation!!.contains("Not supported"))
    }

    @Test
    fun testRollbackCompleteSuccess() {
        val restore = RestorationResult(
            isCompleteSuccess = true,
            restoredItems = listOf("Refresh rate", "Animation scales", "Game mode")
        )

        assertTrue(restore.isCompleteSuccess)
        assertEquals("Default settings restored successfully", restore.summaryText)
        assertEquals(3, restore.restoredItems.size)
        assertTrue(restore.failedItems.isEmpty())
    }

    @Test
    fun testRollbackPartialFailureDiagnostics() {
        val partialRestore = RestorationResult(
            isCompleteSuccess = false,
            restoredItems = listOf("Refresh rate"),
            failedItems = listOf("Animation scale"),
            failureReason = "System rejected the requested value."
        )

        assertFalse(partialRestore.isCompleteSuccess)
        assertEquals("Restore partially complete", partialRestore.summaryText)
        assertEquals(listOf("Refresh rate"), partialRestore.restoredItems)
        assertEquals(listOf("Animation scale"), partialRestore.failedItems)
        assertEquals("System rejected the requested value.", partialRestore.failureReason)
    }
}
