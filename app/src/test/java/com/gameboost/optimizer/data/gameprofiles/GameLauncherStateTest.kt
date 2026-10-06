package com.gameboost.optimizer.data.gameprofiles

import com.gameboost.optimizer.models.GameProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLauncherStateTest {

    @Test
    fun testAllSupportedGamePackagesConfigured() {
        val games = GameRegistry.getDefaultSupportedGames()
        val allPackages = games.flatMap { it.packageNames }

        // PUBG Mobile Global
        assertTrue(allPackages.contains("com.tencent.ig"))
        // BGMI
        assertTrue(allPackages.contains("com.pubg.imobile"))
        // PUBG Mobile KR
        assertTrue(allPackages.contains("com.pubg.krmobile"))
        // PUBG Mobile VN
        assertTrue(allPackages.contains("com.vng.pubgmobile"))
        // PUBG Mobile TW
        assertTrue(allPackages.contains("com.rechild.tencent.ig"))
    }

    @Test
    fun testUninstalledGameState() {
        val uninstalledGame = GameProfile(
            id = "bgmi",
            name = "BGMI",
            region = "India",
            packageNames = listOf("com.pubg.imobile"),
            isInstalled = false,
            installedPackageName = null
        )

        assertFalse(uninstalledGame.isInstalled)
        assertFalse(uninstalledGame.optimizationActive)
        assertEquals("com.pubg.imobile", uninstalledGame.activePackageName)
    }

    @Test
    fun testInstalledGameStateAndActiveBoost() {
        val installedGame = GameProfile(
            id = "pubg_global",
            name = "PUBG Mobile",
            region = "Global",
            packageNames = listOf("com.tencent.ig"),
            isInstalled = true,
            installedPackageName = "com.tencent.ig",
            appVersion = "3.2.0",
            optimizationActive = true
        )

        assertTrue(installedGame.isInstalled)
        assertTrue(installedGame.optimizationActive)
        assertEquals("com.tencent.ig", installedGame.activePackageName)
        assertEquals("3.2.0", installedGame.appVersion)
    }
}
