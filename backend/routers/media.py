from fastapi import APIRouter, UploadFile, File, Form, Depends, HTTPException
from sqlmodel import Session
from pathlib import Path
from uuid import uuid4
from ..config import get_settings
from ..db import get_session
from ..models import MediaAsset

router = APIRouter(tags=["media"])

@router.post("/media")
async def upload_media(
    file: UploadFile = File(...),
    alt_text: str | None = Form(None),
    session: Session = Depends(get_session),
) -> dict:
    settings = get_settings()
    media_dir = Path(settings.media_dir)
    media_dir.mkdir(parents=True, exist_ok=True)
    suffix = Path(file.filename).suffix or ""
    name = f"{uuid4().hex}{suffix}"
    path = media_dir / name
    content = await file.read()
    try:
        with open(path, "wb") as f:
            f.write(content)
    except Exception as e:  # noqa: BLE001
        raise HTTPException(status_code=500, detail=str(e))
    asset = MediaAsset(
        filename=file.filename,
        content_type=file.content_type or "application/octet-stream",
        size=len(content),
        storage_path=str(path),
        alt_text=alt_text,
    )
    session.add(asset)
    session.commit()
    session.refresh(asset)
    return {"id": asset.id, "filename": asset.filename, "alt_text": asset.alt_text}
