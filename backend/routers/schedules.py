from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime, timezone
from sqlmodel import Session, select
from ..db import get_session
from ..models import Schedule

router = APIRouter(tags=["schedules"])

class ScheduleCreate(BaseModel):
    post_id: int
    scheduled_at: Optional[datetime] = None

@router.post("/schedules")
def create_schedule(payload: ScheduleCreate, session: Session = Depends(get_session)) -> dict:
    when = payload.scheduled_at or datetime.now(timezone.utc)
    sch = Schedule(post_id=payload.post_id, scheduled_at=when, status="queued")
    session.add(sch)
    session.commit()
    session.refresh(sch)
    return {"id": sch.id, "status": sch.status, "scheduled_at": when}

@router.get("/schedules/{schedule_id}")
def get_schedule(schedule_id: int, session: Session = Depends(get_session)) -> dict:
    sch = session.get(Schedule, schedule_id)
    if not sch:
        raise HTTPException(status_code=404, detail="not_found")
    return {
        "id": sch.id,
        "post_id": sch.post_id,
        "status": sch.status,
        "scheduled_at": sch.scheduled_at,
        "attempt_count": sch.attempt_count,
        "last_error": sch.last_error,
    }

@router.get("/schedules")
def list_schedules(session: Session = Depends(get_session)) -> List[dict]:
    rows = session.exec(select(Schedule).order_by(Schedule.id.desc()).limit(100)).all()
    return [
        {
            "id": s.id,
            "post_id": s.post_id,
            "status": s.status,
            "scheduled_at": s.scheduled_at,
            "attempt_count": s.attempt_count,
        }
        for s in rows
    ]
