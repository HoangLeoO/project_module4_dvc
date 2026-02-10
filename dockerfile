# --- Giai đoạn 1: Build ---
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN sed -i 's/gradle-9.2.1-bin.zip/gradle-8.7-bin.zip/g' gradle/wrapper/gradle-wrapper.properties
RUN chmod +x gradlew

COPY src src
RUN ./gradlew clean bootJar -x test


# --- Giai đoạn 2: Run ---
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Không hard-code quá cao
ENTRYPOINT [
  "java",
  "-Xms128m",
  "-Xmx220m",
  "-XX:+UseContainerSupport",
  "-jar",
  "app.jar"
]
