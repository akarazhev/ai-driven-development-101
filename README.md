# AI-Driven-Development-101

Full-stack Confluence Publisher App implementing the course project (Chapter 06).

## Stack

- Backend: Spring Boot 3.2, JPA/Hibernate, SQLite
- Frontend: Angular 20 + TypeScript + TailwindCSS

## Prerequisites

- Java 21+
- Gradle 8+ (or use included gradlew)
- Node.js 18+ and npm

## Setup

### Prerequisites Check

**Java:**
```bash
java -version  # Should be Java 21 or higher
```

**Node.js:**
```bash
node -v  # Should be Node 18 or higher
npm -v
```

**Gradle (optional, gradlew is included):**
```bash
gradle -v  # Optional, gradlew wrapper is included
```

### Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Start the backend (Linux/Mac):**
```bash
./gradlew bootRun
```

3. **Start the backend (Windows):**
```bash
.\gradlew.bat bootRun
```

4. **Verify backend is running:**
```bash
curl http://localhost:8080/api/health
# Should return: {"status":"ok"}
```

The backend will:
- Create necessary directories (`data/`, `storage/attachments/`) automatically
- Initialize SQLite database at `backend/data/app.db`
- Start on port 8080

**Backend Configuration:**
- Default port: `8080`
- Database: SQLite at `backend/data/app.db`
- Attachments: `backend/storage/attachments/`
- Configuration: `backend/src/main/resources/application.yml`

### Frontend Setup

1. **Navigate to frontend directory:**
```bash
cd frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Start the development server:**
```bash
npm start
# or
ng serve
```

4. **If port 4200 is in use, use a different port:**
```bash
ng serve --port 4201
```

5. **Open in browser:**
```
http://localhost:4200
# or http://localhost:4201 if using alternate port
```

**Frontend Configuration:**
- Default port: `4200`
- API base URL: `http://localhost:8080` (configured in `frontend/src/app/services/api.service.ts`)
- Angular Material theme: Indigo-Pink

### Verify Setup

1. **Backend health check:**
```bash
curl http://localhost:8080/api/health
```

2. **Frontend should display:**
   - New Year countdown at the top
   - Compose page with form fields
   - Navigation to Schedules page

3. **Test API from frontend:**
   - Create a page with title, content, and space key
   - Upload a file attachment
   - Publish the page

### Troubleshooting

**Backend won't start:**
- Check Java version: `java -version` (must be 21+)
- Check if port 8080 is available: `netstat -an | grep 8080` (Linux/Mac) or `netstat -an | findstr 8080` (Windows)
- Check logs in console for errors
- Ensure directories `data/` and `storage/attachments/` exist (created automatically)

**Frontend won't start:**
- Check Node.js version: `node -v` (must be 18+)
- Delete `node_modules` and `package-lock.json`, then run `npm install` again
- Check if port 4200 is available, use `--port` flag to use different port
- Check browser console for errors

**CORS errors:**
- Ensure backend is running on port 8080
- Check `CorsConfig.java` allows `http://localhost:4200`
- For different frontend port, update CORS configuration

**Database errors:**
- Ensure `backend/data/` directory exists and is writable
- Delete `backend/data/app.db` to reset database (will lose all data)

## Project structure

```
backend/
  src/main/java/com/confluence/publisher/
    controller/     # REST controllers (Page, Attachment, Schedule, Confluence, AI)
    service/        # Business logic (PageService, AttachmentService, PublishService, ScheduleService)
    repository/     # JPA repositories
    entity/         # JPA entities (Page, Attachment, Schedule, PublishLog, etc.)
    dto/            # Request/Response DTOs
    provider/       # Provider adapter pattern (BaseProvider, ConfluenceStubProvider)
    scheduler/      # Background scheduler for queued pages
    config/         # Spring configuration and properties
    exception/      # Global exception handling
  src/main/resources/
    application.yml # Application configuration
frontend/
  src/pages/      # ComposePage, Schedules
  src/app/        # app component and routing
data/             # sqlite database (gitignored)
storage/attachments/ # uploaded files (gitignored)
```

## API quickstart

- Health: GET /api/health
- Upload attachment: POST /api/attachments (multipart: file, description?)
- Create page: POST /api/pages { title, content, spaceKey, attachmentIds }
- Publish now: POST /api/confluence/publish { pageId }
- Schedule: POST /api/schedules { pageId, scheduledAt? }
- List schedules: GET /api/schedules

## Testing

### Backend Tests

**Run all tests:**
```bash
cd backend
./gradlew test
```

**Run tests on Windows:**
```bash
cd backend
.\gradlew.bat test
```

**Run specific test class:**
```bash
./gradlew test --tests "com.confluence.publisher.service.PageServiceTest"
```

**View test reports:**
```bash
# HTML report location
backend/build/reports/tests/test/index.html
```

**Test Coverage:**
- Unit tests for all services (PageService, AttachmentService, ScheduleService, PublishService)
- Integration tests for all controllers (PageController, AttachmentController, ScheduleController, ConfluenceController, AiController)
- Provider tests (ConfluenceStubProvider)

### Frontend Tests

**Run Playwright E2E tests:**
```bash
cd frontend
npm run e2e
```

**Run E2E tests in headed mode:**
```bash
cd frontend
npx playwright test --headed
```

**Run specific test file:**
```bash
npx playwright test e2e/tests/compose.spec.ts
```

**View test reports:**
```bash
npx playwright show-report
```

**E2E Test Coverage:**
- Compose page: Form validation, page creation, content improvement, file uploads, publishing, scheduling
- Schedules page: Table display, refresh, auto-refresh, status colors
- Navigation: Route navigation, active state highlighting
- Error handling: Error messages, validation errors, API errors

### API Testing

**Using Postman:**
1. Import the collection: `postman/ConfluencePublisher.postman_collection.json`
2. Set the base URL to `http://localhost:8080`
3. Run the collection or individual requests

**Using cURL:**
```bash
# Health check
curl http://localhost:8080/api/health

# Create a page
curl -X POST http://localhost:8080/api/pages \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Page","content":"Content","spaceKey":"DEV"}'

# Upload attachment
curl -X POST http://localhost:8080/api/attachments \
  -F "file=@document.pdf" \
  -F "description=Test file"
```

**API Documentation:**
See [doc/API_DOCUMENTATION.md](doc/API_DOCUMENTATION.md) for complete API reference.

## Notes

- The Confluence provider is a stub by default; configure real Confluence API integration for production.
- For production, configure a proper database and storage, secure API tokens, and harden CORS.
- Confluence URL and space configuration available via environment variables.

## Containerization (Docker/Podman)

The repo includes Dockerfiles for backend and frontend and a docker-compose.yml for local runs with Docker or Podman.

### Quickstart (Docker)

- Build and run
  ```bash
  docker compose up --build
  ```
- Open
  - Frontend: http://localhost:8080
  - Backend: http://localhost:8080/api/health (accessible via host port 8080)
- Stop / clean
  ```bash
  docker compose down          # stop containers
  docker compose down -v       # also remove volumes (DB/media)
  ```

### Quickstart (Podman)

- macOS hosts require the Podman VM:
  ```bash
  podman machine start
  ```
- Build and run
  ```bash
  podman compose up --build
  # or, depending on your installation
  podman-compose up --build
  ```
- Open
  - Frontend: http://localhost:8080
  - Backend: http://localhost:8080/api/health (accessible via host port 8080)
- Stop / clean
  ```bash
  podman compose down
  podman compose down -v
  ```

### Compose services and ports

- backend
  - Image: built from backend/Dockerfile (Spring Boot)
  - Port: 8080 (host -> container)
  - Volumes: named `data` (SQLite at /data/app.db), `media` (/storage/media)
  - Env (set in compose):
    - `SPRING_PROFILES_ACTIVE=docker`
    - `APP_DATABASE_URL=jdbc:sqlite:///data/app.db`
    - `APP_ATTACHMENT_DIR=/storage/attachments`
    - `APP_CONFLUENCE_URL=https://your-domain.atlassian.net`
    - `APP_CONFLUENCE_DEFAULT_SPACE=DEV`
    - `APP_PROVIDER=confluence-stub`
    - `APP_SCHEDULER_INTERVAL_SECONDS=5`
    - `APP_CORS_ORIGINS=http://localhost:4200,http://localhost:8080,http://localhost:5173`
- frontend
  - Image: built from frontend/Dockerfile
  - Port: 8080 (host -> container port 80, nginx)
  - Build arg: `NG_APP_API_BASE=http://localhost:8080` so the browser calls the backend via host port 8080.

### Customizing configuration

- Backend environment variables (Spring Boot properties with `APP_` prefix):
  - `APP_DATABASE_URL` (default `jdbc:sqlite:./data/app.db` in dev, `jdbc:sqlite:///data/app.db` in container)
  - `APP_ATTACHMENT_DIR` (default `storage/attachments`, container uses `/storage/attachments`)
  - `APP_CONFLUENCE_URL` (Confluence instance URL)
  - `APP_CONFLUENCE_DEFAULT_SPACE` (default Confluence space key)
  - `APP_CONFLUENCE_API_TOKEN` (API token for authentication)
  - `APP_PROVIDER` (`confluence-stub` by default)
  - `APP_SCHEDULER_INTERVAL_SECONDS` (default `5`)
  - `APP_CORS_ORIGINS` comma-separated list of allowed origins
- Frontend API base
  - Set at build time via compose `build.args.NG_APP_API_BASE` (default `http://localhost:8080`).
  - Change when deploying behind another host/port, then rebuild the frontend image.

### Build images manually (optional)

```bash
# Backend image
docker build -t confluence-backend -f backend/Dockerfile .
docker run --rm -p 8080:8080 \
  -e APP_DATABASE_URL=jdbc:sqlite:///data/app.db \
  -e APP_ATTACHMENT_DIR=/storage/attachments \
  -v confluence_data:/data -v confluence_attachments:/storage/attachments \
  confluence-backend

# Frontend image
docker build -t confluence-frontend \
  --build-arg NG_APP_API_BASE=http://localhost:8080 \
  -f frontend/Dockerfile .
docker run --rm -p 8080:80 confluence-frontend
```

### Data persistence

- Compose uses named volumes: `data` for the SQLite DB and `attachments` for uploaded files.
- Remove volumes with `docker compose down -v` (or `podman compose down -v`). This will delete your DB and uploaded attachments.

### Troubleshooting

- Port already in use
  - Change host ports in docker-compose.yml (e.g., `8081:8080`, `8082:80`).
- CORS blocked
  - Add your origin to `APP_CORS_ORIGINS` in docker-compose.yml and recreate containers.
- Frontend cannot reach backend
  - Ensure backend is mapped to host port 8080 and frontend is built with `NG_APP_API_BASE` pointing to `http://localhost:8080`.
  - Rebuild the frontend image after changing `NG_APP_API_BASE`.
- Podman on SELinux hosts (Linux)
  - If you bind host directories instead of volumes, append `:Z` to volume mounts for proper labeling.
- macOS Podman
  - Ensure `podman machine start` before running compose commands.
- Slow or stale frontend
  - Rebuild frontend: `docker compose build frontend` (or Podman equivalent).

## Course documentation

- Overview: [doc/course/README.md](doc/course/README.md)
- Chapters
  1. [01. Introduction to AI](doc/course/01-introduction-to-ai/README.md)
  2. [02. Agents](doc/course/02-agents/README.md)
  3. [03. Setup Cursor AI](doc/course/03-setup-cursor-ai/README.md)
  4. [04. Setup contex 7](doc/course/04-setup-contex-7/README.md)
  5. [05. AI-Driven Software Development](doc/course/05-ai-driven-software-development/README.md)
  6. [06. Project: Confluence Publisher App](doc/course/06-project-confluence-publisher-app/README.md)

### Additional resources

- [Cursor Learn — Official Course](https://cursor.com/learn)
- [Cursor Directory: Rules](https://cursor.directory/rules)