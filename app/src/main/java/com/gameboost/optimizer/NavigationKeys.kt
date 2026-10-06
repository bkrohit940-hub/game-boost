package com.gameboost.optimizer

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object FirstLaunchKey : NavKey
@Serializable data object ShizukuSetupKey : NavKey
@Serializable data object DashboardKey : NavKey
@Serializable data class GameDetailKey(val gameId: String) : NavKey
@Serializable data object SettingsKey : NavKey
@Serializable data object DiagnosticsKey : NavKey
