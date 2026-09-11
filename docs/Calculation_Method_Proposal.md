# Vancomycin Calculation Method

Case Study §6 requires clinical equations to be "based on lecturer-approved
authoritative sources" and that "students must not invent clinical
formulas," and names two starting points to research from: myTDM
Calculator and the PhIS TDM Calculator Manual (§13). This document
records what was actually found in those sources, and the standard,
peer-reviewed pharmacokinetics literature they're built on — every
formula below traces to a named, dated, citable publication, not a
guess.

## Verified directly against myTDM Calculator's own worksheet

myTDM Calculator's site doesn't publish a methodology page, but running
the tool itself produces a step-by-step worksheet showing its actual
formulas. That worksheet was captured and checked line-by-line against
this project's implementation. Two real differences were found and
corrected to match the source exactly:

1. **Ke's time denominator** originally omitted the pre-dose sample's
   own timing offset. myTDM's worksheet formula is
   `Δt = τ − T_infusion − t_post − t_pre` (subtracting *both*
   sample-timing offsets), not just the post-dose one — corrected.
2. **Vd** originally used the full infusion-rate steady-state integral.
   myTDM's worksheet uses the simpler `Vd = Dose / (true Cmax − true
   Cmin)`, applied to the back-extrapolated (not raw measured) peak and
   trough — corrected to match.

**One remaining honest discrepancy, not yet resolved:** myTDM's CrCl
step uses serum creatinine in **µmol/L** with gender multipliers 1.23
(male) / ~1.04 (female), which is the Cockcroft-Gault equation in SI
units — mathematically equivalent to the mg/dL form used here, but this
app's input field is currently labelled and validated in **mg/dL**.
Functionally correct either way as long as the user enters the right
unit for the label shown, but the input unit doesn't match the
named source's own tool. Worth aligning if there's time, otherwise
flag it for whoever reviews this next.

## What the two named sources actually contain

**PhIS TDM Calculator Manual (13th Edition)** — the actual manual was
downloaded and read in full. It is a user manual for pharmacy staff
("how to click through this software"), not a pharmacokinetics
reference — it never states an equation itself. What it *does* confirm
is the exact input/output shape of each calculator, which matches what
this app implements:

| Workflow | Inputs | Outputs listed |
|---|---|---|
| Vancomycin Pre | Dose, interval, pre-level concentration, serum creatinine, **Vd (typed in by the pharmacist, not calculated)**, CrCl | Expected Cmax, assuming Cmin = pre-level |
| Vancomycin Post | Dose, interval, sampling time, post-level concentration, serum creatinine, CrCl | New dose/Cmin recommendation (no Vd/Ke/t½ listed) |
| Vancomycin Pre + Post | Dose, interval, both concentrations, both sample times (and the time between them), serum creatinine, CrCl | Vd, Ke, t½, AUC24, new dose/Cmin recommendation |

The structural implication, confirmed by the standard literature below:
only Pre+Post (two concentrations spanning one interval) can compute a
genuinely patient-specific Ke/Vd. Pre-only and Post-only rely on a
**population estimate** — this isn't a shortcut this project took, it's
inherent to only having one data point.

**myTDM Calculator** (mytdmcalculator.com) — no methodology, formulas,
or references published on the site; it's the tool itself, not a
reference document.

## Formulas used (implemented in `TdmCalculationEngine.kt`)

### Common to all three workflows

**Creatinine clearance — Cockcroft-Gault:**

```
CrCl (mL/min) = (140 − age) × weight(kg) × (0.85 if female else 1) / (72 × SCr(mg/dL))
```
Source: Cockcroft, D. W., & Gault, M. H. (1976). Prediction of
creatinine clearance from serum creatinine. *Nephron, 16*(1), 31–41.
The near-universal standard clinical CrCl estimate.

**Half-life** (mathematical identity):
```
t½ = ln(2) / Ke
```

**Clearance** (definitional, given Ke and Vd):
```
CL = Ke × Vd
```

### Pre-only and Post-only — population estimates

Only one concentration is measured, so Ke and Vd are population
averages rather than derived from this patient's own data:

**Elimination rate constant — Matzke population equation:**
```
Ke (/h) = 0.00083 × CrCl(mL/min) + 0.0044
```
Source: Matzke, G. R., McGory, R. W., Halstenson, C. E., & Keane, W. E.
(1984). Pharmacokinetics of vancomycin in patients with various degrees
of renal function. *Antimicrobial Agents and Chemotherapy, 25*(4),
433–437.

**Volume of distribution — standard population estimate:**
```
Vd (L) = 0.7 × weight (kg)
```
0.7 L/kg is the consistently-cited population average for vancomycin
across clinical pharmacokinetics teaching material and dosing
literature (individual patients typically range roughly 0.4–0.9 L/kg,
which is exactly why Pre+Post's patient-specific calculation, below, is
preferred whenever two concentrations are available).

**Pre-only** projects the measured trough forward to estimate the peak
(matching the PhIS Pre-calculator's described behaviour, "Expected Cmax
if assuming Expected Cmin = Pre level result"):
```
Cmax(estimated) = Cmin(measured) × e^(Ke × (τ − T_infusion))
```

**Post-only** projects the measured peak backward to estimate the
trough. The sample is drawn `t_sample` hours *after the infusion ends*,
so its absolute time since dose start is `T_infusion + t_sample` — both
must be subtracted from the interval to get the time actually
remaining before the next dose (an earlier version of this formula and
its implementation omitted `T_infusion` here, underestimating Cmin by
several percent; fixed and covered by a regression test):
```
Cmin(estimated) = Cmax(measured) × e^(−Ke × (τ − T_infusion − t_sample))
```

### Pre + Post — patient-specific, infusion-corrected two-point method

Both concentrations are actually measured for this patient. This is the
classic Sawchuk-Zaske two-point method, originally developed for
gentamicin and long since standard practice for vancomycin too:

Source: Sawchuk, R. J., & Zaske, D. E. (1976). Pharmacokinetics of
dosing regimens which utilize multiple intravenous infusions:
Gentamicin in burn patients. *Journal of Pharmacokinetics and
Biopharmaceutics, 4*(2), 183–195.

**The two measured concentrations are not simply "elapsed clock time
apart"** — a dose is administered between the pre-dose (trough) and
post-dose (peak) draws, and a dose adds drug rather than eliminating
it, so the raw elapsed time between the two draws cannot be used
directly. Instead, since the pre-dose value is a steady-state trough,
that same concentration recurs at the end of every interval — so the
two points sit on one continuous elimination curve running **from the
post-dose sample forward to the next occurrence of that trough**:

```
Δt = τ − T_infusion − t_post − t_pre
Ke = ln(Cpost / Cpre) / Δt
```

Each measured concentration is then back-extrapolated to its "true"
reference point — the true peak at the exact end of infusion, and the
true trough at the exact moment the dose is given — correcting for the
delay between when each sample was actually drawn and that reference
point:

```
Cmax(true) = Cpost(measured) × e^(Ke × t_post)
Cmin(true) = Cpre(measured) × e^(−Ke × t_pre)
```

Volume of distribution then uses the back-extrapolated true values
(not the raw measured ones) — this is what makes it a real correction
over the naive `Vd = Dose / (Cmax(measured) − Cmin(measured))`
shortcut, and it's the exact form myTDM Calculator's own worksheet
uses:

```
Vd = Dose / (Cmax(true) − Cmin(true))
```

## Validation guards implemented (Case Study §8: division by zero, invalid logarithms)

- Pre+Post refuses to calculate (with a clear message, not a crash) if
  the post-dose concentration isn't higher than the pre-dose
  concentration — required for the logarithm above to be valid.
- Pre+Post also refuses to calculate if the post-dose sample time
  doesn't leave room before the next dose (Δt would be zero or
  negative).
- All three workflows check for a zero/negative elimination rate
  constant before dividing by it.
- Pre-only and Post-only both check that the dosing interval is
  actually longer than the infusion duration / sample time before
  projecting forward or backward.

## Sources

- [PhIS TDM Calculator Manual, 13th Edition (PDF)](https://phisportal.moh.gov.my/sites/default/files/phis_attachments_39556/PB_U.%20MANUAL_TDM%20CALCULATOR-13th%20E.pdf)
- [myTDM Calculator](https://www.mytdmcalculator.com/) — no published methodology page, but its own generated worksheet (captured 2026-09-11) was checked line-by-line against this implementation and used to correct the Ke time term and Vd formula above
- Cockcroft, D. W., & Gault, M. H. (1976). Prediction of creatinine clearance from serum creatinine. *Nephron, 16*(1), 31–41. https://doi.org/10.1159/000180580
- Matzke, G. R., McGory, R. W., Halstenson, C. E., & Keane, W. E. (1984). Pharmacokinetics of vancomycin in patients with various degrees of renal function. *Antimicrobial Agents and Chemotherapy, 25*(4), 433–437.
- Sawchuk, R. J., & Zaske, D. E. (1976). Pharmacokinetics of dosing regimens which utilize multiple intravenous infusions: Gentamicin in burn patients. *Journal of Pharmacokinetics and Biopharmaceutics, 4*(2), 183–195.
- [Concordance of Vancomycin Population-Predicted Pharmacokinetics with Patient-Specific Pharmacokinetics — PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC7221031/)
