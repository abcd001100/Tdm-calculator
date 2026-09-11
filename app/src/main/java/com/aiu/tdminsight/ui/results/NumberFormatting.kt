package com.aiu.tdminsight.ui.results

/**
 * Rounds a computed pharmacokinetic value to a sensible number of
 * decimal places for display. The domain/calculation layer keeps full
 * `Double` precision internally (that's what's actually used in any
 * further math) — this only affects how a human reads the number on
 * screen or in a shared summary, so it lives in the UI layer, not
 * `TdmCalculationEngine.kt`.
 *
 * Four decimal places keeps small values (Ke is often well under 0.01)
 * meaningful without showing raw floating-point noise on larger ones
 * (e.g. a clearance of `0.45477578124999996` becomes `0.4548`).
 */
fun Double.formatClinical(): String = "%.4f".format(this)
