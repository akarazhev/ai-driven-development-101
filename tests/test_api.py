from fastapi.testclient import TestClient
from backend.main import app

def test_health():
    with TestClient(app) as client:
        r = client.get("/api/health")
        assert r.status_code == 200
        assert r.json()["status"] == "ok"

def test_post_create_and_publish():
    with TestClient(app) as client:
        # create post
        r = client.post("/api/posts", json={"text": "Hello World", "media_ids": []})
        assert r.status_code == 200
        post_id = r.json()["id"]
        # publish now
        r2 = client.post(f"/api/providers/default/publish", json={"post_id": post_id})
        assert r2.status_code == 200
        data = r2.json()
        assert data["status"] == "posted"

def test_schedule_create_and_list():
    with TestClient(app) as client:
        r = client.post("/api/posts", json={"text": "Scheduled", "media_ids": []})
        post_id = r.json()["id"]
        r2 = client.post("/api/schedules", json={"post_id": post_id})
        assert r2.status_code == 200
        sch_id = r2.json()["id"]
        r3 = client.get("/api/schedules")
        assert r3.status_code == 200
        rows = r3.json()
        assert any(row["id"] == sch_id for row in rows)
