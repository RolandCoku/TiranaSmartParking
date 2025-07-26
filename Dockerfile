# Use Java 21 slim image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the Spring Boot JAR into the container
COPY target/*.jar app.jar

# Expose Render's dynamic port (will be provided via $PORT)
EXPOSE 8080

# Start the Spring Boot application
CMD ["java", "-jar", "app.jar"]
