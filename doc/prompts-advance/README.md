# Confluence Publisher - AI Generation Prompts

This directory contains a series of detailed prompts for AI to generate a complete full-stack web application called "Confluence Publisher". The application allows users to create, schedule, and publish pages to Atlassian Confluence.

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 21, JPA/Hibernate, SQLite
- **Frontend**: Angular 20, TypeScript, TailwindCSS
- **Deployment**: Docker, Docker Compose, Nginx

## Prompt Sequence

Execute these prompts in order to generate the complete application:

| # | File | Description |
|---|------|-------------|
| 01 | [01-project-setup.md](01-project-setup.md) | Project structure, build files, configuration |
| 02 | [02-backend-entities-repositories.md](02-backend-entities-repositories.md) | JPA entities and Spring Data repositories |
| 03 | [03-backend-dtos.md](03-backend-dtos.md) | Data Transfer Objects for API |
| 04 | [04-backend-configuration.md](04-backend-configuration.md) | Spring configuration classes |
| 05 | [05-backend-services.md](05-backend-services.md) | Business logic services |
| 06 | [06-backend-providers.md](06-backend-providers.md) | Confluence API provider pattern |
| 07 | [07-backend-controllers.md](07-backend-controllers.md) | REST API controllers |
| 08 | [08-backend-scheduler-exception-handler.md](08-backend-scheduler-exception-handler.md) | Background scheduler and error handling |
| 09 | [09-frontend-setup-routing.md](09-frontend-setup-routing.md) | Angular setup and routing |
| 10 | [10-frontend-api-service.md](10-frontend-api-service.md) | HTTP API service |
| 11 | [11-frontend-compose-component.md](11-frontend-compose-component.md) | Main compose page component |
| 12 | [12-frontend-schedules-component.md](12-frontend-schedules-component.md) | Schedules list component |
| 13 | [13-docker-deployment.md](13-docker-deployment.md) | Docker and deployment configuration |

## Application Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (Angular)                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ ComposeComponent│  │SchedulesComponent│  │   ApiService    │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
└───────────┼─────────────────────┼─────────────────────┼──────────┘
            │                     │                     │
            └─────────────────────┼─────────────────────┘
                                  │ HTTP/REST
┌─────────────────────────────────┼───────────────────────────────┐
│                         Backend (Spring Boot)                    │
│  ┌─────────────────────────────┴─────────────────────────────┐  │
│  │                      REST Controllers                      │  │
│  │  PageController │ AttachmentController │ ScheduleController│  │
│  │  ConfluenceController │ AiController │ HealthController    │  │
│  └─────────────────────────────┬─────────────────────────────┘  │
│                                │                                 │
│  ┌─────────────────────────────┴─────────────────────────────┐  │
│  │                        Services                            │  │
│  │  PageService │ AttachmentService │ ScheduleService         │  │
│  │  PublishService                                            │  │
│  └─────────────────────────────┬─────────────────────────────┘  │
│                                │                                 │
│  ┌─────────────────────────────┴─────────────────────────────┐  │
│  │                       Providers                            │  │
│  │  ProviderFactory → ConfluenceServerProvider                │  │
│  │                  → ConfluenceStubProvider                  │  │
│  └─────────────────────────────┬─────────────────────────────┘  │
│                                │                                 │
│  ┌─────────────────────────────┴─────────────────────────────┐  │
│  │                      Repositories                          │  │
│  │  PageRepository │ AttachmentRepository │ ScheduleRepository│  │
│  │  PageAttachmentRepository │ PublishLogRepository           │  │
│  └─────────────────────────────┬─────────────────────────────┘  │
│                                │                                 │
│  ┌─────────────────────────────┴─────────────────────────────┐  │
│  │                        Entities                            │  │
│  │  Page │ Attachment │ PageAttachment │ Schedule │ PublishLog│  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                      PageScheduler                         │  │
│  │  (Background job processing queued publications)          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                         SQLite Database                          │
│  Tables: page, attachment, pageattachment, schedule, publishlog  │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Confluence Server/Cloud                     │
│  REST API: /rest/api/content                                     │
└─────────────────────────────────────────────────────────────────┘
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |
| GET | `/api/config` | Get frontend configuration |
| POST | `/api/pages` | Create a new page |
| GET | `/api/pages/{id}` | Get page by ID |
| POST | `/api/attachments` | Upload attachment |
| POST | `/api/schedules` | Create schedule |
| GET | `/api/schedules` | List schedules |
| GET | `/api/schedules/{id}` | Get schedule by ID |
| POST | `/api/confluence/publish` | Publish immediately |
| POST | `/api/ai/improve-content` | Get content suggestions |

## Database Schema

```sql
-- page
CREATE TABLE page (
  id INTEGER PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  space_key VARCHAR(50) NOT NULL,
  parent_page_id BIGINT,
  author_id BIGINT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

-- attachment
CREATE TABLE attachment (
  id INTEGER PRIMARY KEY,
  filename VARCHAR(255) NOT NULL,
  content_type VARCHAR(100) NOT NULL,
  size BIGINT NOT NULL,
  storage_path VARCHAR(500) NOT NULL,
  description TEXT
);

-- pageattachment
CREATE TABLE pageattachment (
  id INTEGER PRIMARY KEY,
  page_id BIGINT NOT NULL,
  attachment_id BIGINT NOT NULL,
  position INTEGER NOT NULL DEFAULT 0
);

-- schedule
CREATE TABLE schedule (
  id INTEGER PRIMARY KEY,
  page_id BIGINT NOT NULL,
  scheduled_at TIMESTAMP NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'queued',
  attempt_count INTEGER NOT NULL DEFAULT 0,
  last_error TEXT
);

-- publishlog
CREATE TABLE publishlog (
  id INTEGER PRIMARY KEY,
  page_id BIGINT NOT NULL,
  provider VARCHAR(50) NOT NULL,
  space_key VARCHAR(50),
  confluence_page_id VARCHAR(50),
  status VARCHAR(20) NOT NULL,
  message TEXT,
  created_at TIMESTAMP NOT NULL
);
```

## Quick Start

### Development
```bash
# Backend
cd backend
./gradlew bootRun

# Frontend (separate terminal)
cd frontend
npm install
npm start
```

### Docker
```bash
cp .env.example .env
# Edit .env with your Confluence credentials
docker compose up --build
```

## Key Features

1. **Page Creation**: Create pages with title, content, and attachments
2. **File Attachments**: Upload and attach files to pages
3. **Immediate Publishing**: Publish pages directly to Confluence
4. **Scheduled Publishing**: Queue pages for background publication
5. **Content Suggestions**: AI-powered content improvement (stub)
6. **Provider Pattern**: Switchable Confluence providers (stub/server)
7. **Auto-Refresh**: Schedules page updates automatically

## Configuration

All configuration is done via environment variables. See `.env.example` for available options.

## Notes

- The AI content improvement is a stub implementation
- The Confluence provider can be switched between stub and real server
- SQLite is used for simplicity; can be replaced with PostgreSQL/MySQL
- All prompts include complete, runnable code
