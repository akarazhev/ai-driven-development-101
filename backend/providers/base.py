from typing import List, Tuple

class BaseProvider:
    def publish(self, text: str, media_paths: List[str]) -> Tuple[str, str]:
        raise NotImplementedError
