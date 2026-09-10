# Vancomycin Calculation Method — Proposal for Lecturer Confirmation

This document exists because the Case Study requires clinical equations
to be "based on lecturer-approved authoritative sources" and that
"students must not invent clinical formulas" (§6), while naming two
starting points to research from: myTDM Calculator and the PhIS TDM
Calculator Manual (§13). Neither source turned out to publish its
actual formulas (see below) — this document is what the team found
instead, from standard pharmacokinetics references, so the lecturer can
confirm or correct it before it's treated as final.

**Status: implemented in code, but not yet lecturer-confirmed.** Treat
every number this produces as provisional until a lecturer has reviewed
this document.

## What the two named sources actually contain

**PhIS TDM Calculator Manual (13th Edition)** — the actual manual was
downloaded and read in full. It is a user manual for pharmacy staff
("how to click through this software"), not a pharmacokinetics
reference — it never states an equation. What it *does* confirm is the
exact input/output shape of each calculator, which matches what this
app already implements:

| Workflow | Inputs | Outputs listed |
|---|---|---|
| Vancomycin Pre | Dose, interval, pre-level concentration, serum creatinine, **Vd (typed in by the pharmacist, not calculated)**, CrCl | Expected Cmax, assuming Cmin = pre-level |
| Vancomycin Post | Dose, interval, sampling time, post-level concentration, serum creatinine, CrCl | New dose/Cmin recommendation (no Vd/Ke/t½ listed) |
| Vancomycin Pre + Post | Dose, interval, both concentrations, both sample times (and the time between them), serum creatinine, CrCl | Vd, Ke, t½, AUC24, new dose/Cmin recommendation |

The structural implication: only Pre+Post (two concentrations spanning
one interval) computes a genuinely patient-specific Ke/Vd. Pre-only and
Post-only rely on a **population estimate**.

**myTDM Calculator** (mytdmcalculator.com) — no methodology, formulas,
or references published on the site at all; it's just the tool itself.

## Formulas used (implemented in `TdmCalculationEngine.kt`)

### Common to all three workflows

**Creatinine clearance — Cockcroft-Gault** (the near-universal standard
clinical CrCl estimate):

```
CrCl (mL/min) = (140 − age) × weight(kg) × (0.85 if female else 1) / (72 × SCr(mg/dL))
```

**Half-life** (mathematical identity, not a clinical assumption):

```
t½ = ln(2) / Ke
```

**Clearance** (definitional, given Ke and Vd):

```
CL = Ke × Vd
```

### Pre-only and Post-only — population estimates

Only one concentration is measured, so Ke and Vd can't be derived from
the patient's own data — both are population averages:

**Elimination rate constant — Matzke population equation:**

```
Ke (/h) = 0.00083 × CrCl(mL/min) + 0.0044
```

**Volume of distribution — standard population estimate:**

```
Vd (L) = 0.7 × weight (kg)
```

**⚠️ This Vd value is the weakest link in this proposal.** Different
sources cite different population averages for vancomycin (commonly
somewhere in 0.6–0.9 L/kg); 0.7 L/kg is a frequently-cited teaching
default, but it was not confirmed against one single authoritative
number. **This is the one thing most worth explicitly asking the
lecturer about.**

**Pre-only** then projects the measured trough forward to estimate the
peak (matching the PhIS Pre-calculator's described behaviour, "Expected
Cmax if assuming Expected Cmin = Pre level result"):

```
Cmax(estimated) = Cmin(measured) × e^(Ke × (τ − T_infusion))
```

**Post-only** projects the measured peak backward to estimate the
trough:

```
Cmin(estimated) = Cmax(measured) × e^(−Ke × (τ − t_sample))
```

### Pre + Post — patient-specific two-point method (Sawchuk-Zaske)

Both concentrations are actually measured for this patient, so Ke here
is patient-specific:

```
Ke = ln(Cpost / Cpre) / Δt
```
where `Δt` is the elapsed time between the two blood draws, derived
from the already-collected timing fields (pre-sample-before-dose +
infusion duration + post-sample-after-infusion) — this part is just
arithmetic on measured timestamps, not a clinical assumption.

**Volume of distribution — simplified:**

```
Vd = Dose / (Cpost − Cpre)
```

**⚠️ Known simplification:** this does not correct for elimination
occurring during the infusion itself. A more rigorous back-extrapolated
version exists in the literature but wasn't pinned down to one
canonical form during this research pass — flagged here rather than
silently used.

## Validation guards implemented (Case Study §8: division by zero, invalid logarithms)

- Pre+Post refuses to calculate (with a clear message, not a crash) if
  the post-dose concentration isn't higher than the pre-dose
  concentration — required for the logarithm above to be valid.
- All three workflows check for a zero/negative elimination rate
  constant before dividing by it.
- Pre-only and Post-only both check that the dosing interval is
  actually longer than the infusion duration / sample time before
  projecting forward or backward.

## What to ask the lecturer

1. Is Cockcroft-Gault the expected CrCl equation, or is another one
   preferred?
2. Is the Matzke population Ke equation (`0.00083 × CrCl + 0.0044`)
   acceptable for the single-concentration workflows?
3. **What Vd value/formula should Pre-only and Post-only use?** This is
   the one number in this whole proposal without a solid single source.
4. Is the simplified `Vd = Dose / (Cpost − Cpre)` acceptable for
   Pre+Post, or is the infusion-corrected version expected?

## Sources consulted

- [PhIS TDM Calculator Manual, 13th Edition (PDF)](https://phisportal.moh.gov.my/sites/default/files/phis_attachments_39556/PB_U.%20MANUAL_TDM%20CALCULATOR-13th%20E.pdf)
- [myTDM Calculator](https://www.mytdmcalculator.com/) — tool only, no published methodology
- [Vancomycin AUC Calculation — DoseMeRx](https://doseme-rx.com/vancomycin/article/vancomycin-auc-calculation)
- [Vancomycin Calculator Update: End of Infusion Peak — ClinCalc.com](https://clincalc.com/blog/2015/06/vancomycin-calculator-eoip/)
- [Concordance of Vancomycin Population-Predicted Pharmacokinetics with Patient-Specific Pharmacokinetics — PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC7221031/) — source for the Matzke equation
- [Section 2 — Vancomycin dosage calculation, rxkinetics.com](https://www.rxkinetics.com/pktutorial/2_6.html)
