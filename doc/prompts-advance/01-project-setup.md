# Prompt 01: Project Setup and Configuration

## Context
You are an expert Java engineer and expert front-end engineer. Create a full-stack web application called "Confluence Publisher" - a tool for creating, scheduling, and publishing pages to Atlassian Confluence.

## Requirements

### Technology Stack
- **Backend**: Spring Boot 3.2 with Java 21
- **Frontend**: Angular 20 with TypeScript and TailwindCSS
- **Database**: SQLite with Hibernate/JPA
- **Build Tools**: Gradle (Kotlin DSL) for backend, npm for frontend

### Project Structure
Create the following directory structure:
```
project-root/
├── backend/
│   ├── src/main/java/com/confluence/publisher/
│   ├── src/main/resources/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── Dockerfile
├── frontend/
│   ├── src/
│   ├── angular.json
│   ├── package.json
│   ├── tailwind.config.js
│   ├── nginx.conf
│   └── Dockerfile
├── data/                    # SQLite database (gitignored)
├── storage/attachments/     # Uploaded files (gitignored)
├── docker-compose.yml
├── .env.example
└── README.md
```

### Backend Configuration (build.gradle.kts)
```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.confluence"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Database - SQLite
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.4.0.Final")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    archiveFileName.set("confluence-publisher.jar")
}
```

### Application Configuration (application.yml)
Create `src/main/resources/application.yml`:
```yaml
spring:
  application:
    name: confluence-publisher
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.community.dialect.SQLiteDialect
        format_sql: true
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

app:
  app-name: confluence-publisher
  database-url: jdbc:sqlite:./data/app.db
  attachment-dir: storage/attachments
  confluence-url: ${CONFLUENCE_URL:https://your-domain.atlassian.net}
  confluence-username: ${CONFLUENCE_USERNAME:}
  confluence-default-space: ${CONFLUENCE_DEFAULT_SPACE:DEV}
  confluence-api-token: ${CONFLUENCE_API_TOKEN:}
  cors-origins:
    - http://localhost:4200
    - http://localhost:8080
    - http://localhost:5173
  provider: ${CONFLUENCE_PROVIDER:confluence-server}
  scheduler-interval-seconds: ${SCHEDULER_INTERVAL_SECONDS:5}

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    com.confluence.publisher: INFO
    org.springframework.web: INFO
    org.hibernate: WARN
```

### Frontend Configuration (package.json)
```json
{
  "name": "confluence-publisher-app",
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "ng": "ng",
    "start": "ng serve",
    "build": "ng build",
    "watch": "ng build --watch --configuration development"
  },
  "private": true,
  "dependencies": {
    "@angular/animations": "^20.0.0",
    "@angular/common": "^20.0.0",
    "@angular/compiler": "^20.0.0",
    "@angular/core": "^20.0.0",
    "@angular/forms": "^20.0.0",
    "@angular/platform-browser": "^20.0.0",
    "@angular/platform-browser-dynamic": "^20.0.0",
    "@angular/router": "^20.0.0",
    "rxjs": "~7.8.0",
    "tslib": "^2.3.0",
    "zone.js": "~0.15.0"
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^20.0.0",
    "@angular/cli": "^20.0.0",
    "@angular/compiler-cli": "^20.0.0",
    "@types/node": "^20.10.0",
    "autoprefixer": "^10.4.16",
    "postcss": "^8.4.31",
    "tailwindcss": "^3.4.1",
    "typescript": "~5.8.0"
  }
}
```

### TailwindCSS Configuration (tailwind.config.js)
```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### Environment Files
Create `frontend/src/environments/environment.ts`:
```typescript
export const environment = {
  apiBase: 'http://localhost:8080',
  production: false
};
```

Create `frontend/src/environments/environment.prod.ts`:
```typescript
export const environment = {
  apiBase: 'http://localhost:8080',
  production: true
};
```

### .env.example
```
# Confluence API Configuration
CONFLUENCE_URL=https://your-domain.atlassian.net/confluence
CONFLUENCE_USERNAME=your-username
CONFLUENCE_API_TOKEN=your-api-token-here
CONFLUENCE_DEFAULT_SPACE=YOUR_SPACE_KEY

# Application Configuration
CONFLUENCE_PROVIDER=confluence-server
SCHEDULER_INTERVAL_SECONDS=5
CORS_ORIGINS=http://localhost:4200,http://localhost:8080,http://localhost:5173

# Frontend Configuration
NG_APP_API_BASE=http://localhost:8080
```

## Expected Output
1. Complete project structure with all configuration files
2. Backend should compile and start with `./gradlew bootRun`
3. Frontend should install and start with `npm install && npm start`
4. Both should be ready for adding business logic in subsequent prompts

## Verification
- Backend starts on port 8080
- Frontend starts on port 4200
- No compilation errors in either project
