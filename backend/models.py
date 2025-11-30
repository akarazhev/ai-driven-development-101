from typing import Optional
from sqlmodel import SQLModel, Field
from datetime import datetime, timezone

class MediaAsset(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    filename: str
    content_type: str
    size: int
    storage_path: str
    alt_text: Optional[str] = None

class Post(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    text: str
    author_id: Optional[int] = None
    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

class PostMedia(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    post_id: int = Field(foreign_key="post.id")
    media_id: int = Field(foreign_key="mediaasset.id")
    position: int = 0

class Schedule(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    post_id: int = Field(foreign_key="post.id")
    scheduled_at: datetime
    status: str = "queued"
    attempt_count: int = 0
    last_error: Optional[str] = None

class PublishLog(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    post_id: int = Field(foreign_key="post.id")
    provider: str
    provider_account_id: Optional[str] = None
    external_id: Optional[str] = None
    status: str
    message: Optional[str] = None
    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
