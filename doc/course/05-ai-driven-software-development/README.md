# 05. AI-Driven Software Development

A practical playbook for using AI across the software lifecycle.
Emphasis on repeatable prompts, measurable workflows, and safe-by-default practices
while working with Python 3.13 and modern tooling.

## Learning objectives

- Apply prompting patterns for design, coding, docs, and tests.
- Use AI for refactoring, debugging, and code review with clear acceptance criteria.
- Establish repeatable, measurable AI workflows with evaluation artifacts.
- Integrate safety, security, and compliance into the workflow.
- Track and improve productivity with lightweight metrics.

## Prerequisites

- Chapters 01–04 completed

## Outline

- Principles for AI-driven work
- Prompt patterns and templates
- AI-driven dev workflow (spec → implement → test → review → refine)
- Testing and evaluation strategies
- Code quality, security, and compliance
- Governance and PR workflow
- Anti-patterns to avoid
- Checklists

## Principles for AI-driven work

- Keep scope tight with explicit acceptance criteria.
- Work in small diffs; iterate rapidly; commit frequently.
- Prefer deterministic params for code; raise variability for ideation only.
- Validate with tests and linters; avoid relying on intuition.
- Record prompts and decisions for traceability.

## Prompt patterns and templates

### Design spec request

```text
Role: Tech lead
Task: Draft a 1–2 page design for [feature]
Constraints:
- Include goals, non-goals, data model, API surface, risks, and test plan
- Keep it practical; avoid speculation
Inputs: [requirements, current code links]
Output: Markdown sections
Evaluation: Addresses goals and has concrete acceptance criteria
```

### Code change request

```text
Role: Senior Python engineer
Task: Implement [feature/change] in Python 3.13
Constraints:
- Add/adjust unit tests
- Keep diff minimal; include types and docstrings
Inputs: [file paths, relevant snippets]
Output: Patch summary + code
Evaluation: Tests pass and acceptance criteria met
```

### Test-first (TDD) prompt

```text
Role: Test engineer
Task: Write unit tests for [behavior]
Constraints:
- Use pytest or unittest style (pick one and be consistent)
- Cover nominal and edge cases
Inputs: [public API, examples]
Output: Test file contents
Evaluation: Failing tests that define expected behavior
```

### Refactor request

```text
Role: Refactoring assistant
Task: Improve readability and performance of [function/module]
Constraints:
- Preserve behavior; add or keep tests
- Explain the changes briefly
Inputs: [current code]
Output: Minimal diff + rationale
Evaluation: Tests remain green; complexity reduced
```

### Bugfix loop

```text
Role: Debugging assistant
Task: Identify root cause and minimal fix for [bug]
Constraints:
- Propose hypotheses; request missing context
- Provide before/after tests
Inputs: [stack trace, code excerpts]
Steps: [1–3 step plan]
Output: Root cause, minimal patch, updated tests
Evaluation: Repro test turns green; no unrelated changes
```

### Code review prompt

```text
Role: Code reviewer
Task: Review the diff for correctness, security, and style
Constraints:
- Call out risky areas and missing tests
- Suggest targeted improvements only
Inputs: [diff or file excerpts]
Output: Bullet feedback + risk assessment
Evaluation: Actionable comments; no nitpicks
```

## AI-driven development workflow

1) Plan: define a small slice with acceptance criteria.
2) Spec: draft a brief design/spec if non-trivial.
3) Implement: request a minimal change; keep scope tight.
4) Test: write/adjust tests; run locally; capture results.
5) Review: request AI-assisted review; address findings.
6) Refine: iterate to reduce complexity and tighten tests.
7) Document: update README/changelogs if needed.

## Testing and evaluation strategies

- Golden cases: a tiny set exercised on each iteration.
- Coverage: focus on critical paths; avoid chasing 100%.
- Regression tests: capture previous failures as tests.
- Evaluation log: record prompt, params, outputs, and pass/fail.

Evaluation template

```text
Task: [what changed]
Criteria: [bullets]
Tests: [list]
Result: [pass/fail]
Notes: [issues, follow-ups]
```

## Code quality, security, and compliance

- Static analysis and linters; type checking where applicable.
- Secrets scanning; never paste secrets in prompts.
- Dependency policy and license compliance.
- Input/output validation for any AI-assisted features.
- Document data handling and privacy considerations.

## Governance and PR workflow

- Commit messages: concise summary + context.
- PR template: goals, screenshots/logs, tests, risks, rollback plan.
- Traceability: link to prompts/specs; summarize changes.
- Require green tests and at least one review (human/AI-assisted).

## Anti-patterns

- Over-broad prompts that change unrelated code.
- No acceptance criteria; vague requests.
- Skipping tests; relying on manual inspection.
- Blind copy-paste from external sources without license/validation.

## Checklists

- Per-task
    - [ ] Acceptance criteria defined
    - [ ] Minimal prompt drafted
    - [ ] Tests updated/added
    - [ ] Diff small and focused
    - [ ] Evaluation log updated

- Pull request
    - [ ] Description with goals and screenshots/logs
    - [ ] Tests green; coverage acceptable
    - [ ] Risk assessment and rollback noted
    - [ ] Prompts/specs linked

## Exercises

### Exercise 1: Feature slice

Implement a small feature using the workflow above.
Steps:

- Draft acceptance criteria and a brief spec.
- Implement with a minimal diff; add tests; run locally.
- Request a code review and apply feedback.
  Deliverable: PR with links to prompts and evaluation log.

### Exercise 2: TDD with AI

Write tests first for a new utility, then implement until green.
Include at least one edge case and a regression test.

### Exercise 3: Safe refactor

Refactor a function for clarity/performance.
Keep behavior identical; prove with tests and a brief rationale.

### Exercise 4: Bugfix and evaluation

Reproduce a bug, write a failing test, fix minimally, and update the evaluation log.

## Knowledge check (self-assessment)

- What belongs in acceptance criteria vs prompt constraints?
- When do you choose deterministic params vs exploratory ones?
- How do you prevent over-broad edits in practice?
- What artifacts ensure traceability and compliance?

## References

- Python testing (unittest/pytest) documentation
- Secure coding and secrets management guidelines (e.g., OWASP)
- License compliance basics and SBOM resources
- Model provider docs on prompting and evaluation
