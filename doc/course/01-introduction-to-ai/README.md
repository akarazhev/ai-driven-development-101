# 01. Introduction to AI

A practical overview of how modern language models and related tools accelerate software work.
You will learn core ideas—tokens, context windows, prompting, and safety—and how to apply
them in day-to-day engineering with Python 3.13, Cursor, and contex 7.

The emphasis is pragmatic: adopt simple concepts you can immediately use, build a habit of
evaluating outputs, and integrate AI into repeatable workflows.

## Learning objectives

- Explain tokens, tokenization, context windows, and message roles (system/user).
- Describe generation controls: temperature, top_p, and max_tokens.
- Decide when to use direct prompting vs. tools/agents vs. manual coding.
- Write effective prompts with role, task, constraints, examples, and evaluation criteria.
- Run an iterative loop: draft → test → critique → refine; keep brief notes.
- Apply basic safety: avoid PII/secrets, check licensing, validate factual claims.

## Prerequisites

- Python 3.13 installed
- Basic Git and terminal usage

## Outline

- LLM fundamentals and key terminology
- Prompting basics and evaluation practices
- Safety, privacy, and responsible AI usage

## Key concepts and terminology

- Token: a small unit of text; model limits and cost are measured in tokens.
- Context window: maximum tokens for prompt + response; truncation can change behavior.
- Roles/messages: system/instructions, user, tool, and examples; structure prompts deliberately.
- Generation controls: temperature/top_p for variability; max_tokens to cap length.
- Tool use vs plain generation: calling functions/APIs vs free-form text.
- Hallucination: confident but incorrect output; validate and constrain.
- Cost/latency: prefer concise prompts and cached artifacts when possible.

## Prompting basics

### A minimal prompt template

```text
Role: [who is the assistant?]
Task: [what to produce?]
Constraints: [rules on style, scope, limits]
Inputs: [data/context the model must use]
Steps: [optional short plan]
Output format: [e.g., markdown sections, JSON schema, code file]
Evaluation: [acceptance criteria or tests]
```

### Good vs weak prompt

Weak:

```text
Write code to count words.
```

Good:

```text
Role: Senior Python engineer
Task: Implement count_words(text: str) -> int for Python 3.13
Constraints:
- Include docstring and type hints
- Ignore punctuation, treat multiple whitespace as one separator
- Add O(n) solution and a doctest
Output format: Python code only
Evaluation: Provide 3 doctests that pass
```

## The evaluation loop

1) Define success criteria and a tiny test set.
2) Run the prompt; record inputs, params, outputs.
3) Compare against criteria; identify failure modes.
4) Refine prompt or constraints; repeat.

## Responsible and safe use

- Do not paste secrets or personal data into prompts.
- Check licenses for generated or retrieved code.
- Cite sources for factual claims when possible.
- Prefer deterministic checks (tests, linters) over intuition.

## Exercises

### Exercise 1: Iterative prompting

Goal: Implement a robust `slugify(text: str) -> str` in Python 3.13.

Steps:

- Draft a prompt using the template above.
- Require docs, types, unit tests, and edge-case handling (Unicode, whitespace, punctuation).
- Run; note failures; refine; repeat until tests pass.

Deliverable: Final prompt, code, and a short note on iterations.

### Exercise 2: Parameter exploration

Run the same prompt with temperature 0.0 and 0.8; compare outputs.
Note differences in determinism, style, and defects.

### Exercise 3: Safety

Prompt the model to redact emails and phone numbers from this text:

```text
Contact: Jane Doe, jane@example.com, +1 (415) 555-1212.
```

Add acceptance criteria: all PII is removed without altering other content.

## Knowledge check (self-assessment)

- What is a token and why does it matter?
- How does temperature influence output?
- What risks cause hallucination and how can you mitigate them?
- What belongs in constraints vs inputs?
- Why should you keep an evaluation set?

## Glossary

- Token, context window, temperature, top_p, hallucination, tool use.

## References

- Official Python documentation
- Your organization's Responsible AI guidelines
- Model provider documentation for your chosen LLM
- Prompting best practices from your provider
