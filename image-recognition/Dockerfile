FROM adamgyulavari/opencv-java:4.3.0 AS builder
WORKDIR /workspace/app
COPY src src
COPY libs libs
COPY examples examples
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY gradle gradle
COPY gradlew gradlew
ENV AWS_ACCESS=asd
ENV AWS_SECRET=asd
ENV AWS_BUCKET=asd
RUN ./gradlew build --no-daemon

FROM adamgyulavari/opencv-java:4.3.0
WORKDIR /workspace/app
COPY --from=builder /workspace/app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
