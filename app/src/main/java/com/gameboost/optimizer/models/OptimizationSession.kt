package com.gameboost.optimizer.models

data class RestorationResult(
    val isCompleteSuccess: Boolean,
    val restoredItems: List<String> = emptyList(),
    val failedItems: List<String> = emptyList(),
    val failureReason: String? = null
) {
    val summaryText: String
        get() = when {
            isCompleteSuccess -> "Default settings restored successfully"
            restoredItems.isNotEmpty() -> "Restore partially complete"
            else -> "Restoration failed: ${failureReason ?: "Unknown error"}"
        }
}

enum class SessionStatus {
    INITIALIZING,
    BASELINE_CAPTURED,
    APPLIED,
    ACTIVE,
    REVERTED,
    FAILED
}

data class OptimizationSession(
    val sessionId: String = java.util.UUID.randomUUID().toString(),
    val gamePackage: String,
    val gameName: String,
    val startTimestamp: Long = System.currentTimeMillis(),
    val selectedProfile: OptimizationProfile,
    val requestedRefreshRate: Float,
    val originalSettings: DisplayStateBackup,
    val modifiedSettings: List<String> = emptyList(),
    val verificationResult: OptimizationResult? = null,
    val sessionStatus: SessionStatus = SessionStatus.INITIALIZING,
    val restorationResult: RestorationResult? = null
)
