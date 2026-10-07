package com.gameboost.optimizer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gameboost.optimizer.models.OptimizationProfile
import com.gameboost.optimizer.models.OptimizationProfileType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gameboost_preferences")

data class AppUserPreferences(
    val isFirstRunCompleted: Boolean = false,
    val selectedProfileType: OptimizationProfileType = OptimizationProfileType.PERFORMANCE,
    val targetRefreshRate: Float = 120f,
    val isAutoBoostEnabled: Boolean = true,
    val isRestoreOnExitEnabled: Boolean = true,
    val isSessionMonitoringEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val showStartupScreen: Boolean = true,
    val wasShizukuEverAuthorized: Boolean = false
)

class UserPreferencesRepository(
    private val context: Context
) {
    companion object {
        val KEY_FIRST_RUN_COMPLETED = booleanPreferencesKey("key_first_run_completed")
        val KEY_PROFILE_TYPE = stringPreferencesKey("key_profile_type")
        val KEY_TARGET_REFRESH_RATE = floatPreferencesKey("key_target_refresh_rate")
        val KEY_AUTO_BOOST = booleanPreferencesKey("key_auto_boost")
        val KEY_RESTORE_ON_EXIT = booleanPreferencesKey("key_restore_on_exit")
        val KEY_SESSION_MONITORING = booleanPreferencesKey("key_session_monitoring")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("key_notifications")
        val KEY_SHOW_STARTUP_SCREEN = booleanPreferencesKey("key_show_startup_screen")
        val KEY_SHIZUKU_EVER_AUTH = booleanPreferencesKey("key_shizuku_ever_auth")
    }

    val userPreferencesFlow: Flow<AppUserPreferences> = context.dataStore.data.map { prefs ->
        val firstRun = prefs[KEY_FIRST_RUN_COMPLETED] ?: false
        val profileTypeStr = prefs[KEY_PROFILE_TYPE] ?: OptimizationProfileType.PERFORMANCE.name
        val profileType = try {
            OptimizationProfileType.valueOf(profileTypeStr)
        } catch (_: Throwable) {
            OptimizationProfileType.PERFORMANCE
        }
        val targetRate = prefs[KEY_TARGET_REFRESH_RATE] ?: 120f
        val autoBoost = prefs[KEY_AUTO_BOOST] ?: true
        val restoreOnExit = prefs[KEY_RESTORE_ON_EXIT] ?: true
        val sessionMonitoring = prefs[KEY_SESSION_MONITORING] ?: true
        val notifications = prefs[KEY_NOTIFICATIONS] ?: true
        val startupScreen = prefs[KEY_SHOW_STARTUP_SCREEN] ?: true
        val shizukuEverAuth = prefs[KEY_SHIZUKU_EVER_AUTH] ?: false

        AppUserPreferences(
            isFirstRunCompleted = firstRun,
            selectedProfileType = profileType,
            targetRefreshRate = targetRate,
            isAutoBoostEnabled = autoBoost,
            isRestoreOnExitEnabled = restoreOnExit,
            isSessionMonitoringEnabled = sessionMonitoring,
            isNotificationsEnabled = notifications,
            showStartupScreen = startupScreen,
            wasShizukuEverAuthorized = shizukuEverAuth
        )
    }

    suspend fun setFirstRunCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_FIRST_RUN_COMPLETED] = completed }
    }

    suspend fun setProfileType(type: OptimizationProfileType) {
        context.dataStore.edit { it[KEY_PROFILE_TYPE] = type.name }
    }

    suspend fun setTargetRefreshRate(rate: Float) {
        context.dataStore.edit { it[KEY_TARGET_REFRESH_RATE] = rate }
    }

    suspend fun setAutoBoost(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_BOOST] = enabled }
    }

    suspend fun setRestoreOnExit(enabled: Boolean) {
        context.dataStore.edit { it[KEY_RESTORE_ON_EXIT] = enabled }
    }

    suspend fun setSessionMonitoring(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SESSION_MONITORING] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun setShowStartupScreen(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_STARTUP_SCREEN] = enabled }
    }

    suspend fun setShizukuEverAuthorized(authorized: Boolean) {
        context.dataStore.edit { it[KEY_SHIZUKU_EVER_AUTH] = authorized }
    }
}
