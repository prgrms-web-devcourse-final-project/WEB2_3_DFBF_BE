# Start with a base image containing Java runtime
FROM eclipse-temurin:17-jdk-jammy

# The application's jar file
ARG JAR_FILE=default/build/libs/soundlink_default.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]