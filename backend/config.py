from functools import lru_cache
from typing import List
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    app_name: str = "social-app"
    database_url: str = "sqlite:///./data/app.db"
    media_dir: str = "storage/media"
    cors_origins: List[str] = ["http://localhost:5173"]
    provider: str = "stub"
    scheduler_interval_seconds: int = 5
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

@lru_cache
def get_settings() -> Settings:
    return Settings()
