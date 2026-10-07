package com.gameboost.optimizer.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.gameboost.optimizer.models.HardwareStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Locale

/**
 * Lightweight hardware telemetry collector.
 * Emits hardware stats using genuine Android system APIs at battery-friendly intervals.
 */
class HardwareMonitor(
    private val context: Context,
    private val displayController: DisplayController
) {
    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val powerManager =
        context.getSystemService(Context.POWER_SERVICE) as? PowerManager

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

        // Real Thermal Status via PowerManager (API 29+)
        val thermalStatusStr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
            when (powerManager.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> "NORMAL"
                PowerManager.THERMAL_STATUS_LIGHT -> "LIGHT"
                PowerManager.THERMAL_STATUS_MODERATE -> "MODERATE"
                PowerManager.THERMAL_STATUS_SEVERE -> "SEVERE"
                PowerManager.THERMAL_STATUS_CRITICAL -> "CRITICAL"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "EMERGENCY"
                PowerManager.THERMAL_STATUS_SHUTDOWN -> "SHUTDOWN"
                else -> "NORMAL"
            }
        } else {
            "NORMAL"
        }

        // Real Thermal Headroom (API 30+)
        val thermalHeadroomStr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && powerManager != null) {
            try {
                val headroom = powerManager.getThermalHeadroom(30)
                if (!headroom.isNaN()) String.format(Locale.US, "%.2f", headroom) else null
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }

        return HardwareStats(
            batteryPercentage = batteryPct,
            batteryTemperatureCelsius = batteryTemp,
            isCharging = isCharging,
            ramUsedBytes = ramUsed,
            ramTotalBytes = memInfo.totalMem,
            currentRefreshRate = displayState.currentRefreshRate,
            estimatedFpsText = "Not available",
            cpuFrequencyInfo = "Normal",
            thermalStatus = thermalStatusStr,
            thermalHeadroom = thermalHeadroomStr
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
