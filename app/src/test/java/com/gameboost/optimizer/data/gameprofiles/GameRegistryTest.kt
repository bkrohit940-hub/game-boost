package com.gameboost.optimizer.data.gameprofiles

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRegistryTest {

    @Test
    fun testDefaultGamesContainPubgVariants() {
        val games = GameRegistry.getDefaultSupportedGames()
        assertEquals(5, games.size)

        val pubgGlobal = games.firstOrNull { it.id == "pubg_global" }
        assertNotNull(pubgGlobal)
        assertTrue(pubgGlobal!!.packageNames.contains("com.tencent.ig"))

        val pubgVn = games.firstOrNull { it.id == "pubg_vn" }
        assertNotNull(pubgVn)
        assertTrue(pubgVn!!.packageNames.contains("com.vng.pubgmobile"))

        val pubgTw = games.firstOrNull { it.id == "pubg_tw" }
        assertNotNull(pubgTw)
        assertTrue(pubgTw!!.packageNames.contains("com.rechild.tencent.ig"))

        val bgmi = games.firstOrNull { it.id == "bgmi" }
        assertNotNull(bgmi)
        assertTrue(bgmi!!.packageNames.contains("com.pubg.imobile"))

        val pubgKr = games.firstOrNull { it.id == "pubg_kr" }
        assertNotNull(pubgKr)
        assertTrue(pubgKr!!.packageNames.contains("com.pubg.krmobile"))
    }

    @Test
    fun testFindGameByPackage() {
        val foundGlobal = GameRegistry.findGameByPackage("com.tencent.ig")
        assertNotNull(foundGlobal)
        assertEquals("PUBG Mobile", foundGlobal!!.displayName)

        val foundVn = GameRegistry.findGameByPackage("com.vng.pubgmobile")
        assertNotNull(foundVn)
        assertEquals("PUBG Mobile VN", foundVn!!.displayName)

        val foundTw = GameRegistry.findGameByPackage("com.rechild.tencent.ig")
        assertNotNull(foundTw)
        assertEquals("PUBG Mobile TW", foundTw!!.displayName)

        val foundBgmi = GameRegistry.findGameByPackage("com.pubg.imobile")
        assertNotNull(foundBgmi)
        assertEquals("BGMI", foundBgmi!!.displayName)

        val foundKr = GameRegistry.findGameByPackage("com.pubg.krmobile")
        assertNotNull(foundKr)
        assertEquals("PUBG Mobile KR", foundKr!!.displayName)

        val notFound = GameRegistry.findGameByPackage("com.unknown.game")
        assertNull(notFound)
    }
}
