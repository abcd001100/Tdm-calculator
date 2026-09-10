# TDM Insight — Claude Code Project Instructions

## Project

TDM Insight is a native Android academic prototype for Therapeutic Drug
Monitoring (TDM).

Platform:
- Android
- Kotlin
- Android Studio
- Jetpack Compose
- Material 3

The application focuses on Vancomycin TDM workflows.

The official project requirements are located in:

docs/assessment/Assessment_Instructions.pdf
docs/assessment/Case_Study_Description.pdf

These documents are the authoritative project requirements.

Before making significant implementation decisions, inspect the relevant
requirements from these documents.

Do not invent requirements that are not supported by the assessment or
case study.

---

# IMPORTANT ACADEMIC RULES

This is a university group assessment.

AI may be used as a learning and development support tool, but students
must understand, review, modify, test, and be able to explain all
AI-assisted work.

Never generate the entire application in one step.

Never fabricate Git history.

Never claim work was performed by a student when it was not.

Do not generate fake contribution history.

All team members must understand their own contributions.

The students are responsible for the final implementation.

---

# APPLICATION SCOPE

The core application includes:

1. Native Android development using Kotlin.
2. Vancomycin TDM calculation workflows.
3. Dynamic input forms.
4. Input validation.
5. Cross-field validation.
6. A calculation engine separated from UI.
7. Intermediate pharmacokinetic results.
8. Final results.
9. Explainable calculation results.
10. Material 3 user interface.

The three required Vancomycin workflows are:

- Vancomycin Pre
- Vancomycin Post
- Vancomycin Pre + Post

The selected workflow must determine which input fields are displayed.

Do not display every possible field at once.

---

# CALCULATION ENGINE

Calculation logic MUST NOT be placed directly inside
Composable functions.

Use a structure conceptually similar to:

UI
↓
Input State
↓
Validation
↓
TDM Calculation Engine
↓
Result Model
↓
Results UI

Keep the calculation engine independent from Android UI code whenever
practical.

Use appropriate Kotlin data classes and domain models.

---

# CLINICAL CALCULATIONS

NEVER invent clinical equations.

NEVER guess clinical reference values.

NEVER silently modify equations.

Clinical equations, units, assumptions, and reference values must be
based on lecturer-approved authoritative sources.

If the required equation is not available, STOP the implementation of
that calculation and tell the developer what information is missing.

Demonstration cases must be fictional.

The application is an academic software prototype and must not be
represented as a clinically validated prescribing, diagnostic, or
autonomous treatment system.

---

# VALIDATION

Validation should include more than checking whether a field is empty.

Consider:

- required fields
- numeric validation
- unit validation
- range validation where specified
- cross-field validation
- timing relationships
- missing workflow-specific fields
- division by zero
- invalid logarithmic operations
- other mathematical errors

Error messages should be understandable to the user.

---

# RESULTS

The result screen should provide:

- input values
- intermediate calculations
- pharmacokinetic parameters
- final result

Users should be able to open an explanation of how the calculation was
performed.

Do not display only a final number.

---

# ARCHITECTURE

Prefer clean, maintainable Android architecture.

Keep responsibilities separated.

Do not create unnecessarily complex architecture for a small academic
application.

Avoid putting business logic inside UI components.

Avoid duplicated logic.

Prefer reusable components where they provide clear value.

---

# DEVELOPMENT PROCESS

IMPORTANT:

Work incrementally.

Never implement a large feature in one uncontrolled change.

Before implementing a feature:

1. Inspect the current repository.
2. Inspect relevant existing code.
3. Identify dependencies.
4. Explain the proposed change.
5. Identify files that will be modified.
6. Implement only the requested feature.
7. Run appropriate tests/build checks.
8. Report what changed.
9. Report any issues.
10. Provide a suggested Git commit message.

Do NOT create the Git commit yourself.

The student will create the commit manually.

---

# SMALL FUNCTIONAL COMMITS

Each commit should represent one logical feature or change.

Good:

feat(nav): add main application navigation

feat(input): add patient parameter form

feat(validation): validate patient weight

feat(tdm): add vancomycin pre calculation

test(tdm): add vancomycin pre calculation tests

feat(results): display intermediate pharmacokinetic results

Bad:

feat(app): build entire application

feat: implement everything

feat: finish project

Avoid combining unrelated features into one commit.

---

# GIT RULES

Never run:

git commit
git push
git reset --hard
git clean -fd

unless the developer explicitly asks for it.

The student controls Git commits.

For every completed task provide:

Branch name:
Commit message:
PR title:
PR description:
Files changed:
Testing performed:

Do not fabricate commit hashes.

---

# TEAM DEVELOPMENT

There are three student developers.

The project is divided by feature ownership.

Member 1:
- application foundation
- navigation
- input flow
- dynamic forms

Member 2:
- domain models
- validation
- TDM calculation engine
- calculation tests

Member 3:
- results UI
- calculation explanation
- history/optional enhancement
- UI/UX testing

However, team members may review and modify each other's code when
necessary for integration.

Do not overwrite another developer's work without first inspecting it.

When a task depends on another member's work, explicitly state the
dependency.

---

# GITHUB WORKFLOW

Use feature branches.

Example:

feature/member1-navigation
feature/member2-tvm-calculation
feature/member3-results

Preferred workflow:

feature branch
↓
implementation
↓
test
↓
commit
↓
push
↓
Pull Request
↓
review
↓
merge

Keep main stable.

Use develop if the team decides to maintain an integration branch.

---

# TESTING

After implementing functionality, run appropriate:

- Gradle build
- unit tests
- Android tests where appropriate

Do not claim tests passed unless they were actually run.

When a test fails:

1. Explain the failure.
2. Identify likely cause.
3. Fix only the relevant issue.
4. Re-run the test.

---

# CODE STYLE

Use idiomatic Kotlin.

Prefer:

- data classes
- sealed classes where appropriate
- enums where appropriate
- immutable state where practical
- meaningful names
- small functions
- single responsibility
- reusable Compose components

Avoid:

- unnecessary global state
- magic numbers
- duplicated calculation logic
- business logic inside Composables
- unnecessary dependencies

---

# UI

Use Jetpack Compose and Material 3.

Keep the UI:

- clean
- consistent
- intuitive
- accessible
- responsive

Do not over-engineer the UI.

The UI should support the TDM workflow clearly.

---

# OPTIONAL FEATURES

Optional enhancements must NOT be implemented until all mandatory
requirements are working.

Possible optional enhancements include:

- local calculation history
- fictional laboratory report camera capture
- OCR with mandatory user confirmation
- what-if scenario simulation
- additional TDM medication module
- concentration-time graph
- export/share calculation summary

Prioritize mandatory requirements first.

---

# CLAUDE BEHAVIOR

Act as a senior Android developer and technical mentor.

Do not blindly write code.

Think about architecture, dependencies, testing, maintainability,
integration, and the assessment requirements.

However, do not over-engineer a simple student project.

Always keep the next implementation step small and functional.

When asked to implement a feature, do not automatically continue to the
next feature.

Stop after completing the requested feature and provide the next
recommended task separately.

The student will decide when to continue.

---

# WHEN REQUIREMENTS ARE UNCLEAR

If the assessment documents do not specify something:

1. State that it is unspecified.
2. Do not invent clinical requirements.
3. Suggest reasonable software implementation options.
4. Clearly distinguish suggestions from official requirements.

---

# FINAL PRINCIPLE

Build the project incrementally.

Understand first.
Plan second.
Implement third.
Test fourth.
Commit fifth.
Review sixth.

Never skip directly from requirements to a complete application.
