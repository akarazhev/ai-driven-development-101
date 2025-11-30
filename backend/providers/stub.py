from typing import List, Tuple
from uuid import uuid4
from .base import BaseProvider

class StubProvider(BaseProvider):
    def publish(self, text: str, media_paths: List[str]) -> Tuple[str, str]:
        return (str(uuid4()), "ok")
