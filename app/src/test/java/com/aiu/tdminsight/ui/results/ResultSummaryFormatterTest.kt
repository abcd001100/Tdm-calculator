package com.aiu.tdminsight.ui.results

import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.DoseInfo
import com.aiu.tdminsight.domain.model.PatientInfo
import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.domain.model.WorkflowInput
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultSummaryFormatterTest {

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

    private fun preResult(parameters: PharmacokineticParameters) = TdmResult(
        workflow = VancomycinWorkflow.PRE,
        input = WorkflowInput.Pre(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 8.0,
            preDoseSampleTimeBeforeDoseHours = 0.5
        ),
        parameters = parameters
    )

    @Test
    fun `includes workflow name, input values, and parameters`() {
        val summary = formatTdmResultSummary(
            preResult(
                PharmacokineticParameters(
                    eliminationRateConstantPerHour = 0.08,
                    eliminationHalfLifeHours = 8.6,
                    volumeOfDistributionL = 49.0,
                    clearanceLPerHour = 3.9,
                    creatinineClearanceMlPerMin = 92.4
                )
            )
        )

        assertTrue(summary.contains("Vancomycin Pre Result"))
        assertTrue(summary.contains("Weight: 70.0 kg"))
        assertTrue(summary.contains("Pre-dose concentration: 8.0 mg/L"))
        assertTrue(summary.contains("Creatinine clearance: 92.4 mL/min"))
        assertTrue(summary.contains("Elimination rate constant (Ke): 0.08 /h"))
    }

    @Test
    fun `omits creatinine clearance line when absent`() {
        val summary = formatTdmResultSummary(preResult(PharmacokineticParameters()))

        assertFalse(summary.contains("Creatinine clearance"))
    }

    @Test
    fun `always includes the academic prototype disclaimer`() {
        val summary = formatTdmResultSummary(preResult(PharmacokineticParameters()))

        assertTrue(
            summary.contains(
                "TDM Insight is an academic software prototype for educational and " +
                    "software development purposes only."
            )
        )
    }

    @Test
    fun `notes sample data only when flagged`() {
        val flagged = formatTdmResultSummary(preResult(PharmacokineticParameters()), isSampleData = true)
        val real = formatTdmResultSummary(preResult(PharmacokineticParameters()), isSampleData = false)

        assertTrue(flagged.contains("Sample data — no calculation has been run yet."))
        assertFalse(real.contains("Sample data — no calculation has been run yet."))
    }
}
