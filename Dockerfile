# Use OpenJDK 21 with Alpine as the base image
FROM eclipse-temurin:21-alpine

# Set the working directory in the Docker image
WORKDIR /llabdocs

# Copy the entire project source
COPY modules/llabdocs-rest-api/target/llabdocs-rest-api-*.jar /llabdocs/llabdocs-rest-api.jar

# Specify the entry point for the application
# ENTRYPOINT ["java","-jar","/llabdocs/llabdocs-rest-api.jar"]
# Debug mode
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","/llabdocs/llabdocs-rest-api.jar"]