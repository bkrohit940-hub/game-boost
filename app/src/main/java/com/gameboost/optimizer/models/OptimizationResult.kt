package com.gameboost.optimizer.models

data class OptimizationResult(
    val isSuccess: Boolean,
    val gameName: String,
    val requestedRefreshRate: Float,
    val actualRefreshRate: Float,
    val appliedSettings: List<String> = emptyList(),
    val failedSettings: List<String> = emptyList(),
    val statusMessage: String,
    val technicalExplanation: String? = null,
    val verified: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),

    // Explicit separation of Display vs Game FPS (Phase 4 & Phase 7)
    val displayRefreshVerified: Boolean = false,
    val displayRefreshStatusText: String = "",
    val gameFpsVerified: Boolean = false,
    val gameFpsStatusText: String = "90 FPS NOT VERIFIED (In-game graphics selection required)",
    val gameModeStatusText: String = "",
    val optimizationSteps: List<OptimizationStepResult> = emptyList()
) {
    val isDisplayRateVerified: Boolean
        get() = displayRefreshVerified || (requestedRefreshRate > 0f && Math.abs(actualRefreshRate - requestedRefreshRate) < 1.0f)

    val verificationSummary: String
        get() = if (isDisplayRateVerified) {
            "✓ Applied successfully (${actualRefreshRate.toInt()}Hz display verified)"
        } else {
            "✕ Device/system refused ${requestedRefreshRate.toInt()}Hz (current: ${actualRefreshRate.toInt()}Hz)"
        }
}
