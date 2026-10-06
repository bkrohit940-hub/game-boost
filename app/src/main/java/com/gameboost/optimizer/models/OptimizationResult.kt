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
    val timestamp: Long = System.currentTimeMillis()
) {
    val isDisplayRateVerified: Boolean
        get() = Math.abs(actualRefreshRate - requestedRefreshRate) < 1.0f

    val verificationSummary: String
        get() = if (isDisplayRateVerified) {
            "✓ Applied successfully (${actualRefreshRate.toInt()}Hz verified)"
        } else {
            "✕ Device/system refused ${requestedRefreshRate.toInt()}Hz (current: ${actualRefreshRate.toInt()}Hz)"
        }
}
