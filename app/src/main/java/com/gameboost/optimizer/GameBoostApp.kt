package com.gameboost.optimizer

import android.app.Application
import com.gameboost.optimizer.data.datastore.UserPreferencesRepository
import com.gameboost.optimizer.data.repositories.OptimizationRepository
import com.gameboost.optimizer.domain.optimizer.OptimizationEngine
import com.gameboost.optimizer.system.DeviceCapabilityDetector
import com.gameboost.optimizer.system.DisplayController
import com.gameboost.optimizer.system.HardwareMonitor
import com.gameboost.optimizer.system.PackageDetector
import com.gameboost.optimizer.system.PerformanceController
import com.gameboost.optimizer.system.ShizukuManager

import com.gameboost.optimizer.system.PrivilegedExecutionEngine
import com.gameboost.optimizer.system.ShizukuBackend
import com.gameboost.optimizer.system.WirelessAdbBackend
import com.gameboost.optimizer.system.adb.AdbConnectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameBoostApp : Application() {

    lateinit var shizukuManager: ShizukuManager
        private set

    lateinit var adbConnectionManager: AdbConnectionManager
        private set

    lateinit var privilegedEngine: PrivilegedExecutionEngine
        private set

    lateinit var displayController: DisplayController
        private set

    lateinit var performanceController: PerformanceController
        private set

    lateinit var deviceCapabilityDetector: DeviceCapabilityDetector
        private set

    lateinit var packageDetector: PackageDetector
        private set

    lateinit var hardwareMonitor: HardwareMonitor
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    lateinit var optimizationEngine: OptimizationEngine
        private set

    lateinit var optimizationRepository: OptimizationRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        shizukuManager = ShizukuManager(this)
        adbConnectionManager = AdbConnectionManager(this)

        val shizukuBackend = ShizukuBackend(shizukuManager)
        val wirelessAdbBackend = WirelessAdbBackend(adbConnectionManager)
        privilegedEngine = PrivilegedExecutionEngine(shizukuBackend, wirelessAdbBackend)

        displayController = DisplayController(this, shizukuManager, privilegedEngine)
        performanceController = PerformanceController(this, shizukuManager, privilegedEngine)
        deviceCapabilityDetector = DeviceCapabilityDetector(this, displayController)
        packageDetector = PackageDetector(this, shizukuManager, privilegedEngine)
        hardwareMonitor = HardwareMonitor(this, displayController)
        userPreferencesRepository = UserPreferencesRepository(this)

        optimizationEngine = OptimizationEngine(
            shizukuManager = shizukuManager,
            displayController = displayController,
            performanceController = performanceController,
            privilegedEngine = privilegedEngine
        )

        optimizationRepository = OptimizationRepository(
            shizukuManager = shizukuManager,
            packageDetector = packageDetector,
            deviceCapabilityDetector = deviceCapabilityDetector,
            hardwareMonitor = hardwareMonitor,
            userPreferencesRepository = userPreferencesRepository,
            optimizationEngine = optimizationEngine,
            privilegedEngine = privilegedEngine,
            adbConnectionManager = adbConnectionManager,
            displayController = displayController
        )

        CoroutineScope(Dispatchers.Main.immediate).launch {
            combine(
                shizukuManager.statusFlow,
                adbConnectionManager.stateFlow
            ) { shizuku, adb ->
                privilegedEngine.updateStates(shizuku, adb)
            }.collect()
        }
    }

    companion object {
        lateinit var instance: GameBoostApp
            private set
    }
}
