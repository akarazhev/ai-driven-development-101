import asyncio
from datetime import datetime, timezone
from sqlmodel import select
from .config import get_settings
from .db import SessionLocal
from .models import Schedule
from .services.publish import publish_post

_task = None
_running = False

async def _loop() -> None:
    global _running
    settings = get_settings()
    _running = True
    while _running:
        now = datetime.now(timezone.utc)
        try:
            with SessionLocal() as session:
                rows = session.exec(
                    select(Schedule).where(
                        Schedule.status == "queued", Schedule.scheduled_at <= now
                    ).limit(10)
                ).all()
                for sch in rows:
                    try:
                        log = publish_post(sch.post_id)
                        sch.status = "posted"
                        sch.attempt_count += 1
                        sch.last_error = None
                        session.add(sch)
                        session.commit()
                    except Exception as e:  # noqa: BLE001
                        sch.attempt_count += 1
                        sch.last_error = str(e)
                        sch.status = "failed"
                        session.add(sch)
                        session.commit()
        except Exception:
            pass
        await asyncio.sleep(settings.scheduler_interval_seconds)

async def start_scheduler() -> None:
    global _task
    if _task is None:
        _task = asyncio.create_task(_loop())

async def stop_scheduler() -> None:
    global _running, _task
    _running = False
    if _task:
        try:
            _task.cancel()
        finally:
            _task = None
