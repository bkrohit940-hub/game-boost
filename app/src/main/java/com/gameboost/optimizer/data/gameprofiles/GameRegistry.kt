package com.gameboost.optimizer.data.gameprofiles

import com.gameboost.optimizer.models.GameProfile

/**
 * Registry of supported games and their regional package IDs.
 * Easily extensible for new regions or game titles.
 */
object GameRegistry {

    fun getDefaultSupportedGames(): List<GameProfile> = listOf(
        GameProfile(
            id = "pubg_global",
            name = "PUBG Mobile",
            region = "Global",
            packageNames = listOf("com.tencent.ig"),
            supportedFeatures = listOf(
                "120Hz Panel Lock",
                "Game Mode Performance",
                "Reduced Animation Latency",
                "Reversible Session Restore"
            )
        ),
        GameProfile(
            id = "bgmi",
            name = "BGMI",
            region = "India",
            packageNames = listOf("com.pubg.imobile"),
            supportedFeatures = listOf(
                "120Hz Panel Lock",
                "Game Mode Performance",
                "Reduced Animation Latency",
                "Reversible Session Restore"
            )
        ),
        GameProfile(
            id = "pubg_kr",
            name = "PUBG Mobile KR",
            region = "Korea / Japan",
            packageNames = listOf("com.pubg.krmobile"),
            supportedFeatures = listOf(
                "120Hz Panel Lock",
                "Game Mode Performance",
                "Reduced Animation Latency",
                "Reversible Session Restore"
            )
        ),
        GameProfile(
            id = "pubg_vn",
            name = "PUBG Mobile VN",
            region = "Vietnam",
            packageNames = listOf("com.vng.pubgmobile"),
            supportedFeatures = listOf(
                "120Hz Panel Lock",
                "Game Mode Performance",
                "Reduced Animation Latency",
                "Reversible Session Restore"
            )
        ),
        GameProfile(
            id = "pubg_tw",
            name = "PUBG Mobile TW",
            region = "Taiwan",
            packageNames = listOf("com.rechild.tencent.ig"),
            supportedFeatures = listOf(
                "120Hz Panel Lock",
                "Game Mode Performance",
                "Reduced Animation Latency",
                "Reversible Session Restore"
            )
        )
    )

    fun findGameByPackage(packageName: String): GameProfile? {
        return getDefaultSupportedGames().firstOrNull { profile ->
            profile.packageNames.contains(packageName)
        }
    }
}
