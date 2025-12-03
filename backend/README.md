# Confluence Publisher - Spring Boot Backend

Modern Spring Boot backend for publishing pages to Confluence application.

## Technology Stack

- **Spring Boot 3.2.0** - modern framework
- **Java 21** - latest LTS version
- **Gradle 8.5** - build system
- **Spring Data JPA** - database operations
- **SQLite** - database
- **Lombok** - reduces boilerplate code
- **Bean Validation** - data validation

## Architecture

The project follows Spring Boot best practices:

- **Layered architecture**: Controllers → Services → Repositories → Entities
- **DTO pattern**: separate classes for requests/responses
- **Dependency Injection**: through constructors (Lombok @RequiredArgsConstructor)
- **Transactional**: @Transactional at service level
- **Error handling**: GlobalExceptionHandler
- **Configuration**: through @ConfigurationProperties
- **Scheduler**: Spring @Scheduled

## Project Structure

```
src/main/java/com/confluence/publisher/
├── controller/      # REST controllers
├── service/         # Business logic
├── repository/      # Spring Data JPA repositories
├── entity/          # JPA entities
├── dto/             # Data Transfer Objects
├── config/          # Configuration classes
├── exception/       # Exception handling
├── provider/        # Confluence providers
└── scheduler/       # Task scheduler
```

## API Endpoints

- `GET /api/health` - health check
- `POST /api/pages` - create page
- `GET /api/pages/{id}` - get page
- `POST /api/attachments` - upload attachment
- `POST /api/schedules` - create schedule
- `GET /api/schedules/{id}` - get schedule
- `GET /api/schedules` - list schedules
- `POST /api/confluence/publish` - publish page to Confluence
- `POST /api/ai/improve-content` - improve content
- `POST /api/ai/generate-summary` - generate summary

## Running

### Locally

```bash
./gradlew bootRun
```

### Docker

```bash
docker-compose up backend
```

## Configuration

Settings in `application.yml`:

- `app.database-url` - database URL
- `app.attachment-dir` - attachments directory
- `app.confluence-url` - Confluence instance URL
- `app.confluence-default-space` - default Confluence space
- `app.confluence-api-token` - API token for authentication
- `app.cors-origins` - allowed CORS origins
- `app.provider` - publication provider (confluence-stub)
- `app.scheduler-interval-seconds` - schedule check interval

## Features

- ✅ Modern Spring Boot 3.x
- ✅ Java 21 with new features
- ✅ Type-safe configuration
- ✅ Input data validation
- ✅ Centralized error handling
- ✅ Logging through SLF4J
- ✅ Production ready (Docker, health checks)
