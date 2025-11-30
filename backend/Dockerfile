# Backend Dockerfile
FROM python:3.13-slim AS runtime

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# Install deps
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# Copy app
COPY backend /app/backend

# Default environment (override via compose)
ENV database_url=sqlite:////data/app.db \
    media_dir=/storage/media \
    provider=stub \
    scheduler_interval_seconds=5

EXPOSE 8000
CMD ["uvicorn", "backend.main:app", "--host", "0.0.0.0", "--port", "8000"]
