# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (caches this layer so builds are faster)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code and compile the application
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled JAR from Stage 1 into this new container
COPY --from=build /app/target/BlogV2-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8092 to match application-prod.yml
EXPOSE 8092

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]