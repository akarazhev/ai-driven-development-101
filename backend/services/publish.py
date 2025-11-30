from typing import List
from sqlmodel import select
from ..config import get_settings
from ..db import SessionLocal
from ..models import Post, PostMedia, MediaAsset, PublishLog
from ..providers.stub import StubProvider

_settings = get_settings()

def _get_provider():
    provider = _settings.provider.lower()
    if provider == "stub":
        return StubProvider()
    return StubProvider()

def publish_post(post_id: int) -> PublishLog:
    with SessionLocal() as session:
        post = session.get(Post, post_id)
        if not post:
            raise RuntimeError("post_not_found")
        links = session.exec(select(PostMedia).where(PostMedia.post_id == post_id)).all()
        media_paths: List[str] = []
        for l in links:
            m = session.get(MediaAsset, l.media_id)
            if m:
                media_paths.append(m.storage_path)
        provider = _get_provider()
        external_id, message = provider.publish(post.text, media_paths)
        log = PublishLog(
            post_id=post_id,
            provider=_settings.provider,
            external_id=external_id,
            status="posted",
            message=message,
        )
        session.add(log)
        session.commit()
        session.refresh(log)
        return log
