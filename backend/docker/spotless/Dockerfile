FROM maven:3.9-eclipse-temurin-25

WORKDIR /Event-Feedback-Analyzer

COPY --link pom.xml .

RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline

COPY --link src ./src