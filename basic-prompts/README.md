# Confluence Publisher - AI Generation Prompts

This directory contains **13 detailed prompts** for AI to generate a complete full-stack web application called "Confluence Publisher". Each prompt describes requirements without providing source code, allowing AI to generate the implementation.

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 21, JPA/Hibernate, SQLite
- **Frontend**: Angular 20, TypeScript, TailwindCSS
- **Deployment**: Docker, Docker Compose, Nginx

## Prompt Sequence

Execute these prompts in order:

| # | File | Description |
|---|------|-------------|
| 01 | [01-project-setup.md](01-project-setup.md) | Project structure, Gradle/npm config |
| 02 | [02-backend-entities-repositories.md](02-backend-entities-repositories.md) | 5 JPA entities, 5 repositories |
| 03 | [03-backend-dtos.md](03-backend-dtos.md) | Request/Response DTOs |
| 04 | [04-backend-configuration.md](04-backend-configuration.md) | Spring config classes |
| 05 | [05-backend-services.md](05-backend-services.md) | Business logic services |
| 06 | [06-backend-providers.md](06-backend-providers.md) | Confluence API providers |
| 07 | [07-backend-controllers.md](07-backend-controllers.md) | REST controllers |
| 08 | [08-backend-scheduler-exception-handler.md](08-backend-scheduler-exception-handler.md) | Scheduler, error handling |
| 09 | [09-frontend-setup-routing.md](09-frontend-setup-routing.md) | Angular setup, routing |
| 10 | [10-frontend-api-service.md](10-frontend-api-service.md) | HTTP API service |
| 11 | [11-frontend-compose-component.md](11-frontend-compose-component.md) | Compose page |
| 12 | [12-frontend-schedules-component.md](12-frontend-schedules-component.md) | Schedules page |
| 13 | [13-docker-deployment.md](13-docker-deployment.md) | Docker configuration |

## How to Use

1. Start with prompt 01 and provide it to an AI assistant
2. Review and test the generated code
3. Proceed to the next prompt
4. Each prompt builds on the previous ones

## Application Features

- **Page Creation**: Create pages with title, content, attachments
- **File Attachments**: Upload and attach files to pages
- **Immediate Publishing**: Publish pages directly to Confluence
- **Scheduled Publishing**: Queue pages for background publication
- **Content Suggestions**: AI-powered content improvement (stub)
- **Provider Pattern**: Switchable Confluence providers (stub/server)

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/health | Health check |
| GET | /api/config | Frontend configuration |
| POST | /api/pages | Create page |
| GET | /api/pages/{id} | Get page |
| POST | /api/attachments | Upload file |
| POST | /api/schedules | Create schedule |
| GET | /api/schedules | List schedules |
| POST | /api/confluence/publish | Publish immediately |
| POST | /api/ai/improve-content | Content suggestions |

## Database Tables

- **page**: id, title, content, spaceKey, parentPageId, timestamps
- **attachment**: id, filename, contentType, size, storagePath, description
- **pageattachment**: id, pageId, attachmentId, position
- **schedule**: id, pageId, scheduledAt, status, attemptCount, lastError
- **publishlog**: id, pageId, provider, confluencePageId, status, message, createdAt

## Quick Start

### Development
```bash
# Backend
cd backend && ./gradlew bootRun

# Frontend
cd frontend && npm install && npm start
```

### Docker
```bash
cp .env.example .env
docker compose up --build
```
