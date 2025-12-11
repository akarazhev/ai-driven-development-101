# Deployment Guide

## Security Best Practices

### Environment Variables and Secrets Management

This project uses `.env` files for local development and environment variables for production deployments.

#### Local Development

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your credentials:**
   ```bash
   CONFLUENCE_URL=https://your-domain.atlassian.net/confluence
   CONFLUENCE_USERNAME=your-username
   CONFLUENCE_API_TOKEN=your-api-token-here
   CONFLUENCE_DEFAULT_SPACE=YOUR_SPACE_KEY
   ```

3. **Set proper file permissions:**
   ```bash
   chmod 600 .env
   ```

⚠️ **Important**: The `.env` file is already in `.gitignore` and will NOT be committed to the repository.

#### Production Deployment

For production environments, use one of these secure methods:

##### Option 1: Environment Variables (Recommended for Docker/Podman)

Set environment variables before running compose:

```bash
export CONFLUENCE_URL="https://your-domain.atlassian.net/confluence"
export CONFLUENCE_USERNAME="your-username"
export CONFLUENCE_API_TOKEN="your-api-token"
export CONFLUENCE_DEFAULT_SPACE="YOUR_SPACE_KEY"
docker compose up -d
```

##### Option 2: Docker Secrets (Docker Swarm)

```bash
echo "your-api-token" | docker secret create confluence_api_token -
```

Then reference in `docker-compose.yml`:
```yaml
secrets:
  confluence_api_token:
    external: true
```

##### Option 3: Kubernetes Secrets

```bash
kubectl create secret generic confluence-credentials \
  --from-literal=url=https://your-domain.atlassian.net/confluence \
  --from-literal=username=your-username \
  --from-literal=api-token=your-api-token \
  --from-literal=space=YOUR_SPACE_KEY
```

##### Option 4: HashiCorp Vault / AWS Secrets Manager

For enterprise deployments, integrate with:
- HashiCorp Vault
- AWS Secrets Manager
- Azure Key Vault
- Google Secret Manager

## Running with Docker Compose

```bash
# Build and start services
docker compose up -d --build

# View logs
docker compose logs -f

# Stop services
docker compose down

# Stop and remove volumes
docker compose down -v
```

## Running with Podman Compose

```bash
# macOS: Start Podman VM first
podman machine start

# Build and start services
podman-compose -f podman-compose.yaml up -d --build

# View logs
podman-compose -f podman-compose.yaml logs -f

# Stop services
podman-compose -f podman-compose.yaml down
```

> **Note**: Both containers run as non-root users for rootless Podman compatibility:
> - Backend runs as `appuser` (UID 1000) on port 8080
> - Frontend nginx runs on port 8080 (non-privileged), mapped to host port 4200

## Security Checklist

- [ ] `.env` file is not committed to git
- [ ] `.env` file has permissions `600` (owner read/write only)
- [ ] API tokens are rotated regularly
- [ ] Secrets are stored in secure secret management system (production)
- [ ] Container images are scanned for vulnerabilities
- [ ] Network policies are configured (Kubernetes)
- [ ] TLS/SSL is enabled for production endpoints
- [ ] CORS origins are restricted to known domains
- [ ] Health checks are configured
- [ ] Logs don't contain sensitive information

## Troubleshooting

### Credentials not loading

1. Check that `.env` file exists and has correct format
2. Verify file permissions: `ls -la .env`
3. Check environment variables: `docker compose config`
4. View container environment: `docker compose exec backend env | grep CONFLUENCE`

### Permission denied errors

```bash
# Fix .env file permissions
chmod 600 .env

# Check current permissions
ls -la .env
```

### Secrets not found in container

Ensure `env_file` is properly configured in `docker-compose.yml`:
```yaml
env_file:
  - .env
```

