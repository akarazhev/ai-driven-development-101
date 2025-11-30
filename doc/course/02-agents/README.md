# 02. Agents

A practical guide to designing, building, and operating AI agents for software work.
Learn when to use an agent, how to design tools and memory, choose control loops, add guardrails,
and instrument for observability and evaluation.

## Learning objectives

- Decide when an agent is warranted vs direct prompting; understand cost and risk trade-offs.
- Decompose tasks into tools with clear I/O schemas and error semantics.
- Select memory strategies: scratchpad, retrieval, and long-term storage.
- Design control loops (plan-act-observe/ReAct-style, function-calling, workflow) with termination conditions.
- Add guardrails: policies, validation, safe tool preconditions, rate limits.
- Add observability: structured step logs, redaction, metrics, and traces for debugging.
- Run offline evaluation with golden tasks and failure injection.
- Apply security fundamentals for tools (least privilege, timeouts, network allowlists).

## Prerequisites

- Chapter 01 completed

## Outline

- Agent fundamentals and architecture
- Tool design and APIs
- Memory strategies
- Planning and control loops
- Observability and debugging
- Guardrails and safety
- Evaluation and offline tests
- Cost and latency considerations

## Key concepts and terminology

- Agent: a component that decides actions to reach a goal using tools and context.
- Tool: a named capability invoked with inputs returning an observation (or error).
- Action/Observation: a step pair representing a tool call and its result.
- Scratchpad: short-term working memory for the current task.
- Retrieval memory: external knowledge store accessed via search/retrieval.
- Long-term memory: persisted state across tasks with retention policies.
- Policy/guardrail: rules and validations constraining behavior.
- Plan: a light-weight sequence or strategy of intended steps.

## Agent architecture at a glance

1) Receive a task and relevant context.
2) Decide whether to call a tool or respond directly.
3) If a tool is selected: validate input → execute RPC → capture observation → update scratchpad/memory.
4) Check termination conditions (step budget, success predicate, user approval).
5) Emit structured output (and logs/metrics) for downstream systems.

### Control paradigms

- Direct function calling: select a function and arguments per step.
- Plan–act–observe (ReAct-style): propose a short plan, take an action, observe, iterate.
- Planner–executor: one model plans, another executes with stricter constraints.
- Finite-state/workflow: explicit states and transitions for predictable flows.
- Orchestration vs autonomous: human-in-the-loop vs self-directed with budgets.

## Tool design checklist

- Purpose, name, and concise description
- Input schema and validation (types, ranges, required/optional fields)
- Output schema and error taxonomy (retryable vs terminal errors)
- Idempotence and side effects (be explicit)
- Security: auth, least privilege, network allowlists, timeouts
- Rate limits and backoff strategies
- Observability: structured logs for request/response (with redaction)

## Memory strategies

- Scratchpad: ephemeral notes for the current task to reduce repetition.
- Retrieval-augmented: store and fetch relevant docs; include citations/IDs for traceability.
- Long-term: persist facts/preferences with TTL and consent; avoid storing secrets/PII.
- Prompt discipline: distinguish trusted context from untrusted inputs; quote and sandbox untrusted text.

## Planning and control loops

- Termination: step budget, max runtime, success predicate, and user approval points.
- Stuck detection: detect repeated actions or oscillation; escalate or stop.
- Error handling: bounded retries, fallback tools, and human handoff.
- Parameter strategy: low variability for tool selection; allow higher variability for content drafting.

## Observability and debugging

- Per-step logs: timestamp, tool, argument digest, observation summary, status.
- Redaction: mask secrets/PII in prompts, logs, and traces.
- Metrics: success rate, average steps, tool error rate, latency, and token/cost usage.
- Repro harness: replay transcripts to reproduce bugs and evaluate fixes offline.

## Guardrails and safety

- Input validation and output schema checks
- Safety policies: forbidden actions/domains; require user approval for risky ops
- Prompt-injection defense: treat untrusted text as data; quote and delimit; avoid executing embedded instructions
- Tool preconditions: allowlists for network/file access; sandbox side effects
- Quotas/rate limits and timeouts to bound behavior

## Prompt template for a decision step

```text
Role: Orchestrator agent for software tasks
Goal: [objective]
Available tools:
- name: <tool_name>
  description: <what it does>
  input_schema: <JSON schema or list of fields>
Constraints:
- Follow safety policy [short bullets]
- Use only approved tools
Context: [task, user input, relevant docs]
Scratchpad: [brief notes from prior steps]
Output format (JSON):
{
  "decision": "tool|respond",
  "tool_name": "<name or null>",
  "arguments": { /* fields */ },
  "rationale": "<1-2 sentences>"
}
Evaluation: [acceptance criteria for this step]
```

## Evaluation plan

- Golden tasks: a small offline set with clear success criteria
- Failure injection: API 500s, timeouts, malformed responses, rate limits
- Metrics: success rate, steps, tool errors, latency, token/cost budget
- Analysis: inspect traces for error patterns; iterate on prompts, tools, or policies

## Exercises

### Exercise 1: Flow design

Draft a flow for an agent that summarizes a repository README and outputs a 5-bullet overview.
Steps:

- Identify tools (e.g., http_get(url), summarize(text)) and their I/O schemas.
- Define termination conditions and a step budget.
- List acceptance criteria (content, length, citations/links).

### Exercise 2: Tool contracts

Design input/output schemas and error handling for a `search_issues(query)` tool with pagination.
Include validation rules and examples of retryable vs terminal errors.

### Exercise 3: Control loop

Choose a plan–act–observe loop with a 6-step budget. Describe stuck detection and fallback behavior.

### Exercise 4: Guardrails

Write a short safety policy: allowed network domains, forbidden operations, and PII handling.

### Exercise 5: Observability

Define a minimal per-step log schema: timestamp, tool, args hash, token usage, outcome, latency.

## Knowledge check (self-assessment)

- When should you introduce an agent instead of direct prompting?
- What distinguishes scratchpad, retrieval, and long-term memory?
- Give examples of termination conditions and stuck detection.
- How do you defend against prompt injection in untrusted inputs?
- Which metrics best indicate agent health in production?

## Glossary

- Agent, tool, action, observation, scratchpad, retrieval, policy, guardrail, termination.

## References

- Open-source agent frameworks (survey)
- Function-calling and tool-use docs from your LLM provider
- Resources on plan–act–observe (ReAct-style) prompting
- Guardrails and validation libraries (schema validation, content filters)
- Privacy and security best practices for AI-assisted systems
