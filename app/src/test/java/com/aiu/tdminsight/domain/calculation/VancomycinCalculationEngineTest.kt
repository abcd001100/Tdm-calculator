package com.aiu.tdminsight.domain.calculation

import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.DoseInfo
import com.aiu.tdminsight.domain.model.PatientInfo
import com.aiu.tdminsight.domain.model.WorkflowInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Expected values below were independently computed (not asserted
 * against the implementation itself) and cross-checked by hand against
 * the formulas documented in docs/Calculation_Method_Proposal.md.
 */
class VancomycinCalculationEngineTest {

    private val engine = VancomycinCalculationEngine()

    private val patient = PatientInfo(
        weightKg = 70.0,
        ageYears = 45,
        sex = BiologicalSex.MALE,
        serumCreatinineMgDl = 1.0
    )

    private val dose = DoseInfo(
        doseMg = 1000.0,
        infusionDurationMinutes = 60.0,
        dosingIntervalHours = 12.0
    )

    @Test
    fun `pre-post two-point method matches hand-calculated values`() {
        val input = WorkflowInput.PrePost(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 10.0,
            preDoseSampleTimeBeforeDoseHours = 0.5,
            postDoseConcentrationMgL = 30.0,
            postDoseSampleTimeAfterInfusionHours = 1.0
        )

        val result = engine.calculate(input) as CalculationResult.Success
        val params = result.result.parameters

        assertEquals(0.4394, params.eliminationRateConstantPerHour!!, 0.0005)
        assertEquals(1.5773, params.eliminationHalfLifeHours!!, 0.0005)
        assertEquals(50.0, params.volumeOfDistributionL!!, 0.001)
        assertEquals(21.972, params.clearanceLPerHour!!, 0.005)
    }

    @Test
    fun `pre-post fails cleanly when post concentration is not higher than pre`() {
        val input = WorkflowInput.PrePost(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 20.0,
            preDoseSampleTimeBeforeDoseHours = 0.5,
            postDoseConcentrationMgL = 15.0,
            postDoseSampleTimeAfterInfusionHours = 1.0
        )

        val result = engine.calculate(input)

        assertTrue(result is CalculationResult.Failure)
    }

    @Test
    fun `pre-only uses population Ke and projects Cmax from measured trough`() {
        val input = WorkflowInput.Pre(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 8.0,
            preDoseSampleTimeBeforeDoseHours = 0.5
        )

        val result = engine.calculate(input) as CalculationResult.Success
        val params = result.result.parameters

        assertEquals(92.361, params.creatinineClearanceMlPerMin!!, 0.01)
        assertEquals(0.08106, params.eliminationRateConstantPerHour!!, 0.0001)
        assertEquals(49.0, params.volumeOfDistributionL!!, 0.001)
        assertEquals(19.513, result.result.explanation.last().value!!, 0.01)
    }

    @Test
    fun `post-only uses population Ke and projects Cmin from measured peak`() {
        val input = WorkflowInput.Post(
            patient = patient,
            dose = dose,
            postDoseConcentrationMgL = 30.0,
            postDoseSampleTimeAfterInfusionHours = 1.0
        )

        val result = engine.calculate(input) as CalculationResult.Success

        assertEquals(12.299, result.result.explanation.last().value!!, 0.01)
    }
}
