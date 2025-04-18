# Use a lightweight Java image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy build output
COPY build/libs/*.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
