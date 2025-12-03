# 06. Project: Confluence Publisher App

Build a full-stack web application to compose, schedule, and publish documentation pages to Confluence. This project
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

You'll build a Confluence publisher app that allows users to:

- Compose documentation pages with title, content, and attachments
- Schedule pages for future publication to Confluence spaces
- Publish pages immediately to Confluence
- View page status and retry failed publications
- Use AI to improve content, generate summaries, and suggest titles

## Project structure

The project is divided into 4 milestones, each building on the previous one:

1. **[M1: Project Scaffold](./06.1-milestone-1-scaffold.md)** - Set up project structure and basic infrastructure
2. **[M2: Core Posting Flows](./06.2-milestone-2-core-flows.md)** - Implement core functionality
3. **[M3: AI-Assisted Content Generation](./06.3-milestone-3-ai-features.md)** - Add AI features
4. **[M4: Polishing and Deployment](./06.4-milestone-4-polishing.md)** - Testing, documentation, and deployment

## Architecture overview

### Components

- **Backend API**: RESTful API for pages, attachments, schedules, and Confluence operations
- **Frontend UI**: Web interface for composing, scheduling, and viewing pages
- **Provider Adapters**: Interface to integrate with Confluence API (stub provider for development)
- **Scheduler**: Background worker for scheduled publishing and retries
- **Storage**: Database for pages/attachments/schedules; file storage for attachments
- **AI Module**: Content improvement and generation helpers

### Technology choices

**You choose**:

- Backend framework (FastAPI, Express, Spring Boot, etc.)
- Frontend framework (React, Vue, Angular, etc.)
- Database (PostgreSQL, MySQL, SQLite for dev, etc.)
- Language (Python, JavaScript/TypeScript, Java, Go, etc.)

**Recommendation**: Use what you know best. The patterns and prompts work with any stack.

## Requirements and user stories

### Core user stories

- As a content manager, I can compose a Confluence page (title + content + attachments) and preview it
- As a content manager, I can schedule a page for future publication or publish immediately
- As a content manager, I can view publication status (queued, published, failed) and retry failures safely
- As a content manager, I can request AI to improve content, generate summaries, or suggest titles
- As a content manager, I can specify target Confluence space and parent page for organization

### Non-goals (initial scope)

- Multi-tenant authentication (single user/team for now)
- Advanced Confluence features (macros, templates)
- Page versioning and diff tracking
- Real Confluence API integration (use stub provider for development)

## Data model

### Core entities

- **Attachment**: id, filename, content_type, size, storage_path, description
- **Page**: id, title, content, space_key, parent_page_id, created_at, updated_at
- **PageAttachment**: page_id, attachment_id, position (junction table)
- **Schedule**: id, page_id, scheduled_at, status, attempt_count, last_error
- **PublishLog**: id, page_id, provider, page_id (Confluence), status, message, created_at

### Relationships

- Page → PageAttachment → Attachment (one-to-many)
- Page → Schedule (one-to-many)
- Page → PublishLog (one-to-many)

## API endpoints

### Attachments

- `POST /api/attachments` - Upload attachment file
- `GET /api/attachments/{id}` - Get attachment metadata

### Pages

- `POST /api/pages` - Create page
- `GET /api/pages/{id}` - Get page with attachments
- `GET /api/pages` - List pages

### Schedules

- `POST /api/schedules` - Schedule a page for publication
- `GET /api/schedules` - List schedules
- `GET /api/schedules/{id}` - Get schedule status

### Publishing

- `POST /api/confluence/publish` - Publish page immediately to Confluence
- `GET /api/confluence/status/{pageId}` - Get publication status

### AI

- `POST /api/ai/improve-content` - Improve page content
- `POST /api/ai/generate-summary` - Generate page summary
- `POST /api/ai/suggest-title` - Suggest page title

## Design patterns

### Provider Adapter Pattern

Use an adapter pattern to abstract Confluence API:

```text
BaseProvider interface:
- publishPage(spaceKey, title, content, parentPageId, attachmentPaths) -> (pageId, message)
- getStatus(pageId) -> status

ConfluenceStubProvider (for development):
- Simulates Confluence API responses
- Returns mock Confluence page IDs
- Can simulate failures for testing
```

This allows you to:

- Test without real Confluence API calls
- Switch to real Confluence integration easily
- Develop without Confluence credentials

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
- Create database schema (pages, attachments, schedules)
- Implement health endpoint
- Create Confluence stub provider

**Deliverable**: Working project scaffold with health check

### M2: Core Publishing Flows (Week 2)

- Implement attachment upload
- Create page endpoints
- Build scheduling system
- Create basic UI
- Implement Confluence publish flow

**Deliverable**: Can create, schedule, and publish pages to Confluence

### M3: AI Features (Week 3)

- Add AI content improvement
- Implement summary generation
- Add title suggestions
- Add AI prompts to UI
- Test AI output quality

**Deliverable**: AI-assisted content enhancement working

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

## Resources

- [Cursor Learn — Official Course](https://cursor.com/learn)
- [Cursor Directory: Rules](https://cursor.directory/rules)
