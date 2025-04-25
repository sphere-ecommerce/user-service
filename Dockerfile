FROM openjdk:17-jdk-slim
# Set the working directory
WORKDIR /app

# Copy the jar file to the container
COPY target/user-service-*.jar user-service.jar

# Expose the application's port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "user-service.jar"]

