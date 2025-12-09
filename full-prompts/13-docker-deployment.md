# Prompt 13: Docker Deployment Configuration

## Context
Continue building the Confluence Publisher application. Create the Docker configuration files for containerized deployment of both backend and frontend.

## Requirements

### Backend Dockerfile
Create `backend/Dockerfile`:
```dockerfile
# Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle files
COPY backend/build.gradle.kts backend/settings.gradle.kts backend/gradle.properties ./
COPY backend/gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY backend/src ./src

# Build application
RUN gradle build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create directories for data and attachments
RUN mkdir -p /data /storage/attachments /app/data

# Copy built JAR
COPY --from=build /app/build/libs/confluence-publisher.jar app.jar

# Default environment variables (can be overridden)
ENV SPRING_PROFILES_ACTIVE=docker \
    APP_DATABASE_URL=jdbc:sqlite:///data/app.db \
    APP_ATTACHMENT_DIR=/storage/attachments \
    APP_PROVIDER=confluence-server \
    APP_SCHEDULER_INTERVAL_SECONDS=5

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Dockerfile
Create `frontend/Dockerfile`:
```dockerfile
# Frontend Dockerfile
FROM node:20-alpine AS build
WORKDIR /app
ARG NG_APP_API_BASE=http://localhost:8080
ENV NG_APP_API_BASE=${NG_APP_API_BASE}
COPY frontend/package.json ./
RUN npm install
COPY frontend ./
RUN sed -i "s|apiBase: '.*'|apiBase: '${NG_APP_API_BASE}'|g" src/environments/environment.prod.ts
RUN npm run build

FROM nginx:alpine AS runtime
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/confluence-publisher-app/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx Configuration
Create `frontend/nginx.conf`:
```nginx
server {
  listen 80 default_server;
  server_name _;

  location / {
    root /usr/share/nginx/html;
    try_files $uri $uri/ /index.html;
  }
}
```

### Docker Compose
Create `docker-compose.yml`:
```yaml
version: "3.9"

services:
  backend:
    build:
      context: .
      dockerfile: backend/Dockerfile
    ports:
      - "8080:8080"
    env_file:
      # Load secrets from .env file (not committed to git)
      - .env
    environment:
      SPRING_PROFILES_ACTIVE: docker
      APP_DATABASE_URL: jdbc:sqlite:///data/app.db
      APP_ATTACHMENT_DIR: /storage/attachments
      # Confluence configuration - loaded from .env file
      CONFLUENCE_URL: ${CONFLUENCE_URL:-https://your-domain.atlassian.net}
      CONFLUENCE_DEFAULT_SPACE: ${CONFLUENCE_DEFAULT_SPACE:-DEV}
      CONFLUENCE_API_TOKEN: ${CONFLUENCE_API_TOKEN:-}
      CONFLUENCE_USERNAME: ${CONFLUENCE_USERNAME:-}
      CONFLUENCE_PROVIDER: ${CONFLUENCE_PROVIDER:-confluence-server}
      SCHEDULER_INTERVAL_SECONDS: ${SCHEDULER_INTERVAL_SECONDS:-5}
      CORS_ORIGINS: ${CORS_ORIGINS:-http://localhost:4200,http://localhost:8080,http://localhost:5173}
    volumes:
      - data:/data
      - attachments:/storage/attachments
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  frontend:
    build:
      context: .
      dockerfile: frontend/Dockerfile
      args:
        NG_APP_API_BASE: ${NG_APP_API_BASE:-http://localhost:8080}
    ports:
      - "4200:80"
    depends_on:
      backend:
        condition: service_healthy
    restart: unless-stopped

volumes:
  data:
    driver: local
  attachments:
    driver: local
```

### Environment Example
Create `.env.example`:
```bash
# Confluence API Configuration
# Copy this file to .env and fill in your actual values
# DO NOT commit .env file to version control

# Confluence API Credentials
CONFLUENCE_URL=https://your-domain.atlassian.net/confluence
CONFLUENCE_USERNAME=your-username
CONFLUENCE_API_TOKEN=your-api-token-here
CONFLUENCE_DEFAULT_SPACE=YOUR_SPACE_KEY

# Application Configuration
CONFLUENCE_PROVIDER=confluence-server
SCHEDULER_INTERVAL_SECONDS=5
CORS_ORIGINS=http://localhost:4200,http://localhost:8080,http://localhost:5173

# Frontend Configuration
NG_APP_API_BASE=http://localhost:8080
```

## Key Design Decisions

### Multi-Stage Builds
Both Dockerfiles use multi-stage builds to minimize image size:
- **Build stage**: Full SDK/tools for compilation
- **Runtime stage**: Minimal runtime image

### Backend Image
- **Base**: `eclipse-temurin:21-jre-alpine` (small JRE image)
- **Gradle caching**: Dependencies downloaded before source copy
- **Directory creation**: `/data` and `/storage/attachments` created at build time

### Frontend Image
- **Build-time API URL**: `NG_APP_API_BASE` injected via sed
- **Nginx**: Serves static files with SPA routing support
- **try_files**: Redirects all routes to index.html for Angular routing

### Docker Compose Features
- **Health check**: Backend must be healthy before frontend starts
- **Named volumes**: Persistent storage for database and attachments
- **Environment variables**: Loaded from `.env` file with defaults
- **Restart policy**: `unless-stopped` for production reliability

### Volume Mapping
| Volume | Container Path | Purpose |
|--------|---------------|---------|
| `data` | `/data` | SQLite database |
| `attachments` | `/storage/attachments` | Uploaded files |

## Deployment Commands

### Build and Run
```bash
# Copy environment file
cp .env.example .env
# Edit .env with your Confluence credentials

# Build and start
docker compose up --build

# Or with Podman
podman compose up --build
```

### Access Points
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api/health

### Stop and Clean
```bash
# Stop containers
docker compose down

# Stop and remove volumes (deletes data!)
docker compose down -v
```

### Build Images Manually
```bash
# Backend
docker build -t confluence-backend -f backend/Dockerfile .

# Frontend
docker build -t confluence-frontend \
  --build-arg NG_APP_API_BASE=http://localhost:8080 \
  -f frontend/Dockerfile .
```

## Verification
- Both images build successfully
- Backend starts and passes health check
- Frontend serves Angular app
- API calls from frontend reach backend
- Data persists across container restarts
- Environment variables are properly loaded
