# syntax=docker/dockerfile:1.7

# =============================================================================
# mangala-authentication Dockerfile
# Multi-stage build for Spring Boot 3.4 / Java 21
# =============================================================================
# Build from mangala-authentication directory:
#   DOCKER_BUILDKIT=1 docker build -t mangala-auth .
# =============================================================================

# -----------------------------------------------------------------------------
# Build stage: Compile and package the application
# -----------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy submodule POMs first for dependency caching
COPY mangala-common-security/pom.xml mangala-common-security/pom.xml
COPY mangala-exception/pom.xml mangala-exception/pom.xml
COPY pom.xml .

# Download dependencies first (cached layer) - this step caches dependencies
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B -q -T 1C || true

# Copy submodule sources and build them
COPY mangala-common-security/src mangala-common-security/src
COPY mangala-exception/src mangala-exception/src

# Build common modules with Maven cache and parallel execution
RUN --mount=type=cache,target=/root/.m2/repository \
    cd /app/mangala-common-security && \
    mvn clean install -Dmaven.test.skip=true -B -q -T 1C && \
    cd /app/mangala-exception && \
    mvn clean install -Dmaven.test.skip=true -B -q -T 1C

# Copy main application source code (changes frequently)
COPY src src

# Build the application with layered JAR support
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -Dmaven.test.skip=true -B -q -T 1C && \
    # Extract layered JAR for faster startup and better caching
    java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination extracted

# -----------------------------------------------------------------------------
# Runtime stage: Minimal production image
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# OCI Labels for container metadata
LABEL org.opencontainers.image.title="mangala-authentication" \
      org.opencontainers.image.description="Mangala Wallet Authentication Service" \
      org.opencontainers.image.vendor="Mangala" \
      org.opencontainers.image.source="https://github.com/mangala/mangala-authentication" \
      org.opencontainers.image.licenses="Proprietary"

# Build arguments
ARG APP_USER=mangala
ARG APP_GROUP=mangala
ARG APP_UID=1000
ARG APP_GID=1000

# Install minimal runtime dependencies and create non-root user in single layer
RUN apk add --no-cache \
        curl \
        tzdata && \
    # Remove apk cache
    rm -rf /var/cache/apk/* && \
    # Create non-root user with specific UID/GID
    addgroup -g ${APP_GID} -S ${APP_GROUP} && \
    adduser -u ${APP_UID} -S -G ${APP_GROUP} -h /app -s /sbin/nologin ${APP_USER} && \
    # Create necessary directories
    mkdir -p /app/logs /app/tmp && \
    chown -R ${APP_USER}:${APP_GROUP} /app

WORKDIR /app

# Copy extracted layers (Spring Boot layered JAR for optimal caching)
# Order matters: dependencies change less than application code
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/extracted/dependencies/ ./
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/extracted/spring-boot-loader/ ./
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/extracted/application/ ./

# Switch to non-root user
USER ${APP_USER}

# Expose port
EXPOSE 8080

# Environment variables for runtime configuration
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom \
    -Djava.io.tmpdir=/app/tmp"

# Spring profile (override in deployment)
ENV SPRING_PROFILES_ACTIVE="default"

# Health check using curl
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 CMD curl --fail --silent --max-time 3 http://localhost:8080/actuator/health/liveness || exit 1

# Signal for graceful shutdown
STOPSIGNAL SIGTERM

# Use exec form for proper signal handling
# JAVA_TOOL_OPTIONS is automatically picked up by the JVM
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
