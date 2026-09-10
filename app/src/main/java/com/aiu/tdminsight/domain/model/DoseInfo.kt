package com.aiu.tdminsight.domain.model

/**
 * The administered Vancomycin dose, common to every workflow.
 */
data class DoseInfo(
    val doseMg: Double,
    val infusionDurationMinutes: Double,
    val dosingIntervalHours: Double
)
