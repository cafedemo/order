FROM alpine:latest

# Install OpenJDK 21
RUN apk update && apk add --no-cache openjdk21

# Create a directory for the project
RUN mkdir /project && chown -R 1001:1001 /project

# Copy the application JAR file to the project directory
COPY build/libs/order-0.0.1-SNAPSHOT.jar /project/app.jar
# COPY ops/sbcp.p12 /project/sbcp.p12

# Set the working directory
WORKDIR /project

# Set the user
USER 1001

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/project/app.jar"]
