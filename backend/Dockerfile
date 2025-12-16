# Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle files
COPY backend/build.gradle.kts backend/settings.gradle.kts backend/gradle.properties ./
COPY backend/gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY backend/src ./src

# Build application
RUN gradle build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -D appuser

# Install wget for healthcheck and create directories with proper ownership
RUN apk add --no-cache wget && \
    mkdir -p /data /storage/attachments /app/data && \
    chown -R appuser:appgroup /data /storage /app

# Copy built JAR
COPY --from=build --chown=appuser:appgroup /app/build/libs/confluence-publisher.jar app.jar

# Switch to non-root user
USER appuser

# Default environment variables (can be overridden)
# Note: APP_PROVIDER is intentionally NOT set here to allow .env file override via CONFLUENCE_PROVIDER
ENV SPRING_PROFILES_ACTIVE=docker \
    APP_DATABASE_URL=jdbc:sqlite:///data/app.db \
    APP_ATTACHMENT_DIR=/storage/attachments \
    APP_SCHEDULER_INTERVAL_SECONDS=5

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

