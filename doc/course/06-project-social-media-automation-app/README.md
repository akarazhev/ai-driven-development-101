# 06. Project: Social Media Automation App

Build a provider-agnostic web application to compose, schedule, and publish text and images to
social platforms. Use AI for content drafting, variant generation, and quality checks while
maintaining safety, privacy, and compliance. The design favors clear boundaries, testability,
and an adapter pattern to integrate with chosen providers.

## Learning objectives

- Translate product requirements into a shippable architecture
- Implement, test, and iterate with AI assistance
- Follow best practices and design patterns

## Prerequisites

- Chapters 01–05 completed

## Outline

- Requirements and user stories
- Non-goals and constraints
- Architecture and data model
- Provider adapter pattern
- Scheduling and background processing
- AI-assisted content generation
- UI flows and accessibility
- Security, privacy, and compliance
- Testing strategy and evaluation
- Observability (logs/metrics/traces)
- Deployment and configuration

## Milestones

- M1: Project scaffold and environment
- M2: Core posting flows
- M3: AI-assisted content generation
- M4: Polishing, tests, and deployment

## Requirements and user stories

### Core user stories

- As a content manager, I can connect a social platform account so the app can post on my behalf.
- As a content manager, I can compose a post (text + 0–4 images) and preview how it will render.
- As a content manager, I can schedule a post for a future time or post immediately.
- As a content manager, I can view status (queued, posted, failed) and retry failures safely.
- As a content manager, I can request AI to suggest post variants or improve tone/clarity.
- As a content manager, I can ensure accessibility by providing or generating alt text for images.

### Non-goals and constraints (initial scope)

- Multi-tenant auth/UIs beyond a single team is out of scope for the first release.
- Cross-platform analytics and comments/replies are out of scope initially.
- Rate limits and platform content policies must be respected; avoid automation that violates terms.

## Architecture overview

- Web API/backend (Python 3.13): endpoints for posts, media, schedules, and provider operations.
- UI (minimal to moderate): compose/preview, queue view, and status.
- Provider adapters: boundary to integrate with chosen platforms via a common interface.
- Scheduler/worker: background task runner for scheduled publishing and retries.
- Storage: relational DB for posts/media/schedules; object store or filesystem for media in dev.
- AI module: content prompting helpers and evaluation.

### Provider adapter pattern

- Define an interface for actions: authenticate/configure, create_post(text, media, options), get_status(id), etc.
- Implement a stub provider for local dev that simulates responses and failure modes.
- Add real providers later by implementing the same interface behind a configuration flag.

## Data model (logical)

- User: id, name, role
- ProviderAccount: id, provider, display_name, credentials_ref, created_at
- MediaAsset: id, filename, content_type, size, storage_ref, alt_text
- Post: id, text, created_at, updated_at, author_id
- PostMedia: post_id, media_id, position
- Schedule: id, post_id, scheduled_at, status, attempt_count, last_error
- PublishLog: id, post_id, provider_account_id, external_id, status, message, created_at

## API sketch (example routes)

- POST /api/media: upload media; returns MediaAsset
- POST /api/posts: create draft post (text + media refs)
- GET /api/posts/{id}: get post with media
- POST /api/schedules: schedule a post for a time
- GET /api/schedules/{id}: get schedule status
- POST /api/providers/{account_id}/publish: publish now
- GET /api/providers/{account_id}/status/{external_id}: fetch provider status

## AI-assisted content generation

### Social copy prompt template

```text
Role: Social copywriter
Task: Produce 3 short variants (max 200 chars) for the following message
Constraints:
- Respect platform tone guidelines and avoid sensitive claims
- Include 1 relevant hashtag when appropriate
Inputs: [original message]
Output format: JSON array of strings
Evaluation: Content is clear, non-duplicative, and policy-safe
```

### Image alt-text template

```text
Role: Accessibility assistant
Task: Write concise, descriptive alt text for an image
Constraints:
- 1 sentence, 8–20 words; avoid speculation
Inputs: [brief image description]
Output: Plain sentence
Evaluation: Helpful and accurate without extra marketing language
```

## Security, privacy, and compliance

- Never store provider secrets in the repository; use environment variables or secret managers.
- Apply least privilege to tokens and rotate regularly.
- Validate and sanitize inputs; enforce media size/type limits.
- Redact secrets/PII from logs; implement structured logging with safe fields only.
- Respect platform policies and rate limits; implement backoff and retries.

## Testing strategy and evaluation

- Unit tests: services, validators, and adapter interfaces with a stub provider.
- Integration tests: API endpoints against an in-memory or test database.
- E2E tests: happy path for compose → schedule → publish (using stub provider).
- Evaluation: small golden set of messages to compare AI variants for clarity and policy safety.

## Observability

- Structured logs with correlation/request IDs.
- Metrics: posts_created, posts_published, publish_failures, retry_count, queue_latency.
- Traces (optional): per-request spans for publish flows.

## Deployment notes

- Configuration via environment variables; provide a sample .env.example (no secrets).
- SQLite for local dev; document how to switch to another DB for staging/prod.
- Store media in local filesystem for dev; document a cloud storage option without committing keys.

## Milestones

- M1: Project scaffold and environment
- M2: Core posting flows
- M3: AI-assisted content generation
- M4: Polishing, tests, and deployment

### Acceptance criteria per milestone

M1: Project scaffold and environment

- Repo structure with backend, adapters, scheduler, tests, and docs folders
- Health endpoint and initial DB migration
- Stub provider implemented; basic compose/schedule models ready

M2: Core posting flows

- Create post with text and media; preview UI available
- Schedule and immediate publish flows via stub provider
- Retry mechanism with backoff; status dashboard

M3: AI-assisted content generation

- Prompt helpers for copy variants and alt text
- Evaluation notes and parameter defaults checked in docs
- Safety checks integrated (length limits, basic policy flags)

M4: Polishing, tests, and deployment

- Test suite covering core flows; CI pipeline running
- Structured logs and minimal metrics
- Deployment guide and rollback plan documented

## Deliverables

- Working application with README and setup guide
- Architecture and adapter documentation
- API sketch/spec and example requests
- Test results (unit/integration/e2e) and evaluation notes
- Usage guide with screenshots and limitations

## Exercises

### Exercise 1: Scaffold and health check

- Create repo structure and a health endpoint; add first migration.
- Implement the stub provider with basic responses and a failure toggle.

### Exercise 2: Compose and schedule

- Implement draft creation with text + media; add schedule endpoint.
- Build a minimal compose/preview UI; validate inputs and alt text.

### Exercise 3: Publish and retry

- Implement publish-now and scheduled publish via stub provider.
- Add retry/backoff and a status dashboard; log correlation IDs.

### Exercise 4: AI assistance

- Add copy-variant and alt-text helpers using the templates above.
- Record an evaluation note comparing 2–3 prompts/params.

### Exercise 5: Hardening and docs

- Add input validation, rate limiting (if applicable), and structured logging.
- Write the deployment guide and sample .env.example.

## Knowledge check (self-assessment)

- What boundaries separate adapters from business logic and the API?
- How do you test publishing flows without calling real provider APIs?
- Which safeguards help keep content within platform policies?
- What metrics best indicate health of scheduling and publish jobs?

## References

- Provider API documentation for the platforms you choose
- Secure secrets management and logging guidelines
- Python logging and structured logging patterns
- Accessibility guidance for alt text (e.g., W3C)
