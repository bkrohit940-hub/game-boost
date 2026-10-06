package com.gameboost.optimizer.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.gameboost.optimizer.models.HardwareStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Lightweight hardware telemetry collector.
 * Emits hardware stats at battery-friendly intervals.
 */
class HardwareMonitor(
    private val context: Context,
    private val displayController: DisplayController
) {
    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun getHardwareStats(): HardwareStats {
        // Battery status
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (level != -1 && scale > 0) (level * 100) / scale else 100

        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 300) ?: 300
        val batteryTemp = tempTenths / 10.0f

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        // Memory info
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val ramUsed = memInfo.totalMem - memInfo.availMem

        // Refresh rate
        val displayState = displayController.getDisplayState()

        return HardwareStats(
            batteryPercentage = batteryPct,
            batteryTemperatureCelsius = batteryTemp,
            isCharging = isCharging,
            ramUsedBytes = ramUsed,
            ramTotalBytes = memInfo.totalMem,
            currentRefreshRate = displayState.currentRefreshRate,
            estimatedFpsText = "Not available",
            cpuFrequencyInfo = "Normal"
        )
    }

    /**
     * Cold flow that polls hardware stats at relaxed intervals (2.5s) while observed.
     */
    fun monitorFlow(intervalMs: Long = 2500L): Flow<HardwareStats> = flow {
        while (true) {
            emit(getHardwareStats())
            delay(intervalMs)
        }
    }.flowOn(Dispatchers.Default)
}
