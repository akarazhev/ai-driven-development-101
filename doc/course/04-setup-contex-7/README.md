# 04. Setup contex 7

Configure contex 7 for this course and integrate it with your editor and projects.
This chapter focuses on verifiable, tool-agnostic practices: installation/verification,
project linking, safe defaults, integration with Cursor and Python 3.13, and validation.

## Learning objectives

- Install and verify contex 7 availability on macOS.
- Link a project and align configuration with course conventions.
- Integrate usage with Cursor workflows and Python 3.13 environments.
- Apply safe defaults (e.g., redaction, logging) if supported by your setup.
- Validate end-to-end usage on a sample repository.
- Troubleshoot common issues (auth, permissions, network, indexing/config).

## Prerequisites

- Chapter 03 completed
- Python 3.13 installed and project virtual environment set up

## Outline

- Installation and verification
- Project linking and settings
- Integration with Cursor and Python 3.13
- Prompt templates for repeatable requests
- Validation steps and troubleshooting
- Checklists (first-time, per-project)
- References

## Installation and verification

- Follow the official contex 7 installation instructions for macOS.
- After installation, verify that contex 7 is accessible (per official docs).
- Record the installed version and where configuration lives (global vs per-project).
- If an editor extension exists for your setup, enable it; otherwise, proceed with CLI/API usage as applicable.

## Project linking and settings

- Open the target repository in your editor (Cursor) and terminal.
- If contex 7 supports per-project configuration files, store them in the repo (e.g., under a config directory) and
  document ownership.
- Decide on include/exclude patterns for files/folders to scope context and reduce noise.
- Establish standard locations for logs and artifacts (avoid committing large artifacts).
- Ensure sensitive data is not collected; apply redaction or masking features if available.

## Integration with Cursor and Python 3.13

- Keep your Python 3.13 virtual environment active when working in this project.
- In Cursor, prefer selection-scoped prompts to keep requests focused and auditable.
- If contex 7 offers APIs/SDKs you will use in code, add dependencies gradually and pin versions.
- Document any environment variables needed (do not hardcode secrets; use your org’s secret management).

## Prompt templates

Use these templates to standardize requests related to contex 7 without relying on tool-specific commands.

### Configuration request

```text
Role: Project maintainer
Task: Propose a minimal, safe configuration for contex 7 in this repository
Constraints:
- Align with course conventions (scoped context, redaction/masking where possible)
- Keep settings explicit and documented
Inputs: [repository structure overview, policies, folders to include/exclude]
Output format: Markdown section with settings and rationale
Evaluation: Checklist of safety and scope considerations is satisfied
```

### Validation checklist

```text
Role: QA engineer
Task: Validate that contex 7 integrates correctly with this project
Constraints:
- No secrets/PII captured; logs are redacted where needed
- Requests operate within intended file scope
Steps:
- List test cases and acceptance criteria
Output format: Pass/Fail table with notes and follow-ups
```

## Troubleshooting and diagnostics

- Authentication/permissions: confirm account, tokens/keys (if applicable), and scopes.
- Network/proxy/firewall: verify outbound connectivity to required domains.
- Configuration path: verify you are editing the correct config (global vs project).
- Context scope: if results feel noisy, tighten include/exclude patterns.
- Editor integration: ensure the extension (if any) is enabled and updated.
- Logs: review redacted logs/artifacts; increase verbosity only as needed.

## Checklists

- First-time install
    - [ ] Installed and verified availability
    - [ ] Recorded version and configuration location
    - [ ] (If applicable) Editor extension enabled

- Per-project
    - [ ] Configuration aligned with course conventions
    - [ ] Include/exclude patterns defined
    - [ ] Redaction/masking enabled where applicable
    - [ ] Logs/artifacts location documented (and gitignored if needed)
    - [ ] Environment variables documented (no secrets in repo)

## Exercises

### Exercise 1: Clean install and verify

- Install contex 7 following the official instructions.
- Verify availability and note version and config paths.

### Exercise 2: Project configuration

- Create or refine a minimal per-project configuration aligned with course conventions.
- Define include/exclude patterns and logging/redaction policies.

### Exercise 3: Editor integration

- If an extension exists, enable it and perform a small, scoped request in Cursor.
- Record observations and any constraints you applied in the prompt.

### Exercise 4: Validation run

- Use the “Validation checklist” template to confirm expected behavior and safety.
- Capture Pass/Fail and follow-ups.

## Knowledge check (self-assessment)

- Where does contex 7 store configuration (global vs per-project) in your setup?
- How do you ensure secrets/PII are not collected or logged?
- What patterns help reduce noisy or irrelevant context?
- How do you recover if the editor integration stops responding?

## References

- Official contex 7 documentation (installation, configuration, usage)
- Your organization’s policies for secrets, logging, and privacy
- Editor (Cursor) documentation on workspace trust and extensions
