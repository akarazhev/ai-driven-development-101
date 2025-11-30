# 03. Setup Cursor AI

Step-by-step setup of Cursor for effective AI pair programming in this course.
You will install the editor, prepare a Python 3.13 environment, configure models/parameters,
adopt prompt templates, and follow session hygiene and troubleshooting practices.

## Learning objectives

- Install Cursor and verify AI features are available.
- Open a project/workspace and enable indexing/trust for the repository.
- Configure Python 3.13 virtual environment and interpreter selection.
- Choose model/parameters appropriate for coding vs ideation.
- Apply prompt templates for code changes and bugfix loops.
- Practice session hygiene: small diffs, clear acceptance criteria, and frequent commits.
- Troubleshoot common issues: auth, rate limits, context window, and indexing.

## Prerequisites

- macOS, Git installed
- Python 3.13 installed

## Outline

- Installation and sign-in
- Project/workspace configuration
- Python 3.13 environment setup
- Model and parameter guidance
- Prompt templates for repeatable requests
- Tips, keybindings, and session hygiene
- Troubleshooting and diagnostics
- Checklists (first-time, per-project)

## Installation

- Download and install Cursor from the official site.
- Launch Cursor, sign in, and ensure AI features are enabled.
- Optional: enable automatic updates.

## Project/workspace configuration

- Open this repository folder in Cursor.
- Trust the folder if prompted and allow it to index files for context.
- Verify that the project structure appears in the sidebar and search works.

## Python 3.13 environment setup

In a terminal for this project directory:

```bash
python3.13 --version
python3.13 -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip
```

- If the editor supports interpreter selection, point it to `.venv`.
- Keep dependencies minimal until needed; add packages chapter-by-chapter.

## Model and parameter guidance

- Choose a reliable coding-capable model as default.
- Parameters:
    - Temperature: 0.0–0.3 for deterministic code; higher for brainstorming.
    - Top_p: leave default unless you have a reason to adjust.
    - Max tokens: cap long outputs to keep diffs manageable.
- Use selection-scoped prompts for targeted edits; prefer file/project scope only when necessary.

## Prompt templates

Keep these templates handy to standardize requests.

### Code change request

```text
Role: Senior Python engineer
Task: [describe the code change precisely]
Constraints:
- Python 3.13 compatibility; include types and docstrings where relevant
- Keep diffs minimal and focused
- Add/adjust tests if behavior changes
Inputs: [links/paths to relevant files, excerpts]
Output format: [patch/diff or code block]; summarize key changes
Evaluation: [acceptance criteria or tests]
```

### Bugfix loop

```text
Role: Debugging assistant
Task: Identify root cause and minimal fix
Constraints:
- Propose hypotheses, request missing context
- Show before/after behavior and tests
Inputs: [error logs, stack traces, code excerpts]
Steps: [1-3 step plan]
Output format: rationale + minimal patch + test update
Evaluation: failing test becomes green; no regressions
```

## Tips, keybindings, and session hygiene

- Work in small batches; keep a clear acceptance criterion per request.
- Highlight the specific code region before invoking AI for precise edits.
- Prefer deterministic parameters for refactors; raise temperature for ideation.
- Review diffs carefully; revert or iterate if the change is too broad.
- Avoid pasting secrets/PII; use placeholders or redact.
- Commit frequently with meaningful messages.

## Troubleshooting and diagnostics

- Authentication/rate limits: retry later or reduce request frequency.
- Context window: trim prompt content; include only relevant code snippets.
- Indexing/search issues: re-index the workspace; ensure the folder is trusted.
- Stale suggestions: provide fresh, minimal context and restate constraints.
- Network/proxy: verify connectivity and configure proxy if required.

## Checklists

- First-time install
    - [ ] Installed and signed in
    - [ ] AI features enabled
    - [ ] Updates checked

- Per-project
    - [ ] Folder trusted and indexed
    - [ ] Python 3.13 venv created and selected
    - [ ] Model/parameters set for coding
    - [ ] Prompt templates ready

## Exercises

### Exercise 1: Clean setup

- Install Cursor, open this repository, and trust/index the workspace.
- Create a `.venv` with Python 3.13 and select it in the editor if applicable.
- Record your model/parameter defaults.

### Exercise 2: AI-assisted refactor

- Choose a small function in any sample file (or create one in a scratch file).
- Use the “Code change request” template to improve readability and add types/docstrings.
- Review the diff; iterate to reduce changes to only what you requested.

### Exercise 3: Bugfix loop

- Reproduce a small error locally (e.g., failing doctest or unit test).
- Use the “Bugfix loop” template to isolate root cause and propose a minimal patch.
- Validate that tests pass and no unrelated files changed.

### Exercise 4: Parameters

- Run the same refactor at temperature 0.0 vs 0.7; compare determinism and style.
- Note which setting you prefer for refactors vs brainstorming.

## Knowledge check (self-assessment)

- How do you constrain AI edits to a small, relevant context?
- When would you raise temperature vs keep it near zero?
- What steps reduce risk of over-broad edits?
- How do you recover from indexing or context window issues?

## References

- Cursor official website and product documentation
- Python 3.13 virtual environments (venv) documentation
- Your organization’s coding standards and Responsible AI policy
