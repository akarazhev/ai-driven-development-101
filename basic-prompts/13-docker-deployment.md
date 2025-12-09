# Prompt 13: Docker Deployment Configuration

## Role
You are an expert DevOps engineer.

## Task
Create Docker configuration for containerized deployment of the Confluence Publisher application.

## Files to Create

### 1. Backend Dockerfile (`backend/Dockerfile`)

**Build Stage**:
- Base: `gradle:8.5-jdk21`
- Copy Gradle files first (for dependency caching)
- Run `gradle dependencies`
- Copy source code
- Build with `gradle build -x test`

**Runtime Stage**:
- Base: `eclipse-temurin:21-jre-alpine`
- Create directories: /data, /storage/attachments
- Copy JAR from build stage
- Set environment variables for Docker profile
- Expose port 8080
- Entrypoint: `java -jar app.jar`

### 2. Frontend Dockerfile (`frontend/Dockerfile`)

**Build Stage**:
- Base: `node:20-alpine`
- Accept build arg `NG_APP_API_BASE`
- Install dependencies
- Replace apiBase in environment.prod.ts using sed
- Run `npm run build`

**Runtime Stage**:
- Base: `nginx:alpine`
- Copy nginx.conf
- Copy built files from dist folder
- Expose port 80

### 3. Nginx Configuration (`frontend/nginx.conf`)
- Listen on port 80
- Serve static files from /usr/share/nginx/html
- Use try_files for SPA routing (fallback to index.html)

### 4. Docker Compose (`docker-compose.yml`)

**Backend Service**:
- Build from backend/Dockerfile
- Port: 8080:8080
- Load .env file
- Environment variables for Confluence config
- Named volumes for data and attachments
- Health check: GET /api/health
- Restart: unless-stopped

**Frontend Service**:
- Build from frontend/Dockerfile with NG_APP_API_BASE arg
- Port: 4200:80
- Depends on backend (healthy)
- Restart: unless-stopped

**Volumes**:
- data: for SQLite database
- attachments: for uploaded files

### 5. Environment Example (`.env.example`)
```
CONFLUENCE_URL=https://your-domain.atlassian.net/confluence
CONFLUENCE_USERNAME=your-username
CONFLUENCE_API_TOKEN=your-api-token
CONFLUENCE_DEFAULT_SPACE=YOUR_SPACE_KEY
CONFLUENCE_PROVIDER=confluence-server
SCHEDULER_INTERVAL_SECONDS=5
CORS_ORIGINS=http://localhost:4200,http://localhost:8080
NG_APP_API_BASE=http://localhost:8080
```

## Deployment Commands

**Build and Run**:
```bash
cp .env.example .env
# Edit .env with credentials
docker compose up --build
```

**Access**:
- Frontend: http://localhost:4200
- Backend: http://localhost:8080/api/health

**Stop**:
```bash
docker compose down      # Stop containers
docker compose down -v   # Also remove volumes
```

## Verification Criteria
- Both images build successfully
- Backend passes health check
- Frontend serves Angular app
- API calls work from frontend
- Data persists across restarts
- Environment variables loaded correctly
