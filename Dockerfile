# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY target/BlogV2-0.0.1-SNAPSHOT.jar BlogV2-0.0.1-SNAPSHOT.jar
# Expose port 8080 to the outside world
EXPOSE 8090

# Execute the application
ENTRYPOINT ["java", "-jar", "BlogV2-0.0.1-SNAPSHOT.jar"]