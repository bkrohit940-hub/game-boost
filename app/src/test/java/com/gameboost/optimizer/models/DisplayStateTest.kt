package com.gameboost.optimizer.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayStateTest {

    @Test
    fun test60HzDisplay() {
        val state = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 60f,
            supportedRefreshRates = listOf(60f)
        )
        assertFalse(state.supports120Hz)
        assertFalse(state.supports90Hz)
        assertEquals(60f, state.highestRefreshRate, 0.01f)
    }

    @Test
    fun test90HzDisplay() {
        val state = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 90f,
            supportedRefreshRates = listOf(60f, 90f)
        )
        assertFalse(state.supports120Hz)
        assertTrue(state.supports90Hz)
        assertEquals(90f, state.highestRefreshRate, 0.01f)
    }

    @Test
    fun test120HzDisplay() {
        val state = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 120f,
            supportedRefreshRates = listOf(60f, 90f, 120f)
        )
        assertTrue(state.supports120Hz)
        assertTrue(state.supports90Hz)
        assertEquals(120f, state.highestRefreshRate, 0.01f)
    }

    @Test
    fun test144HzDisplay() {
        val state = DisplayState(
            currentRefreshRate = 120f,
            maximumRefreshRate = 144f,
            supportedRefreshRates = listOf(60f, 90f, 120f, 144f)
        )
        assertTrue(state.supports120Hz)
        assertEquals(144f, state.highestRefreshRate, 0.01f)
    }

    @Test
    fun testRefreshRateTargetOptionsFor60Hz() {
        val state = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 60f,
            supportedRefreshRates = listOf(60f)
        )
        val targets = RefreshRateTarget.getAvailableForDisplay(state)
        assertEquals(listOf(RefreshRateTarget.SYSTEM_DEFAULT, RefreshRateTarget.HZ_60, RefreshRateTarget.HIGHEST_AVAILABLE), targets)
        assertFalse(targets.contains(RefreshRateTarget.HZ_120))
        assertFalse(targets.contains(RefreshRateTarget.HZ_90))
    }

    @Test
    fun testRefreshRateTargetOptionsFor90Hz() {
        val state = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 90f,
            supportedRefreshRates = listOf(60f, 90f)
        )
        val targets = RefreshRateTarget.getAvailableForDisplay(state)
        assertEquals(
            listOf(
                RefreshRateTarget.SYSTEM_DEFAULT,
                RefreshRateTarget.HZ_60,
                RefreshRateTarget.HZ_90,
                RefreshRateTarget.HIGHEST_AVAILABLE
            ),
            targets
        )
        assertFalse(targets.contains(RefreshRateTarget.HZ_120))
    }

    @Test
    fun testRefreshRateTargetOptionsFor120Hz() {
        val state = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 120f,
            supportedRefreshRates = listOf(60f, 90f, 120f)
        )
        val targets = RefreshRateTarget.getAvailableForDisplay(state)
        assertEquals(
            listOf(
                RefreshRateTarget.SYSTEM_DEFAULT,
                RefreshRateTarget.HZ_60,
                RefreshRateTarget.HZ_90,
                RefreshRateTarget.HZ_120,
                RefreshRateTarget.HIGHEST_AVAILABLE
            ),
            targets
        )
        assertTrue(targets.contains(RefreshRateTarget.HZ_120))
    }

    @Test
    fun testUnsupportedRequestedMode() {
        val panel60Hz = DisplayState(
            currentRefreshRate = 60f,
            maximumRefreshRate = 60f,
            supportedRefreshRates = listOf(60f)
        )
        // Requesting 120Hz on 60Hz panel must be detected as unsupported
        assertFalse(panel60Hz.supports120Hz)
        assertFalse(RefreshRateTarget.getAvailableForDisplay(panel60Hz).contains(RefreshRateTarget.HZ_120))
    }
}
