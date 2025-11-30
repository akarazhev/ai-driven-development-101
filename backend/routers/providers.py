from fastapi import APIRouter, Depends
from pydantic import BaseModel
from sqlmodel import Session
from ..db import get_session
from ..services.publish import publish_post

router = APIRouter(tags=["providers"])

class PublishNow(BaseModel):
    post_id: int

@router.post("/providers/{account_id}/publish")
def publish_now(account_id: str, payload: PublishNow, session: Session = Depends(get_session)) -> dict:
    log = publish_post(payload.post_id)
    return {"log_id": log.id, "status": log.status, "external_id": log.external_id}
