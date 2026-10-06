package com.gameboost.optimizer.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayCapabilityTest {

    @Test
    fun testDetectsHighestSupportedRefreshRate() {
        val modes = listOf(
            DisplayModeInfo(modeId = 1, width = 1080, height = 2400, refreshRate = 60f),
            DisplayModeInfo(modeId = 2, width = 1080, height = 2400, refreshRate = 90f),
            DisplayModeInfo(modeId = 3, width = 1080, height = 2400, refreshRate = 120f)
        )

        val caps = DisplayCapabilities(
            currentRefreshRate = 60f,
            supportedRefreshRates = listOf(60f, 90f, 120f),
            supportedModes = modes
        )

        assertEquals(120f, caps.highestRefreshRate, 0.01f)
        assertTrue(caps.supports120Hz)
        assertTrue(caps.supports90Hz)
        assertTrue(caps.supports60Hz)
    }

    @Test
    fun testDetectsStandard60HzOnlyDisplay() {
        val modes = listOf(
            DisplayModeInfo(modeId = 1, width = 1080, height = 1920, refreshRate = 60f)
        )

        val caps = DisplayCapabilities(
            currentRefreshRate = 60f,
            supportedRefreshRates = listOf(60f),
            supportedModes = modes
        )

        assertEquals(60f, caps.highestRefreshRate, 0.01f)
        assertFalse(caps.supports120Hz)
        assertFalse(caps.supports90Hz)
        assertTrue(caps.supports60Hz)
    }

    @Test
    fun testOemRestrictionReportingDoesNotClaimSuccess() {
        val result = OptimizationResult(
            isSuccess = false,
            gameName = "PUBG Mobile",
            requestedRefreshRate = 120f,
            actualRefreshRate = 60f,
            statusMessage = "Mode rejected",
            technicalExplanation = "Display control restricted by device/OEM",
            verified = false
        )

        assertFalse(result.isSuccess)
        assertFalse(result.isDisplayRateVerified)
        assertEquals("Display control restricted by device/OEM", result.technicalExplanation)
        assertTrue(result.verificationSummary.contains("refused"))
    }

    @Test
    fun testDistinguishesDisplayHzFromGameFps() {
        val stats = HardwareStats(
            currentRefreshRate = 120f,
            estimatedFpsText = "Not available"
        )

        assertEquals(120f, stats.currentRefreshRate, 0.01f)
        // Game FPS must not be fabricated simply because display runs at 120Hz
        assertFalse(stats.estimatedFpsText == "120 FPS")
        assertEquals("Not available", stats.estimatedFpsText)
    }
}
