# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle gradle/
COPY build.gradle ./
COPY src src/

# Install dos2unix and fix line endings, update CA certificates for TLS
RUN apt-get update && apt-get install -y dos2unix ca-certificates-java && \
    chmod +x gradlew && dos2unix gradlew && \
    ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --create-home --shell /usr/sbin/nologin workhub
USER workhub

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
