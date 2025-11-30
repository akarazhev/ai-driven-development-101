from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from typing import List
from sqlmodel import Session, select
from ..db import get_session
from ..models import Post, PostMedia, MediaAsset

router = APIRouter(tags=["posts"])

class PostCreate(BaseModel):
    text: str
    media_ids: List[int] = []

@router.post("/posts")
def create_post(payload: PostCreate, session: Session = Depends(get_session)) -> dict:
    post = Post(text=payload.text)
    session.add(post)
    session.commit()
    session.refresh(post)
    for idx, mid in enumerate(payload.media_ids):
        link = PostMedia(post_id=post.id, media_id=mid, position=idx)
        session.add(link)
    session.commit()
    return {"id": post.id, "text": post.text, "media_ids": payload.media_ids}

@router.get("/posts/{post_id}")
def get_post(post_id: int, session: Session = Depends(get_session)) -> dict:
    post = session.get(Post, post_id)
    if not post:
        raise HTTPException(status_code=404, detail="not_found")
    links = session.exec(select(PostMedia).where(PostMedia.post_id == post_id)).all()
    media = []
    for l in links:
        m = session.get(MediaAsset, l.media_id)
        if m:
            media.append({"id": m.id, "filename": m.filename, "alt_text": m.alt_text})
    return {"id": post.id, "text": post.text, "media": media}
