package com.gameboost.optimizer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.gameboost.optimizer.GameBoostApp
import com.gameboost.optimizer.MainActivity
import com.gameboost.optimizer.R
import com.gameboost.optimizer.data.gameprofiles.GameRegistry
import com.gameboost.optimizer.models.OptimizationProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Lightweight foreground service that monitors active game state,
 * displays status notification, and handles auto-boost & auto-restore.
 */
class GameBoostService : LifecycleService() {

    companion object {
        private const val TAG = "GameBoostService"
        const val CHANNEL_ID = "gameboost_service_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_MONITORING = "com.gameboost.optimizer.START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.gameboost.optimizer.STOP_MONITORING"
        const val ACTION_RESTORE_SETTINGS = "com.gameboost.optimizer.RESTORE_SETTINGS"

        fun startService(context: Context) {
            val intent = Intent(context, GameBoostService::class.java).apply {
                action = ACTION_START_MONITORING
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, GameBoostService::class.java).apply {
                action = ACTION_STOP_MONITORING
            }
            context.startService(intent)
        }
    }

    private var monitorJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_STOP_MONITORING -> {
                lifecycleScope.launch {
                    val app = application as? GameBoostApp
                    app?.optimizationEngine?.restoreDefaults()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
            ACTION_RESTORE_SETTINGS -> {
                lifecycleScope.launch {
                    val app = application as? GameBoostApp
                    app?.optimizationEngine?.restoreDefaults()
                    updateNotification("Default settings restored", "Monitoring standby")
                }
            }
            ACTION_START_MONITORING, null -> {
                val notification = buildNotification(
                    title = "GAMEBOOST Active",
                    text = "Monitoring for PUBG / BGMI sessions"
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
                startActiveMonitoring()
            }
        }

        return START_STICKY
    }

    private fun startActiveMonitoring() {
        monitorJob?.cancel()
        monitorJob = lifecycleScope.launch {
            val app = application as? GameBoostApp ?: return@launch
            var previousForegroundGame: String? = null

            while (isActive) {
                try {
                    val prefs = app.userPreferencesRepository.userPreferencesFlow.first()
                    if (prefs.isAutoBoostEnabled && app.shizukuManager.hasPermission()) {
                        val currentFg = app.packageDetector.getForegroundPackage()

                        if (currentFg != null) {
                            val matchedGame = GameRegistry.findGameByPackage(currentFg)

                            if (matchedGame != null && currentFg != previousForegroundGame) {
                                Log.i(TAG, "Game launch detected: ${matchedGame.displayName} ($currentFg)")
                                previousForegroundGame = currentFg

                                val detectedProfile = app.optimizationRepository.getDetectedGames()
                                    .firstOrNull { it.id == matchedGame.id }
                                    ?: matchedGame.copy(installedPackageName = currentFg)

                                val optResult = app.optimizationEngine.applyOptimization(
                                    gameProfile = detectedProfile,
                                    profile = OptimizationProfile(targetRefreshRate = prefs.targetRefreshRate)
                                )

                                updateNotification(
                                    title = "GAMEBOOST: ${matchedGame.displayName} Active",
                                    text = "${optResult.actualRefreshRate.toInt()}Hz • Performance mode active"
                                )
                            } else if (matchedGame == null && previousForegroundGame != null) {
                                // Game closed
                                if (prefs.isRestoreOnExitEnabled) {
                                    Log.i(TAG, "Game exited: $previousForegroundGame. Restoring baseline...")
                                    app.optimizationEngine.restoreDefaults()
                                    updateNotification(
                                        title = "GAMEBOOST Standby",
                                        text = "Settings restored after game exit"
                                    )
                                }
                                previousForegroundGame = null
                            }
                        }
                    }
                } catch (e: Throwable) {
                    Log.w(TAG, "Error in game monitor loop", e)
                }

                delay(3000L) // Relaxed 3-second interval to avoid battery drain
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GameBoost Optimization Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live gaming optimization status and controls"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, text: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val restoreIntent = Intent(this, GameBoostService::class.java).apply {
            action = ACTION_RESTORE_SETTINGS
        }
        val restorePendingIntent = PendingIntent.getService(
            this,
            1,
            restoreIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_revert, "Restore Defaults", restorePendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(title: String, text: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, buildNotification(title, text))
    }

    override fun onDestroy() {
        super.onDestroy()
        monitorJob?.cancel()
    }
}
