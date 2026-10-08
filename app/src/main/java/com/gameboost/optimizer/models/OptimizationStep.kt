package com.gameboost.optimizer.models

/**
 * Capability check status before command execution.
 */
enum class CapabilityStatus {
    SUPPORTED,
    UNAVAILABLE,
    NOT_SUPPORTED
}

/**
 * Result status of an individual optimization step execution.
 */
enum class StepExecutionStatus {
    ACTIVE,
    FAILED,
    SKIPPED,
    UNAVAILABLE,
    NOT_SUPPORTED
}

/**
 * Detailed trace of a privileged optimization operation meeting the Phase 5 engineering specification:
 * COMMAND, CAPABILITY CHECK, EXECUTION, EXIT CODE, OUTPUT, VERIFICATION.
 */
data class OptimizationStepResult(
    val stepName: String,
    val command: String,
    val capability: CapabilityStatus,
    val status: StepExecutionStatus,
    val exitCode: Int? = null,
    val output: String? = null,
    val verified: Boolean = false,
    val details: String = ""
)
