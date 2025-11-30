from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pathlib import Path
from .config import get_settings
from .db import init_db
from .scheduler import start_scheduler, stop_scheduler
from .routers import media, posts, schedules, providers, ai

app = FastAPI(title="Social Media Automation App")

settings = get_settings()
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.on_event("startup")
async def _startup() -> None:
    Path("data").mkdir(parents=True, exist_ok=True)
    Path(settings.media_dir).mkdir(parents=True, exist_ok=True)
    init_db()
    await start_scheduler()

@app.on_event("shutdown")
async def _shutdown() -> None:
    await stop_scheduler()

@app.get("/api/health")
def health() -> dict:
    return {"status": "ok"}

app.include_router(media.router, prefix="/api")
app.include_router(posts.router, prefix="/api")
app.include_router(schedules.router, prefix="/api")
app.include_router(providers.router, prefix="/api")
app.include_router(ai.router, prefix="/api")
