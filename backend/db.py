from sqlmodel import SQLModel, create_engine, Session
from sqlalchemy.orm import sessionmaker
from .config import get_settings

_settings = get_settings()
_connect_args = {"check_same_thread": False} if _settings.database_url.startswith("sqlite") else {}
engine = create_engine(_settings.database_url, echo=False, connect_args=_connect_args)
SessionLocal = sessionmaker(engine, class_=Session, expire_on_commit=False)

def init_db() -> None:
    SQLModel.metadata.create_all(engine)

def get_session():
    with SessionLocal() as session:
        yield session
