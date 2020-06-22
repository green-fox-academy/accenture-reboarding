FROM gradle:6.5.0-jre14 AS builder
WORKDIR /workspace/app
COPY src src
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
RUN gradle build --no-daemon

FROM openjdk:15-jdk-alpine
COPY --from=builder /workspace/app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
