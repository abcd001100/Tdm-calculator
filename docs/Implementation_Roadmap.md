# TDM Insight — Implementation Roadmap

Three-developer build plan for the Vancomycin TDM calculator. Mandatory
requirements are sequenced first; optional enhancements are postponed to
the end. Tasks are sized for one branch and one focused PR (or a short
series of related commits) each, with file ownership kept separate
wherever the architecture allows it.

This roadmap sequences the work — it does not implement any of it.
No clinical equation is assumed anywhere in Phase 2 or 5 until the
lecturer supplies it.

## Legend

- **Member 1** — application foundation, navigation, input flow, dynamic forms
- **Member 2** — domain models, validation, TDM calculation engine, calculation tests
- **Member 3** — results UI, calculation explanation, UI/UX testing
- 🔒 **Blocked** — cannot proceed without lecturer-approved clinical input
- 🔗 **Integration point** — touches a file or contract shared across members

## Sequencing at a glance

```
Phase 0  Scaffold  (shared)
   │
Phase 1  Foundation (Member 1)   ← hard gate, everyone waits on this
   │
   ├── Phase 2  Domain & Engine   (Member 2)   ─┐
   ├── Phase 3  Input Flow        (Member 1)    ├─ run in parallel
   └── Phase 4  Results & UX      (Member 3)   ─┘
   │
Phase 5  Integration (Member 2 + Member 3, paired)
   │
Phase 6  Optional enhancements (postponed until 0–5 are done)
   │
Phase 7  Submission packaging (shared, final)
```

---

## Testing tasks (summary)

| Task | What |
|---|---|
| 1.1 | `./gradlew assembleDebug` succeeds after project init |
| 2.2 | Unit tests: required, numeric, range, cross-field, division-by-zero |
| 2.3+ | Calculation engine unit tests, once formulas are approved and implemented |
| 3.1–3.3 | Manual UI check — only the selected workflow's fields render |
| 4.3 | Compose/Espresso UI smoke tests across input and results flows |
| 5.1 | Manual end-to-end pass per workflow, real engine output wired in |
| 5.2 | Full regression across all three workflows, valid and invalid inputs |

## Integration points (summary)

| Task | What |
|---|---|
| 1.1 | Hard gate — nobody branches Domain, Input Flow or Results work until this merges |
| 2.1 | `ResultModel` is a contract: Member 2 defines it, Member 3 builds Results UI against it |
| 1.2 / 3.1 / 4.1 | All three add routes to the same `NavGraph.kt` — keep additions additive, coordinate merge order |
| 5.1 | Member 2 and Member 3 pair to bind the real engine to the Results screen; both review the PR |

---

## Phase 0 — Repository & Documentation Scaffolding

**Owner:** shared — do first, merge before anyone branches
**Why first:** the Assessment Instructions specify an exact repository layout. Laying it down first means every later branch starts from the same base.

### 0.1 Add required repository structure
- Branch: `chore/repo-scaffold`
- Commits:
  - `chore(repo): add required top-level directory structure`
  - `docs(readme): add README skeleton with required sections`
  - `chore(license): add project license`
- PR title: `chore: scaffold repository structure per assessment requirements`
- Depends on: nothing — first task
- Files: `README.md`, `LICENCE`, `docs/Case_Study_Analysis.md`, `docs/wireframe/`, `docs/diagrams/`, `screenshots/`, `apk/`, `presentation/`, `ai/`, `assets/`
- Testing: none required — verify the tree against the Assessment Instructions structure diagram before merging.

### 0.2 Case study requirements analysis
- Branch: `docs/case-study-analysis`
- Commits: `docs(analysis): summarize mandatory and optional requirements`
- PR title: `docs: add case study requirements analysis`
- Depends on: 0.1
- Files: `docs/Case_Study_Analysis.md`
- Note: all three members should read this before opening their Phase 1/2 branches — it's the shared reference for what "mandatory" means.

---

## Phase 1 — Application Foundation

**Owner:** Member 1
**Why it's a gate:** nothing else in the project can start until the Android module itself exists.

### 1.1 Initialize Android project 🔗
- Branch: `feature/member1-project-init`
- Commits:
  - `feat(app): initialize Android Studio project with Compose and Material 3`
  - `feat(app): add base package structure and app theme`
- PR title: `feat(app): initialize Android project foundation`
- Depends on: 0.1
- Files: `settings.gradle.kts`, `build.gradle.kts` (root + app), `MainActivity.kt`, `ui/theme/*`
- Testing: `./gradlew assembleDebug` succeeds, app launches to a themed blank screen.
- **Hard gate** — Members 2 and 3 branch from this once it's merged, not before.

### 1.2 Navigation skeleton 🔗
- Branch: `feature/member1-navigation`
- Commits: `feat(nav): add navigation graph with placeholder screens`
- PR title: `feat(nav): add main application navigation`
- Depends on: 1.1
- Files: `ui/navigation/NavGraph.kt`, `ui/navigation/Routes.kt`, empty screen stubs
- Testing: manual — navigate between stub screens without a crash.
- `NavGraph.kt` will receive route additions from 3.1 and 4.1 later — keep those additive, not rewrites.

---

## Phase 2 — Domain Models, Validation & Calculation Engine

**Owner:** Member 2
**Runs in parallel** with Phase 3 and 4 once 1.1 merges — this layer has no dependency on navigation or UI.

### 2.1 Core domain models 🔗
- Branch: `feature/member2-domain-models`
- Commits:
  - `feat(domain): add patient and workflow input data models`
  - `feat(domain): add result model skeleton`
- PR title: `feat(domain): add core domain models for TDM workflows`
- Depends on: 1.1
- Files: `domain/model/*.kt`
- Testing: compiles, no runtime tests yet.
- Note the `ResultModel` shape in the PR description — Member 3 builds the Results screen against it before the engine is finished.

### 2.2 Input & cross-field validation
- Branch: `feature/member2-validation`
- Commits:
  - `feat(validation): add required-field and numeric validation`
  - `feat(validation): add cross-field and timing validation`
  - `feat(validation): add workflow-specific missing-field detection`
  - `test(validation): add unit tests for validation rules`
- PR title: `feat(validation): add input validation for TDM workflows`
- Depends on: 2.1
- Files: `domain/validation/*.kt`, `test/.../ValidationTest.kt`
- Testing: `./gradlew test` — required/numeric/range/cross-field cases plus division-by-zero and invalid-logarithm guards.

### 2.3 Calculation engine — interface only 🔒
- Branch: `feature/member2-calc-engine-skeleton`
- Commits: `feat(tdm): add calculation engine interface and result contracts`
- PR title: `feat(tdm): add TDM calculation engine skeleton`
- Depends on: 2.1
- Files: `domain/calculation/TdmCalculationEngine.kt` (interface), `VancomycinPreCalculator.kt` (stub)
- **Blocked:** the Vancomycin Pre / Post / Pre+Post equations, units and reference values are not in the assessment documents and must come from the lecturer before any formula body is written. This task defines signatures only, so UI work can proceed against a stable contract.

Once approved, three follow-on tasks unblock — each its own branch and PR:

| Workflow | Branch | Commits |
|---|---|---|
| Pre | `feature/member2-calc-pre` | `feat(tdm): implement vancomycin pre calculation` + `test(tdm): add vancomycin pre calculation tests` |
| Post | `feature/member2-calc-post` | `feat(tdm): implement vancomycin post calculation` + `test(tdm): add vancomycin post calculation tests` |
| Pre+Post | `feature/member2-calc-prepost` | `feat(tdm): implement vancomycin pre+post calculation` + `test(tdm): add vancomycin pre+post calculation tests` |

---

## Phase 3 — Input Flow & Dynamic Forms

**Owner:** Member 1
**Runs parallel** to Phase 2 and 4. Needs the domain models to bind form state and the validation rules to surface errors, so it trails 2.1/2.2 slightly even though it starts from the same Phase 1 base.

### 3.1 Workflow selection screen 🔗
- Branch: `feature/member1-workflow-selection`
- Commits: `feat(input): add vancomycin workflow selection screen`
- PR title: `feat(input): add workflow selection screen`
- Depends on: 1.2, 2.1
- Files: `ui/input/WorkflowSelectionScreen.kt`, + route in `NavGraph.kt`
- Testing: manual — each of Pre / Post / Pre+Post navigates to the right next screen.

### 3.2 Patient parameter form
- Branch: `feature/member1-patient-form`
- Commits: `feat(input): add patient parameter form`
- PR title: `feat(input): add dynamic patient parameter input form`
- Depends on: 2.1, 2.2
- Files: `ui/input/PatientFormScreen.kt`, `ui/input/PatientFormState.kt`
- Testing: manual — validation errors from 2.2 surface as understandable messages, not raw exceptions.

### 3.3 Workflow-specific dynamic fields
- Branch: `feature/member1-dynamic-fields`
- Commits:
  - `feat(input): add vancomycin pre workflow fields`
  - `feat(input): add vancomycin post workflow fields`
  - `feat(input): add vancomycin pre+post workflow fields`
- PR title: `feat(input): add workflow-specific dynamic input fields`
- Depends on: 3.2, 2.2
- Files: `ui/input/fields/*.kt`
- Testing: manual — only the selected workflow's fields render; switching workflows doesn't leak stale fields.

---

## Phase 4 — Results & Explanation UI

**Owner:** Member 3
**Runs parallel** to Phase 3. Built against the 2.1 `ResultModel` contract with mock data, so it doesn't wait on the blocked calculation engine — it gets re-verified once 5.1 wires in real output.

### 4.1 Results screen (mock data) 🔗
- Branch: `feature/member3-results-screen`
- Commits:
  - `feat(results): display intermediate pharmacokinetic results`
  - `feat(results): display final calculation result`
- PR title: `feat(results): add TDM results screen`
- Depends on: 1.2, 2.1
- Files: `ui/results/ResultsScreen.kt`, + route in `NavGraph.kt`
- Testing: manual, against fixture `ResultModel` data. Also touches `NavGraph.kt` — coordinate merge order with 1.2 and 3.1.

### 4.2 Calculation explanation screen
- Branch: `feature/member3-explanation`
- Commits: `feat(explanation): add calculation explanation screen`
- PR title: `feat(explanation): add explainable calculation results screen`
- Depends on: 4.1
- Files: `ui/results/ExplanationScreen.kt`
- Testing: manual — sequence reads Input Values → Intermediate Values → Pharmacokinetic Parameters → Final Result.

### 4.3 UI/UX testing pass
- Branch: `test/member3-ui-testing`
- Commits: `test(ui): add UI smoke tests for input and results flows`
- PR title: `test(ui): add UI/UX validation for TDM workflows`
- Depends on: 3.1, 3.2, 3.3, 4.1, 4.2
- Files: `androidTest/.../*.kt`
- Testing: Compose/Espresso UI tests covering form entry, navigation, and error-state rendering.

---

## Phase 5 — Integration

**Owner:** Member 2 + Member 3, paired
Where the three parallel tracks reconverge. Cannot start until the equations are approved and 2.3's follow-on calculation tasks land.

### 5.1 Wire calculation engine to results UI 🔗
- Branch: `integration/wire-calc-engine-to-results`
- Commits: `feat(integration): connect calculation engine output to results UI`
- PR title: `feat(integration): wire TDM calculation engine to results UI`
- Depends on: 2.3 (+ post-approval calc tasks), 4.1
- Files: `ui/results/ResultsViewModel.kt` (new — bridges domain and UI)
- Testing: manual end-to-end per workflow, plus re-running the 2.2/2.3 unit tests. Both members review before merge.

### 5.2 Full regression pass
- Branch: `test/full-regression`
- Commits: `test(e2e): add end-to-end workflow regression checks`
- PR title: `test: full regression pass for all vancomycin workflows`
- Depends on: 5.1
- Files: `androidTest/.../EndToEndTest.kt`
- Testing: all three workflows, valid and invalid input paths, explanation screen accuracy against the calculation output.

---

## Phase 6 — Optional Enhancements (postponed)

Not started until Phase 0–5 are complete and demoed. Listed as candidates only — none get a branch or commit plan until the mandatory workflows are verified working end to end.

| Enhancement | Candidate owner |
|---|---|
| Local calculation history | Member 3 |
| Fictional lab report camera capture | Member 1 |
| OCR-assisted extraction with mandatory user confirmation | Member 1 / 2 |
| What-if scenario simulation | Member 2 |
| Additional TDM medication module | Member 2 |
| Concentration-time graph | Member 3 |
| Export or share calculation summary | Member 3 |

Each gets its own small roadmap once the mandatory scope is confirmed stable — not before.

---

## Phase 7 — Submission Packaging

**Owner:** shared, final. The last pass before the Week 6 deadline.

- `apk/` — release APK (`app-release.apk`)
- `screenshots/` — captures of each workflow and the explanation screen
- `README.md` — final pass: install guide, build guide, architecture, features, APK link
- `ai/` — AI Usage Log (tools, prompts, adopted/rejected suggestions, reflection)
- `presentation/` — slides for the 10–15 minute demonstration
- Branch: `chore/submission-packaging`
- PR title: `chore: prepare submission package`

---

*Understand first. Plan second. Implement third. Test fourth. Commit fifth. Review sixth.*
