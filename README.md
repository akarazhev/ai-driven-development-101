# ai-driven-development-101

Full-stack Social Media Automation App implementing the course project (Chapter 06).

## Stack

- Backend: FastAPI, SQLModel (SQLite), Uvicorn, pydantic-settings
- Frontend: Angular 20 + TypeScript + TailwindCSS

## Prerequisites

- Python 3.13
- Node.js 18+ and npm

## Setup

1) Python environment

```bash
python3.13 -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip
pip install -r requirements.txt
cp .env.example .env
```

2) Start backend

```bash
uvicorn backend.main:app --reload --port 8000
```

3) Frontend

```bash
cd frontend
npm install
npm start
# or
ng serve
```

Open http://localhost:4200 and ensure the backend is running on http://localhost:8000.

## Project structure

```
backend/
  routers/        # media, posts, schedules, providers, ai
  providers/      # adapter pattern; includes stub provider
  services/       # publish service
  models.py       # SQLModel tables
  db.py           # engine/session init
  scheduler.py    # background scheduler for queued posts
  config.py       # settings via .env
frontend/
  src/pages/      # Compose, Schedules
  src/app/        # app component and routing
data/             # sqlite database (gitignored)
storage/media/    # uploaded media (gitignored)
```

## API quickstart

- Health: GET /api/health
- Upload media: POST /api/media (multipart: file, alt_text?)
- Create post: POST /api/posts { text, media_ids }
- Publish now: POST /api/providers/default/publish { post_id }
- Schedule: POST /api/schedules { post_id, scheduled_at? }
- List schedules: GET /api/schedules

## Tests

```bash
pytest -q
```

## Notes

- The provider is a stub by default; add real providers via the adapter interface.
- For production, configure a proper database and storage, secure secrets, and harden CORS.

## Containerization (Docker/Podman)

The repo includes Dockerfiles for backend and frontend and a docker-compose.yml for local runs with Docker or Podman.

### Quickstart (Docker)

- Build and run
  ```bash
  docker compose up --build
  ```
- Open
  - Frontend: http://localhost:8080
  - Backend: http://localhost:8000/api/health
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
  - Backend: http://localhost:8000/api/health
- Stop / clean
  ```bash
  podman compose down
  podman compose down -v
  ```

### Compose services and ports

- backend
  - Image: built from backend/Dockerfile
  - Port: 8000 (host -> container)
  - Volumes: named `data` (SQLite at /data/app.db), `media` (/storage/media)
  - Env (set in compose):
    - `database_url=sqlite:////data/app.db`
    - `media_dir=/storage/media`
    - `provider=stub`
    - `scheduler_interval_seconds=5`
    - `cors_origins=["http://localhost:5173","http://localhost:8080","http://localhost"]`
- frontend
  - Image: built from frontend/Dockerfile
  - Port: 8080 (host -> container)
  - Build arg: `NG_APP_API_BASE=http://localhost:8000` so the browser calls the backend via host port 8000.

### Customizing configuration

- Backend environment variables (mirrors `.env.example`; lowercase keys for pydantic-settings v2):
  - `database_url` (default `sqlite:///./data/app.db` in dev, `sqlite:////data/app.db` in container)
  - `media_dir` (default `storage/media`, container uses `/storage/media`)
  - `provider` (`stub` by default)
  - `scheduler_interval_seconds` (default `5`)
  - `cors_origins` JSON array of allowed origins
- Frontend API base
  - Set at build time via compose `build.args.NG_APP_API_BASE` (default `http://localhost:8000`).
  - Change when deploying behind another host/port, then rebuild the frontend image.

### Build images manually (optional)

```bash
# Backend image
docker build -t social-backend -f backend/Dockerfile .
docker run --rm -p 8000:8000 \
  -e database_url=sqlite:////data/app.db \
  -e media_dir=/storage/media \
  -v social_data:/data -v social_media:/storage/media \
  social-backend

# Frontend image
docker build -t social-frontend \
  --build-arg NG_APP_API_BASE=http://localhost:8000 \
  -f frontend/Dockerfile .
docker run --rm -p 8080:80 social-frontend
```

### Data persistence

- Compose uses named volumes: `data` for the SQLite DB and `media` for uploaded files.
- Remove volumes with `docker compose down -v` (or `podman compose down -v`). This will delete your DB and uploaded media.

### Troubleshooting

- Port already in use
  - Change host ports in docker-compose.yml (e.g., `8001:8000`, `8081:80`).
- CORS blocked
  - Add your origin to `cors_origins` in docker-compose.yml and recreate containers.
- Frontend cannot reach backend
  - Ensure backend is mapped to host port 8000 and frontend is built with `NG_APP_API_BASE` pointing to `http://localhost:8000`.
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
  6. [06. Project: Social Media Automation App](doc/course/06-project-social-media-automation-app/README.md)

- Additional: [Course init brief](doc/ai/init-course.md)
