from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(tags=["ai"])

class VariantsIn(BaseModel):
    message: str

@router.post("/ai/variants")
def variants(payload: VariantsIn) -> dict:
    base = payload.message.strip()
    outs = [
        base,
        base[:180] + " #update" if len(base) > 0 else "",
        base.upper()[:200],
    ]
    outs = [o for o in outs if o]
    return {"variants": outs[:3]}

class AltTextIn(BaseModel):
    description: str

@router.post("/ai/alt-text")
def alt_text(payload: AltTextIn) -> dict:
    desc = payload.description.strip()
    if not desc:
        desc = "Image"
    return {"alt_text": desc[:120]}
