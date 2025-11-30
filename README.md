# ai-driven-development-101

Full-stack Social Media Automation App implementing the course project (Chapter 06).

## Stack

- Backend: FastAPI, SQLModel (SQLite), Uvicorn, pydantic-settings
- Frontend: React + Vite + TypeScript + TailwindCSS

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
npm run dev
```

Open http://localhost:5173 and ensure the backend is running on http://localhost:8000.

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
  src/App.tsx     # routing
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
