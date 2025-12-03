# Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application
RUN gradle build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create directories for data and media
RUN mkdir -p /data /storage/media

# Copy built JAR
COPY --from=build /app/build/libs/social-media-automation.jar app.jar

# Default environment variables (can be overridden)
ENV SPRING_PROFILES_ACTIVE=docker \
    APP_DATABASE_URL=jdbc:sqlite:///data/app.db \
    APP_MEDIA_DIR=/storage/media \
    APP_PROVIDER=stub \
    APP_SCHEDULER_INTERVAL_SECONDS=5

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

