# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# The application's jar file
ARG JAR_FILE=./chat/build/libs/SoundLinkChat_Java-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]