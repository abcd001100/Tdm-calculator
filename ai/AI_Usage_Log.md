# AI Usage Log — TDM Insight

Prepared for: CDE2313 Mobile Application Development, Group Project
(Assessment 2), Albukhary International University.

> **Draft — review before submission.** This file was drafted by
> Claude Code to accurately record what happened during AI-assisted
> sessions, one section per team member. The factual sections (tools,
> prompts, suggestions adopted/rejected, mechanical modifications) are
> filled in from actual session/git history. Each member's
> **Modifications Made Personally** and **Reflection** subsections are
> left for that student to write themselves — an AI-authored reflection
> on "how AI supported learning" would defeat the point of the
> declaration. Export this file to PDF as `ai/AI_Usage_Log.pdf` once
> all three sections are reviewed and completed.

## AI Tool Used (all members)

**Claude Code** (Anthropic), model Claude Sonnet 5 — an interactive
CLI/IDE coding assistant, used across separate sessions by different
team members on their own branches.

---

## Abdulaziz Taju Mohammedyasin (AIU24102453) — `member1-abdulaziz`

### Purpose of Use

- Setting up the project from scratch: `CLAUDE.md` project instructions,
  repository scaffolding matching the Assessment Instructions' required
  structure, `docs/Implementation_Roadmap.md`, and
  `docs/Case_Study_Analysis.md`.
- Initializing the actual Android Studio/Gradle project (Phase 1),
  including diagnosing and fixing real environment problems: an
  incompatible Gradle/JDK pairing, then an AGP version newer than the
  installed Android Studio supported.
- Implementing Phase 3 (workflow selection screen, dynamic patient/dose
  input form, workflow-specific fields) against Member 2's validation
  and domain models.
- Researching the Vancomycin calculation methodology from the case
  study's named sources (PhIS TDM Calculator Manual, myTDM Calculator)
  and standard pharmacokinetics literature, and implementing the
  calculation engine's formulas (`TdmCalculationEngine.kt`) — including
  a correction, prompted by the student explicitly asking for the "real"
  (not simplified) formula, from an initial simplified Pre+Post volume
  of distribution calculation to the infusion-corrected, back-extrapolated
  version.
- Wiring the calculation engine into the input form and navigation
  (Phase 5 integration): running validation, then the engine, then
  carrying the result to the Results/Explanation screens.
- Drafting handoff files (untracked, never committed by the assistant)
  for teammates' domain/calculation and results-UI work, at the
  student's explicit request, so the student could distribute them.
- Ongoing git workflow support: syncing branches after teammates'
  merges, diagnosing merge conflicts caused by stale local copies of
  files teammates had already committed, and switching the team from a
  branch-per-task model to one persistent branch per member at the
  student's direction.

### Representative Prompts

- "create claude.md fire for your instraction with this [...]"
- "i want you to write everything out [...] and don't cause just a
  one time commit"
- "the run button (simbol is not clickable now)" (Android Studio
  troubleshooting)
- "you can write it but i want more the code to work like the app to
  work with a real formula"
- "also you have used some simplicity of formulas i want the real
  formulas"

### AI-Generated Suggestions — Adopted

- The three-persistent-branch git workflow (one per member, plus
  `main`), and the corresponding rewrite of `CLAUDE.md`'s GITHUB
  WORKFLOW section.
- Verifying every build/test claim against an actual `./gradlew` run
  rather than asserting it would work.
- Splitting handoff work into small, individually buildable commits
  (e.g. domain models, then validation, then the calculation engine,
  then tests, as separate commits) rather than one large dump.
- Downgrading AGP to the version the team's actual Android Studio
  installation supports, once diagnosed as the real cause of a broken
  IDE sync.

### AI-Generated Suggestions — Rejected / Corrected

- Claude initially framed the case study's "lecturer-approved
  authoritative sources" requirement as "the lecturer will provide the
  equations" — an overstatement not actually present in the source
  documents. The student challenged this directly, twice, and Claude
  corrected the claim to what the text actually says.
- Claude's first Pre+Post volume-of-distribution formula used a
  simplified `Vd = Dose / (Cmax − Cmin)` relationship and a Ke
  calculation that treated the pre-dose and post-dose samples as if
  they were on one continuous decay curve, ignoring that a dose is
  administered between them. The student asked for the "real" (not
  simplified) formula, which prompted a correction to the
  infusion-corrected, back-extrapolated two-point method — a genuine
  calculation error, not just a simplification, caught because the
  student pushed back rather than accepting the first answer.
- Claude proposed writing a "Calculation Method Proposal" document as
  if it were an assessment requirement; the student pointed out neither
  source document asks for such a document, and Claude corrected the
  framing to "a suggestion, not a requirement."
- Claude accidentally reverted the AGP version to conflict with the
  Android Studio version compatibility, and separately committed an
  Android project's initial files with a resources folder missing —
  both were caught by the student asking to double check the commit,
  and fixed via amend before anything was pushed.

### Modifications Made Personally

_(To be completed by Abdulaziz — what you personally reviewed, changed,
decided, or ran yourself. Examples to draw on: choosing to install the
Android 35 SDK platform yourself when Claude flagged it was missing;
deciding the package name and SDK versions; running the app in the
emulator yourself; deciding when to commit, push, and merge, and
explicitly controlling that process rather than delegating it.)_

### Reflection

_(To be written personally — not generated by AI. Consider: what did
you have to catch or correct in Claude's work? What do you now
understand about the calculation engine, the git workflow, or the
Android build toolchain that you didn't before? Do you agree with every
design decision Claude suggested, including the ones you pushed back
on?)_

---

## Amir Wuhab Nurhussen (AIU24102454) — `member2-amir`

### Purpose of Use

Based on the actual git history: the domain models, validation logic,
and calculation engine skeleton/formulas under `domain/model/`,
`domain/validation/`, and `domain/calculation/` were drafted by Claude
Code during Abdulaziz's session, at the student's request, as files
handed off outside the assistant (not committed by it). Amir's role
documented here is reviewing and committing that drafted code onto his
own branch, in small commits matching one logical change each, and
opening the pull requests that brought it into `main`.

### Representative Prompts

Not directly observed in this log — the prompts that produced the
drafted code came from Abdulaziz's session, not Amir's own. This
section should be completed by Amir if he used Claude directly himself
(e.g. while reviewing the handed-off code before committing).

### AI-Generated Suggestions — Adopted

- The domain model shape (`PatientInfo`, `DoseInfo`, `WorkflowInput`
  sealed class, `TdmResult`) and the validation approach (reusable
  field validators, per-workflow validators combining them), committed
  as-is after review.
- The calculation engine's initial structure: an interface plus
  `NotImplementedError` stubs naming exactly what clinical information
  was missing, rather than a guessed formula — committed as the
  starting point before formulas existed.

### AI-Generated Suggestions — Rejected / Corrected

_(To be completed by Amir — did you change anything in the drafted
code before committing it? Did you disagree with any naming, structure,
or validation rule and adjust it?)_

### Modifications Made Personally

_(To be completed by Amir — e.g. reviewing the drafted files before
committing, choosing the commit sequence/messages, running the tests
yourself before pushing, anything you changed from what was drafted.)_

### Reflection

_(To be written personally by Amir — not generated by AI. What did you
learn about the validation/calculation architecture by reviewing code
someone else's AI session drafted? What would you have built
differently?)_

---

## Bonson Adem Alo (AIU24102383) — `member3-bonson`

### Purpose of Use

- Reviewing existing code against `docs/Implementation_Roadmap.md` and
  `docs/Case_Study_Analysis.md` to check what was and wasn't
  implemented, phase by phase.
- Debugging git/branch issues (diverged branches, a stale personal
  branch missing teammates' merged work, merge conflicts).
- Writing and running Compose UI tests (`androidTest`) and JUnit unit
  tests, and using an actual Gradle build/emulator to verify them
  rather than assuming they'd pass.
- Implementing one Phase 6 optional enhancement (export/share a
  calculated result as plain text).
- Drafting documentation (this file's original Bonson section, and a
  `README.md` pass) for submission packaging.

### Representative Prompts

- "look at the code. know it inside out. then I need you to prepare a
  commit message for each of the files."
- "check if each and every phase has been implemented from the
  implementation road map."
- "so step by step, lets start with the things not implemented. start
  from phase 4 unfinished part."
- "phase 5 go"
- "do what is best and recommended, step by step" (for choosing a
  Phase 6 optional enhancement, and again for Phase 7 packaging).

### AI-Generated Suggestions — Adopted

- Splitting a batch of uncommitted changes into small, logically
  separate commits (matching `CLAUDE.md`'s one-feature-per-commit
  rule) rather than one large commit.
- Merging `origin/main` into `member3-bonson` (rather than rebasing) to
  safely pull in teammates' work on an already-pushed, shared personal
  branch.
- The specific UI test coverage added for `ResultsScreen`,
  `ExplanationScreen`, and a full end-to-end regression test
  (`EndToEndTest.kt`) driving all three workflows through the real
  calculation engine.
- The export/share feature's design: a plain (non-Composable) text
  formatter, kept separate from the UI, for testability — matching the
  project's existing separation-of-concerns pattern.

### AI-Generated Suggestions — Rejected / Corrected

- Claude initially suggested (and the student then committed) a test
  change renaming a callback parameter reference from `onContinue` to
  `onCalculated`, based on an incorrect assumption about what had
  already changed elsewhere in the code, without verifying the actual
  current function signature. This produced a real compile error,
  caught later when an actual Gradle build was run (not just assumed).
  It was corrected — twice, since the first correction also turned out
  to be based on a stale, unmerged branch state. **Lesson:** an AI
  suggestion about code it hasn't actually compiled/run should be
  verified against a real build before trusting it, which is exactly
  what surfaced and fixed this.
- Claude's first phase-completion assessment (Phase 2's calculation
  engine, Phase 5's integration) was based on reading a stale local
  git branch and incorrectly reported both as "not implemented," when
  teammates had already completed and merged them into `main`. This
  was caught, corrected, and explained once the actual git history was
  checked properly (`git log`, comparing branches) instead of trusting
  a single local file read.

### Modifications Made Personally

_(To be completed/expanded by Bonson — what you personally reviewed,
changed, decided, or ran yourself, beyond what's listed above. Examples
to draw on: choosing to merge rather than rebase; deciding when to
commit and push, and explicitly instructing Claude not to commit
without asking; running `git pull`, `git commit`, and `git push`
yourself rather than letting the assistant do it.)_

### Reflection

_(To be written personally by Bonson — not generated by AI. The rubric
asks specifically for a reflection on how AI supported learning, and a
critical evaluation of its suggestions. Points to consider: What did
you have to catch or correct? What would you have done differently
without AI assistance? What do you now understand about the codebase
that you didn't before reviewing the AI's work? Do you agree with every
design decision it suggested?)_
