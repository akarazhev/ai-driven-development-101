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

# Create directories for data and attachments
RUN mkdir -p /data /storage/attachments /app/data

# Copy built JAR
COPY --from=build /app/build/libs/confluence-publisher.jar app.jar

# Default environment variables (can be overridden)
ENV SPRING_PROFILES_ACTIVE=docker \
    APP_DATABASE_URL=jdbc:sqlite:///data/app.db \
    APP_ATTACHMENT_DIR=/storage/attachments \
    APP_PROVIDER=confluence-stub \
    APP_SCHEDULER_INTERVAL_SECONDS=5

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

