# ai-driven-development-101

Full-stack Confluence Publisher App implementing the course project (Chapter 06).

## Stack

- Backend: Spring Boot 3.2, JPA/Hibernate, SQLite
- Frontend: Angular 20 + TypeScript + TailwindCSS

## Prerequisites

- Java 21+
- Gradle 8+ (or use included gradlew)
- Node.js 18+ and npm

## Setup

1) Start backend

```bash
cd backend
./gradlew bootRun
```

2) Frontend

```bash
cd frontend
npm install
npm start
# or
ng serve
```

Open http://localhost:4200 and ensure the backend is running on http://localhost:8080.

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

## Tests

```bash
cd backend
./gradlew test
```

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

- Additional: [Course init brief](doc/ai/init-course.md)
