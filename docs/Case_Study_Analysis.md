# Case Study Analysis

Source documents:

- `docs/assessment/Assessment_Instructions.pdf`
- `docs/assessment/Case_Study_Description.pdf`

This document summarizes the mandatory and optional requirements for
TDM Insight, as interpreted directly from the two source documents
above. Nothing here is invented — where the source documents do not
specify something, that gap is called out explicitly rather than
guessed at.

## 1. Mandatory Requirements

**Core scope (Case Study §2):**

- Native Android development using Kotlin, Android Studio, and
  Jetpack Compose/XML.
- Vancomycin TDM calculation workflows — exactly three: **Pre**,
  **Post**, and **Pre + Post**.
- Dynamic input forms that change based on the selected calculation
  method. Do not show every field at once.
- Input validation and cross-field validation (see §6 below — more
  than just checking for empty fields).
- A dedicated calculation engine, structurally separated from the UI:
  `UI → Input State → Validation → TDM Calculation Engine → Result Model → Results UI`.
  Calculation logic must not live inside Composables.
- Intermediate **and** final pharmacokinetic results displayed — not
  just a final number.
- A calculation explanation screen:
  `Input Values → Intermediate Values → Pharmacokinetic Parameters → Final Result`.
- Clean, intuitive Material 3 UI.

**Example user flow (Case Study §4):** open app → create fictional
case → enter patient parameters → select Vancomycin → select
Pre/Post/Pre+Post → enter workflow-specific values → validate → review
inputs → run calculation → view intermediate results → open
explanation.

**Explicitly NOT required for core scope (Case Study §2):**

- User authentication.
- Cloud backend or online services.
- Analytics dashboards.
- Complex patient-management systems.
- Multiple medication modules.
- Large educational-content modules.

## 2. Optional Requirements (postponed)

Per Case Study §10 — may be added only after mandatory requirements
work, per the project's implementation roadmap:

- Local calculation history.
- Camera capture of a fictional laboratory report or medication label.
- OCR-assisted extraction of a selected value, with **mandatory user
  confirmation** before use:
  `Camera → Capture → Review → Confirm Value → Use in Calculation`.
- What-if / scenario simulation.
- Additional TDM medication module.
- Simple concentration-time graph.
- Export or sharing of a calculation summary.

## 3. Technical Requirements

- Platform: Native Android.
- Language: Kotlin.
- IDE: Android Studio.
- UI toolkit: Jetpack Compose (this project's choice — see
  `CLAUDE.md`; the case study also permits XML).
- Design system: Material 3.
- The Assessment Instructions additionally require the app to
  "demonstrate competency in Android Studio by incorporating
  appropriate Android components" — no specific components are named
  beyond this.
- **Unspecified:** minimum/target SDK, architecture pattern (e.g.
  MVVM), and any third-party library. These are left to the team's
  judgement; see the implementation roadmap for the chosen defaults.

## 4. GitHub Requirements

From Assessment Instructions ("Structure/Format" and "Specific
Requirements"):

- Required repository structure (see §9 below).
- `README.md` as the project landing page, containing: project title;
  course information; group members (name, student ID); case study
  problem overview and summary of implemented solutions; key
  implemented features; technology stack and architecture;
  installation guide; how to build; APK download; screenshots;
  repository structure; acknowledgements; references (APA 7th
  Edition, if applicable).
- The repository is the **primary submission** and evidence of
  development.
- Consistent Git commit history demonstrating continuous development
  — not one large dump.
- Individual contribution must be demonstrable through: Git commit
  history, code ownership, pull requests, issues completed,
  participation during demonstration, individual technical viva.
- Repository must remain accessible until final grades are released.
- Submission via AIU Moodle LMS: GitHub Repository URL, plus a PDF
  containing that URL, each member's Android Developer Profile URL,
  and Badge Summary.
- **Deadline:** Week 6 — 18 September 2026, by/before 11:59 PM. Late
  penalty: 5 points/day.

## 5. UI/UX Requirements

- Material 3 design system.
- Consistent, user-friendly interface (Assessment Instructions).
- Clean and intuitive UI (Case Study).
- Rubric ("User Interface (UI) & User Experience (UX) Design", 10%)
  expects: professional, visually appealing, intuitive, responsive UI;
  seamless navigation that enhances the user's experience.
- Dynamic forms: the input screen must adapt to the selected workflow
  rather than showing a static superset of fields.

## 6. Testing Requirements

- "Students shall perform functional testing before submission"
  (Assessment Instructions).
- Rubric ("Testing & Debugging", 5%) expects: thorough testing,
  comprehensive validation, effective error handling, evidence of a
  stable application.
- Validation specifics required by the Case Study (§8):
  1. Required-field validation.
  2. Numeric and unit validation.
  3. Range validation where specified.
  4. Cross-field validation (e.g. logical timing relationships).
  5. Detection of missing values required by the selected workflow.
  6. Protection against mathematical errors (division by zero,
     invalid logarithmic operations).
  7. Clear messages distinguishing an actual error from information
     that simply needs review.
- **Unspecified:** a specific test framework or coverage target. The
  roadmap uses standard Android tooling (JUnit for unit tests,
  Compose/Espresso for UI tests) as a reasonable default.

## 7. AI Usage Requirements

From Assessment Instructions ("Academic Integrity"):

- Generative AI is permitted **only** as a learning-support tool, not
  a substitute for the students' own analytical thinking or practical
  work.
- **Note — internal inconsistency in the source document:** the
  "Generative AI" summary line states students "must not use
  generative artificial intelligence (AI) to generate any materials
  or content in relation to the assessment task," while the paragraph
  immediately below it explicitly permits AI tools (naming Claude
  among them) for specific supportive uses. This contradiction is
  recorded here rather than resolved — clarify with the lecturer which
  statement governs before relying on either reading.
- Permitted AI uses: learning Android programming concepts;
  understanding Android APIs; debugging code; explaining programming
  concepts; generating small code examples; improving code
  readability; refactoring existing code; generating UI design ideas.
- Prohibited: generating/submitting an entire application via AI
  without understanding it; presenting AI-generated work as entirely
  one's own; using AI to answer questions during live demonstration or
  individual technical viva (unless explicitly permitted); fabricating
  Git commit histories or repository activity.
- Required deliverable: an **AI Usage Log** (`ai/AI_Usage_Log.pdf`)
  documenting — AI tool(s) used, purpose of use, representative
  prompts, AI-generated suggestions adopted or rejected, modifications
  made by students, and a reflection on how AI supported learning.
- Students remain fully responsible for correctness, quality, and
  integrity of all submitted work, including AI-assisted code.
- Plagiarism checked via Turnitin; acceptable collaboration level must
  not exceed 30%.
- Rubric ("Responsible AI Usage & Professional Practice", 5%)
  evaluates transparency, completeness/reflectiveness of the AI Usage
  Log, and critical evaluation of AI suggestions.

## 8. Clinical Calculation Constraints

From Case Study §6 and §14:

- Possible pharmacokinetic outputs (workflow-dependent): elimination
  rate constant (Ke), elimination half-life, volume of distribution
  (Vd), clearance, concentration-related values, AUC-related values,
  and any other parameters required by the selected TDM method.
- The **exact clinical equations, units, assumptions, and reference
  values must come from lecturer-approved authoritative sources** —
  students must not invent clinical formulas.
- Reference starting points named for domain understanding (not
  pre-approved formula sources by default): myTDM Calculator
  (<https://www.mytdmcalculator.com/>) and the PhIS (Malaysian
  Pharmacy Information System) TDM Calculator Manual.
- Students must consult current authoritative clinical guidance for
  any clinical equation or target used.
- Demonstration/test cases must be fictional.
- **Mandatory disclaimer:** TDM Insight is an academic software
  prototype for educational/software development purposes only — it
  must not be presented as a clinically validated prescribing,
  diagnostic, or autonomous treatment-decision system.
- **Gap:** neither source document supplies the actual Vancomycin
  Pre/Post/Pre+Post equations, target ranges, or units. Per
  `CLAUDE.md`'s clinical-calculation rule, implementation of the
  calculation engine's formula bodies stops until these are obtained
  from the lecturer. See Phase 2.3 in the implementation roadmap.

## 9. Required Repository Structure

Given verbatim in the Assessment Instructions:

```
MobileAppProject/
│
├── README.md
├── LICENCE
├── .gitignore
│
├── app/                     # Android Studio Project
├── gradle/                  # Gradle configuration
│
├── screenshots/             # Screenshots for the mobile app
│
├── docs/                    # Related documentation
│   ├── Case_Study_Analysis.md
│   ├── wireframe/
│   └── diagrams/
│
├── apk/                     # Release-ready APK file
│   └── app-release.apk
│
├── presentation/            # Presentation slides for final demonstration
│   ├── Presentation.pptx
│   └── Presentation.pdf
│
├── ai/                      # Concise AI usage declaration
│   └── AI_Usage_Log.pdf
│
└── assets/                  # Store supporting resources
```

This structure was scaffolded in Phase 0.1. The remaining gap is the
`app/` and `gradle/` directories, which land in Phase 1.1 when the
Android Studio project itself is initialized.

## 10. Recommended Implementation Phases

Not prescribed verbatim by either source document — this sequencing
is a suggested plan, distinguished from official requirements, and is
maintained in full detail in `docs/Implementation_Roadmap.md`. In
summary: repository scaffolding → Android project foundation → domain
models and validation → calculation engine (blocked pending
lecturer-approved equations) → dynamic input forms → results and
explanation UI → integration → testing → optional enhancements (only
after mandatory scope is complete) → submission packaging.
