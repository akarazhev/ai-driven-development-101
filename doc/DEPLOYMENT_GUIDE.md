# Deployment Guide

This guide covers deploying the Confluence Publisher application to production environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Backend Deployment](#backend-deployment)
3. [Frontend Deployment](#frontend-deployment)
4. [Database Setup](#database-setup)
5. [Storage Configuration](#storage-configuration)
6. [Environment Variables](#environment-variables)
7. [Security Considerations](#security-considerations)
8. [Monitoring and Logging](#monitoring-and-logging)
9. [Scaling Considerations](#scaling-considerations)

---

## Prerequisites

### Production Requirements

- **Java**: 21 or higher
- **Node.js**: 18 or higher (for building frontend)
- **Database**: PostgreSQL/MySQL (recommended) or SQLite (development only)
- **Storage**: Persistent storage for file attachments
- **Reverse Proxy**: Nginx or Apache (recommended)
- **SSL/TLS**: HTTPS certificate (required for production)

### Infrastructure Options

- **Cloud Platforms**: AWS, Azure, GCP, Heroku, Railway
- **Container Orchestration**: Docker Compose, Kubernetes
- **Traditional Servers**: Linux/Windows Server with Java runtime

---

## Backend Deployment

### Option 1: JAR File Deployment

1. **Build the JAR:**
```bash
cd backend
./gradlew clean build
# Windows: .\gradlew.bat clean build
```

2. **JAR location:**
```
backend/build/libs/confluence-publisher-0.0.1-SNAPSHOT.jar
```

3. **Run the JAR:**
```bash
java -jar confluence-publisher-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=production \
  --APP_DATABASE_URL=jdbc:postgresql://localhost:5432/confluence_pub \
  --APP_ATTACHMENT_DIR=/var/app/storage/attachments
```

4. **Run as a service (systemd on Linux):**
```ini
# /etc/systemd/system/confluence-publisher.service
[Unit]
Description=Confluence Publisher Backend
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/confluence-publisher
ExecStart=/usr/bin/java -jar /opt/confluence-publisher/confluence-publisher-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable confluence-publisher
sudo systemctl start confluence-publisher
```

### Option 2: Docker Deployment

1. **Build the image:**
```bash
docker build -t confluence-publisher-backend -f backend/Dockerfile .
```

2. **Run the container:**
```bash
docker run -d \
  --name confluence-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e APP_DATABASE_URL=jdbc:postgresql://db:5432/confluence_pub \
  -e APP_ATTACHMENT_DIR=/storage/attachments \
  -v confluence_data:/data \
  -v confluence_attachments:/storage/attachments \
  confluence-publisher-backend
```

3. **Using Docker Compose:**
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  backend:
    build:
      context: .
      dockerfile: backend/Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - APP_DATABASE_URL=jdbc:postgresql://db:5432/confluence_pub
      - APP_ATTACHMENT_DIR=/storage/attachments
    volumes:
      - backend_data:/data
      - backend_attachments:/storage/attachments
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=confluence_pub
      - POSTGRES_USER=confluence_user
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  backend_data:
  backend_attachments:
  postgres_data:
```

Run:
```bash
docker compose -f docker-compose.prod.yml up -d
```

---

## Frontend Deployment

### Option 1: Static Hosting

1. **Build the frontend:**
```bash
cd frontend
npm install
npm run build
```

2. **Build output:**
```
frontend/dist/confluence-publisher/
```

3. **Deploy to static hosting:**
   - **Nginx:**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       root /var/www/confluence-publisher;
       index index.html;
       
       location / {
           try_files $uri $uri/ /index.html;
       }
       
       location /api {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

   - **Apache:**
   ```apache
   <VirtualHost *:80>
       ServerName your-domain.com
       DocumentRoot /var/www/confluence-publisher
       
       <Directory /var/www/confluence-publisher>
           Options -Indexes +FollowSymLinks
           AllowOverride All
           Require all granted
       </Directory>
       
       ProxyPass /api http://localhost:8080/api
       ProxyPassReverse /api http://localhost:8080/api
   </VirtualHost>
   ```

   - **Cloud Storage:**
     - AWS S3 + CloudFront
     - Azure Blob Storage + CDN
     - Google Cloud Storage + CDN

### Option 2: Docker Deployment

1. **Build the image:**
```bash
docker build -t confluence-publisher-frontend \
  --build-arg NG_APP_API_BASE=https://api.your-domain.com \
  -f frontend/Dockerfile .
```

2. **Run the container:**
```bash
docker run -d \
  --name confluence-frontend \
  -p 80:80 \
  confluence-publisher-frontend
```

---

## Database Setup

### PostgreSQL (Recommended for Production)

1. **Create database:**
```sql
CREATE DATABASE confluence_pub;
CREATE USER confluence_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE confluence_pub TO confluence_user;
```

2. **Update application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/confluence_pub
    username: confluence_user
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

3. **Add PostgreSQL dependency to build.gradle:**
```gradle
dependencies {
    runtimeOnly 'org.postgresql:postgresql'
}
```

### MySQL

1. **Create database:**
```sql
CREATE DATABASE confluence_pub;
CREATE USER 'confluence_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON confluence_pub.* TO 'confluence_user'@'localhost';
```

2. **Update application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/confluence_pub
    username: confluence_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.MySQLDialect
```

### SQLite (Development Only)

**Not recommended for production** due to:
- Limited concurrency
- No network access
- File locking issues

If you must use SQLite:
```yaml
spring:
  datasource:
    url: jdbc:sqlite:/var/app/data/app.db
```

---

## Storage Configuration

### Local File System

1. **Create storage directory:**
```bash
sudo mkdir -p /var/app/storage/attachments
sudo chown appuser:appuser /var/app/storage/attachments
sudo chmod 755 /var/app/storage/attachments
```

2. **Configure in application.yml:**
```yaml
app:
  attachment-dir: /var/app/storage/attachments
```

### Cloud Storage (Recommended for Production)

**AWS S3:**
```yaml
app:
  storage-type: s3
  s3:
    bucket: confluence-attachments
    region: us-east-1
    access-key: ${AWS_ACCESS_KEY}
    secret-key: ${AWS_SECRET_KEY}
```

**Azure Blob Storage:**
```yaml
app:
  storage-type: azure
  azure:
    account-name: ${AZURE_STORAGE_ACCOUNT}
    account-key: ${AZURE_STORAGE_KEY}
    container: attachments
```

---

## Environment Variables

### Required Environment Variables

```bash
# Database
APP_DATABASE_URL=jdbc:postgresql://localhost:5432/confluence_pub
DB_USERNAME=confluence_user
DB_PASSWORD=your_secure_password

# Storage
APP_ATTACHMENT_DIR=/var/app/storage/attachments

# Confluence
APP_CONFLUENCE_URL=https://your-domain.atlassian.net
APP_CONFLUENCE_API_TOKEN=your_api_token
APP_CONFLUENCE_DEFAULT_SPACE=DEV

# Provider
APP_PROVIDER=confluence-api  # or confluence-stub for development

# Security
APP_CORS_ORIGINS=https://your-domain.com,https://www.your-domain.com
JWT_SECRET=your_jwt_secret_key  # If implementing authentication

# Scheduler
APP_SCHEDULER_INTERVAL_SECONDS=60
```

### Setting Environment Variables

**Docker:**
```bash
docker run -e APP_DATABASE_URL=... -e APP_CONFLUENCE_URL=... ...
```

**Docker Compose:**
```yaml
environment:
  - APP_DATABASE_URL=${APP_DATABASE_URL}
  - APP_CONFLUENCE_URL=${APP_CONFLUENCE_URL}
```

**Systemd service:**
```ini
[Service]
Environment="APP_DATABASE_URL=jdbc:postgresql://localhost:5432/confluence_pub"
Environment="APP_CONFLUENCE_URL=https://your-domain.atlassian.net"
```

**Kubernetes:**
```yaml
env:
  - name: APP_DATABASE_URL
    valueFrom:
      secretKeyRef:
        name: confluence-secrets
        key: database-url
```

---

## Security Considerations

### 1. Authentication & Authorization

**Current State:** API has no authentication (development only)

**Production Requirements:**
- Implement JWT or OAuth2 authentication
- Add role-based access control (RBAC)
- Secure API endpoints with authentication middleware

**Example JWT Implementation:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Add JWT authentication filter
    // Configure protected endpoints
}
```

### 2. HTTPS/TLS

**Required for production:**
- Obtain SSL certificate (Let's Encrypt, commercial CA)
- Configure reverse proxy (Nginx/Apache) with SSL
- Redirect HTTP to HTTPS
- Use HSTS headers

**Nginx SSL Configuration:**
```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /etc/ssl/certs/your-domain.crt;
    ssl_certificate_key /etc/ssl/private/your-domain.key;
    
    # SSL configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
}
```

### 3. API Security

- **Rate Limiting:** Implement rate limiting (e.g., Spring Cloud Gateway, Redis)
- **Input Validation:** Already implemented with Jakarta Validation
- **SQL Injection:** Use parameterized queries (JPA handles this)
- **XSS Protection:** Frontend should sanitize user input
- **CSRF Protection:** Add CSRF tokens for state-changing operations

### 4. File Upload Security

- **File Size Limits:** Already set to 10MB (configurable)
- **File Type Validation:** Add MIME type checking
- **Virus Scanning:** Integrate ClamAV or similar
- **Storage Isolation:** Store uploads outside web root

### 5. Secrets Management

**Never commit secrets to version control!**

**Options:**
- Environment variables
- Secret management services (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)
- Kubernetes secrets
- Docker secrets

---

## Monitoring and Logging

### Application Logging

**Configure logging in application.yml:**
```yaml
logging:
  level:
    root: INFO
    com.confluence.publisher: DEBUG
  file:
    name: /var/log/confluence-publisher/application.log
    max-size: 10MB
    max-history: 30
```

### Health Checks

**Spring Boot Actuator:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

**Access health endpoint:**
```
GET /actuator/health
```

### Monitoring Tools

- **Application Performance Monitoring (APM):**
  - New Relic
  - Datadog
  - Elastic APM

- **Log Aggregation:**
  - ELK Stack (Elasticsearch, Logstash, Kibana)
  - Splunk
  - CloudWatch Logs

- **Metrics:**
  - Prometheus + Grafana
  - Micrometer (built into Spring Boot)

---

## Scaling Considerations

### Horizontal Scaling

**Backend:**
- Stateless design (already implemented)
- Use load balancer (Nginx, HAProxy, AWS ALB)
- Session management (if needed): Redis
- Database connection pooling (HikariCP already configured)

**Frontend:**
- Static files can be served from CDN
- No server-side state

### Database Scaling

- **Read Replicas:** For read-heavy workloads
- **Connection Pooling:** Already configured with HikariCP
- **Caching:** Add Redis for frequently accessed data

### File Storage Scaling

- **Cloud Storage:** Use S3, Azure Blob, or GCS for unlimited scaling
- **CDN:** Serve attachments via CDN for better performance

---

## Deployment Checklist

- [ ] Database configured and migrated
- [ ] Storage directory created and permissions set
- [ ] Environment variables configured
- [ ] SSL/TLS certificate installed
- [ ] Authentication implemented (if required)
- [ ] CORS configured for production domains
- [ ] Logging configured
- [ ] Monitoring set up
- [ ] Backup strategy in place
- [ ] Health checks configured
- [ ] Error tracking (Sentry, Rollbar) configured
- [ ] Documentation updated with production URLs
- [ ] Load testing completed
- [ ] Security audit performed

---

## Rollback Procedure

1. **Stop current deployment:**
```bash
sudo systemctl stop confluence-publisher
# or
docker stop confluence-backend
```

2. **Restore previous version:**
```bash
# Restore JAR from backup
# or
docker tag confluence-backend:previous confluence-backend:latest
```

3. **Restore database (if needed):**
```bash
pg_restore -d confluence_pub backup.dump
```

4. **Start previous version:**
```bash
sudo systemctl start confluence-publisher
# or
docker start confluence-backend
```

---

## Support

For issues or questions:
- Check logs: `/var/log/confluence-publisher/application.log`
- Review API documentation: [doc/API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- Check health endpoint: `GET /api/health`

