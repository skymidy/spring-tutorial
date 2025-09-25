# --- Stage 1: Build the application ---
FROM maven:3.9.9-amazoncorretto-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to leverage Docker layer caching for dependencies
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application package (JAR)
# Assuming your Spring Boot plugin creates an executable JAR named boot-0.0.1-SNAPSHOT.jar
# based on your artifactId and version. Adjust if your final JAR name differs.
RUN mvn clean package -DskipTests


# --- Stage 2: Create the runtime image ---
FROM amazoncorretto:21-alpine AS runtime

# Create a non-root user for better security
RUN addgroup -g 1001 -S spring &&\
    adduser -u 1001 -S spring -G spring
USER spring:spring

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
# Adjust the JAR name if necessary (check your target/ directory after build)
COPY --from=build --chown=spring:spring /app/target/boot-0.0.1-SNAPSHOT.jar app.jar

# Expose the port of Spring Boot application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]