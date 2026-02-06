# Use Java 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy pom.xml and src folder
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apk add --no-cache maven

# Build the Spring Boot jar
RUN mvn clean package -DskipTests

# Expose default Spring Boot port
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/virtualai-backend-0.0.1-SNAPSHOT.jar"]