# 06. Project: Social Media Automation App

Build a full-stack web application to compose, schedule, and publish content to social platforms. This project
demonstrates how to use AI assistance throughout the entire development lifecycle, from planning to deployment.

**This project is technology-agnostic** - you can use any backend (Python, Node.js, Java, Go, etc.) and any frontend (
React, Vue, Angular, etc.) that you're comfortable with.

## Learning objectives

- Apply all skills from previous chapters to build a complete application
- Use AI for planning, design, implementation, testing, and deployment
- Follow best practices and design patterns
- Build a production-ready application with AI assistance

## Prerequisites

- Chapters 00-05 completed (or 00-03, 05 if you skipped 04)
- Cursor set up and working
- Familiarity with your chosen technology stack
- Basic understanding of web development

## Project overview

You'll build a social media automation app that allows users to:

- Compose posts with text and images
- Schedule posts for future publication
- Publish posts immediately
- View post status and retry failed posts
- Use AI to generate post variants and alt text

## Project structure

The project is divided into 4 milestones, each building on the previous one:

1. **[M1: Project Scaffold](./06.1-milestone-1-scaffold.md)** - Set up project structure and basic infrastructure
2. **[M2: Core Posting Flows](./06.2-milestone-2-core-flows.md)** - Implement core functionality
3. **[M3: AI-Assisted Content Generation](./06.3-milestone-3-ai-features.md)** - Add AI features
4. **[M4: Polishing and Deployment](./06.4-milestone-4-polishing.md)** - Testing, documentation, and deployment

## Architecture overview

### Components

- **Backend API**: RESTful API for posts, media, schedules, and provider operations
- **Frontend UI**: Web interface for composing, scheduling, and viewing posts
- **Provider Adapters**: Interface to integrate with social platforms (stub provider for development)
- **Scheduler**: Background worker for scheduled publishing and retries
- **Storage**: Database for posts/media/schedules; file storage for media
- **AI Module**: Content generation helpers

### Technology choices

**You choose**:

- Backend framework (FastAPI, Express, Spring Boot, etc.)
- Frontend framework (React, Vue, Angular, etc.)
- Database (PostgreSQL, MySQL, SQLite for dev, etc.)
- Language (Python, JavaScript/TypeScript, Java, Go, etc.)

**Recommendation**: Use what you know best. The patterns and prompts work with any stack.

## Requirements and user stories

### Core user stories

- As a content manager, I can compose a post (text + 0–4 images) and preview how it will render
- As a content manager, I can schedule a post for a future time or post immediately
- As a content manager, I can view status (queued, posted, failed) and retry failures safely
- As a content manager, I can request AI to suggest post variants or improve tone/clarity
- As a content manager, I can ensure accessibility by providing or generating alt text for images

### Non-goals (initial scope)

- Multi-tenant authentication (single user/team for now)
- Cross-platform analytics
- Comments/replies management
- Real social media integrations (use stub provider)

## Data model

### Core entities

- **MediaAsset**: id, filename, content_type, size, storage_path, alt_text
- **Post**: id, text, created_at, updated_at, author_id
- **PostMedia**: post_id, media_id, position (junction table)
- **Schedule**: id, post_id, scheduled_at, status, attempt_count, last_error
- **PublishLog**: id, post_id, provider, external_id, status, message, created_at

### Relationships

- Post → PostMedia → MediaAsset (one-to-many)
- Post → Schedule (one-to-many)
- Post → PublishLog (one-to-many)

## API endpoints

### Media

- `POST /api/media` - Upload media file
- `GET /api/media/{id}` - Get media metadata

### Posts

- `POST /api/posts` - Create post
- `GET /api/posts/{id}` - Get post with media
- `GET /api/posts` - List posts

### Schedules

- `POST /api/schedules` - Schedule a post
- `GET /api/schedules` - List schedules
- `GET /api/schedules/{id}` - Get schedule status

### Publishing

- `POST /api/providers/default/publish` - Publish post immediately
- `GET /api/providers/default/status/{external_id}` - Get publish status

### AI

- `POST /api/ai/variants` - Generate post variants
- `POST /api/ai/alt-text` - Generate alt text for image

## Design patterns

### Provider Adapter Pattern

Use an adapter pattern to abstract social media providers:

```text
BaseProvider interface:
- publish(text, media_paths) -> (external_id, message)
- get_status(external_id) -> status

StubProvider (for development):
- Simulates publishing
- Returns mock external_id
- Can simulate failures for testing
```

This allows you to:

- Test without real API calls
- Switch providers easily
- Add real providers later

## Security and compliance

### Best practices

- **Never store secrets in code**: Use environment variables
- **Validate all inputs**: Sanitize user input
- **Enforce file size/type limits**: Prevent abuse
- **Redact secrets from logs**: Don't log sensitive data
- **Respect rate limits**: Implement backoff strategies

### Privacy

- Don't store PII unnecessarily
- Use secure storage for credentials
- Implement proper access controls
- Follow data protection regulations

## Testing strategy

### Test types

1. **Unit tests**: Individual functions/components
2. **Integration tests**: API endpoints with test database
3. **E2E tests**: Complete user flows
4. **AI evaluation**: Test AI output quality

### Testing with stub provider

Use stub provider for:

- Development (no API keys needed)
- Testing (predictable behavior)
- CI/CD (no external dependencies)

## Getting started

1. **Choose your stack**: Pick backend and frontend technologies
2. **Start with M1**: Follow [Milestone 1 guide](./06.1-milestone-1-scaffold.md)
3. **Work incrementally**: Complete each milestone before moving on
4. **Use AI throughout**: Apply patterns from previous chapters

## Milestones overview

### M1: Project Scaffold (Week 1)

- Set up project structure
- Configure development environment
- Create database schema
- Implement health endpoint
- Create stub provider

**Deliverable**: Working project scaffold with health check

### M2: Core Posting Flows (Week 2)

- Implement media upload
- Create post endpoints
- Build scheduling system
- Create basic UI
- Implement publish flow

**Deliverable**: Can create, schedule, and publish posts

### M3: AI Features (Week 3)

- Add AI variant generation
- Implement alt text generation
- Add AI prompts to UI
- Test AI output quality

**Deliverable**: AI-assisted content generation working

### M4: Polishing (Week 4)

- Write comprehensive tests
- Add error handling
- Improve UI/UX
- Document everything
- Deploy application

**Deliverable**: Production-ready application

## Tips for success

### Use AI effectively

- **Plan first**: Use AI to break down each milestone
- **Small steps**: Implement one feature at a time
- **Test constantly**: Verify each change works
- **Refine prompts**: Improve prompts based on results
- **Review everything**: Don't accept AI output blindly

### Stay organized

- **Commit frequently**: Small, focused commits
- **Document decisions**: Why you made certain choices
- **Track progress**: Check off milestones as you complete them
- **Ask for help**: Use AI when stuck

### Quality matters

- **Write tests**: Test as you build
- **Handle errors**: Don't ignore error cases
- **Follow patterns**: Use consistent code style
- **Review code**: Check AI output before committing

## Troubleshooting

### Common issues

**Problem**: AI suggests wrong technology patterns

- **Solution**: Provide more context about your stack, use project rules

**Problem**: Tests fail after AI changes

- **Solution**: Always run tests, refine prompts to include test requirements

**Problem**: AI changes too much code

- **Solution**: Select specific code, be explicit about scope

**Problem**: Deployment issues

- **Solution**: Test locally first, use staging environment

## Knowledge check

Before starting, ensure you can:

- [ ] Write effective prompts (Chapter 01)
- [ ] Break down complex tasks (Chapter 02)
- [ ] Use Cursor effectively (Chapter 03)
- [ ] Apply development workflows (Chapter 05)
- [ ] Create project rules (Chapter 00)

## Next steps

Ready to start? Begin with **[Milestone 1: Project Scaffold](./06.1-milestone-1-scaffold.md)**

## References

- Your chosen technology stack documentation
- REST API design best practices
- Database design patterns
- Security best practices (OWASP)
- Accessibility guidelines (WCAG)
