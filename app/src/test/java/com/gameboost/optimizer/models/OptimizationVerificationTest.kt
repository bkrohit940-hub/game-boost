package com.gameboost.optimizer.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OptimizationVerificationTest {

    @Test
    fun testPerformanceModesDefinitions() {
        assertEquals("Safe", PerformanceMode.SAFE.displayName)
        assertEquals("Performance", PerformanceMode.PERFORMANCE.displayName)
        assertEquals("Aggressive", PerformanceMode.AGGRESSIVE.displayName)
        assertEquals("Thermal Override (Experimental)", PerformanceMode.THERMAL_OVERRIDE.displayName)

        assertFalse(PerformanceMode.SAFE.isDangerous)
        assertFalse(PerformanceMode.PERFORMANCE.isDangerous)
        assertFalse(PerformanceMode.AGGRESSIVE.isDangerous)
        assertTrue(PerformanceMode.THERMAL_OVERRIDE.isDangerous)
    }

    @Test
    fun testThermalOverrideRequiresExplicitConfirmation() {
        val unconfirmedProfile = OptimizationProfile(
            mode = PerformanceMode.THERMAL_OVERRIDE,
            thermalOverrideConfirmed = false
        )
        assertFalse(unconfirmedProfile.thermalOverrideConfirmed)
        assertTrue(unconfirmedProfile.mode.isDangerous)

        val confirmedProfile = OptimizationProfile(
            mode = PerformanceMode.THERMAL_OVERRIDE,
            thermalOverrideConfirmed = true
        )
        assertTrue(confirmedProfile.thermalOverrideConfirmed)
    }

    @Test
    fun testBaselineRestoreBehavior() {
        val backup = DisplayStateBackup(
            peakRefreshRate = "60.0",
            minRefreshRate = "60.0",
            userRefreshRate = "60",
            windowAnimationScale = "1.0",
            transitionAnimationScale = "1.0",
            animatorDurationScale = "1.0"
        )

        assertEquals("60.0", backup.peakRefreshRate)
        assertEquals("1.0", backup.windowAnimationScale)

        val restoration = RestorationResult(
            isCompleteSuccess = true,
            restoredItems = listOf("Refresh rate restored", "Restored animation scales")
        )

        assertTrue(restoration.isCompleteSuccess)
        assertEquals(2, restoration.restoredItems.size)
        assertEquals("Default settings restored successfully", restoration.summaryText)
    }

    @Test
    fun testVerificationFailureReporting() {
        val failedResult = OptimizationResult(
            isSuccess = false,
            gameName = "BGMI",
            requestedRefreshRate = 120f,
            actualRefreshRate = 60f,
            failedSettings = listOf("Display control restricted by device/OEM"),
            statusMessage = "Optimization failed",
            technicalExplanation = "Display control restricted by device/OEM",
            verified = false
        )

        assertFalse(failedResult.isSuccess)
        assertFalse(failedResult.verified)
        assertEquals("Display control restricted by device/OEM", failedResult.technicalExplanation)
        assertEquals(1, failedResult.failedSettings.size)
    }
}
