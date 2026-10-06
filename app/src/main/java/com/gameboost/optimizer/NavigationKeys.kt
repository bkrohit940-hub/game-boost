package com.gameboost.optimizer

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Clean 5-section navigation architecture matching specification section 12:
 * HOME, GAMES, BOOST, DIAGNOSTICS, SETTINGS
 */
@Serializable data object HomeKey : NavKey
@Serializable data object GamesKey : NavKey
@Serializable data object BoostKey : NavKey
@Serializable data object DiagnosticsKey : NavKey
@Serializable data object SettingsKey : NavKey

// Flow & Subscreens
@Serializable data object FirstLaunchKey : NavKey
@Serializable data object ShizukuSetupKey : NavKey
@Serializable data class GameDetailKey(val gameId: String) : NavKey

// Legacy compatibility
@Serializable data object DashboardKey : NavKey
