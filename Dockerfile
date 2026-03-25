# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
#   Uses a full Maven + JDK 21 image to compile and package the application.
#   The resulting fat-JAR will be copied to the lean runtime stage.
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

# Copy dependency descriptor first — lets Docker cache the layer
# and skip the expensive `mvn dependency:go-offline` on code-only changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy the rest of the source tree and build the fat-JAR (skip tests here;
# tests run in the CI stage before the Docker build starts).
COPY src ./src
RUN mvn package -DskipTests -B --no-transfer-progress

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime
#   Lean Alpine JRE-only image — no compiler, no Maven, ~100 MB smaller.
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Non-root user for security (principle of least privilege)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy only the fat-JAR produced by the build stage
COPY --from=build /workspace/target/study-core.jar app.jar

# Ensure the non-root user owns the file
RUN chown appuser:appgroup app.jar

USER appuser

# Spring Boot default port
EXPOSE 8080

# Docker health check — calls the Spring Actuator health endpoint.
# start_period: gives the JVM / Flyway time to finish startup before counting failures.
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/v1/actuator/health | grep -q '"status":"UP"' || exit 1

# JAVA_OPTS lets operators inject JVM tuning flags at runtime, e.g.:
#   -Xmx512m -XX:+UseContainerSupport -Dspring.profiles.active=prod
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

