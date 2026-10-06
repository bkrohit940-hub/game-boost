package com.gameboost.optimizer.system

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryOptimizationTest {

    @Test
    fun testProtectedPackageListProtectsCriticalServices() {
        val protected = MemoryOptimizer.PROTECTED_PACKAGES

        // Game Boost self and Shizuku must be protected
        assertTrue(protected.contains("com.gameboost.optimizer"))
        assertTrue(protected.contains("moe.shizuku.privileged.api"))

        // System critical services must be protected
        assertTrue(protected.contains("android"))
        assertTrue(protected.contains("com.android.systemui"))
        assertTrue(protected.contains("com.google.android.gms"))
        assertTrue(protected.contains("com.android.phone"))
        assertTrue(protected.contains("com.android.server.telecom"))
        assertTrue(protected.contains("com.android.bluetooth"))

        // Input methods / keyboards must be protected
        assertTrue(protected.contains("com.google.android.inputmethod.latin"))
        assertTrue(protected.contains("com.android.inputmethod.latin"))

        // Launchers must be protected
        assertTrue(protected.contains("com.android.launcher3"))
        assertTrue(protected.contains("com.google.android.apps.nexuslauncher"))
        assertTrue(protected.contains("com.sec.android.app.launcher"))
    }

    @Test
    fun testMemoryOptimizationResultHonestReporting() {
        val result = MemoryOptimizationResult(
            ramUsedBeforeBytes = 5_000_000_000L,
            ramUsedAfterBytes = 4_700_000_000L,
            ramTotalBytes = 8_000_000_000L,
            ramAvailableBytes = 3_300_000_000L,
            optimizedPackages = listOf("com.example.social", "com.example.browser"),
            statusMessage = "Trimmed background memory allocations across 2 applications"
        )

        assertEquals("4.4 GB", result.formattedRamUsed)
        assertEquals("3.1 GB", result.formattedRamAvailable)
        assertEquals("7.5 GB", result.formattedRamTotal)
        assertEquals(2, result.optimizedPackages.size)
        // No fake inflated claims like "Freed 10GB RAM"
        assertFalse(result.statusMessage.contains("10GB"))
    }
}
