# Build stage
FROM m.daocloud.io/docker.io/library/maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Skip tests to speed up build
RUN mvn clean package -DskipTests

# Run stage
FROM m.daocloud.io/docker.io/library/eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Install ffmpeg for video cover generation
RUN apk add --no-cache ffmpeg

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
