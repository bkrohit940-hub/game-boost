package com.gameboost.optimizer.models

data class GameProfile(
    val id: String,
    val name: String,
    val region: String,
    val packageNames: List<String>,
    val isInstalled: Boolean = false,
    val installedPackageName: String? = null,
    val appVersion: String? = null,
    val optimizationActive: Boolean = false,
    val selectedProfile: OptimizationProfile = OptimizationProfile(),
    val lastOptimizedTime: Long? = null,
    val supportedFeatures: List<String> = listOf("120Hz Display", "Game Mode API", "Animation Optimization")
) {
    val displayName: String
        get() = name

    val activePackageName: String
        get() = installedPackageName ?: packageNames.firstOrNull() ?: ""
}
