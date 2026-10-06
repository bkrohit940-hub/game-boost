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

class GameBoostApp : Application() {

    lateinit var shizukuManager: ShizukuManager
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
        displayController = DisplayController(this, shizukuManager)
        performanceController = PerformanceController(this, shizukuManager)
        deviceCapabilityDetector = DeviceCapabilityDetector(this, displayController)
        packageDetector = PackageDetector(this, shizukuManager)
        hardwareMonitor = HardwareMonitor(this, displayController)
        userPreferencesRepository = UserPreferencesRepository(this)

        optimizationEngine = OptimizationEngine(
            shizukuManager = shizukuManager,
            displayController = displayController,
            performanceController = performanceController
        )

        optimizationRepository = OptimizationRepository(
            shizukuManager = shizukuManager,
            packageDetector = packageDetector,
            deviceCapabilityDetector = deviceCapabilityDetector,
            hardwareMonitor = hardwareMonitor,
            userPreferencesRepository = userPreferencesRepository,
            optimizationEngine = optimizationEngine
        )
    }

    companion object {
        lateinit var instance: GameBoostApp
            private set
    }
}
