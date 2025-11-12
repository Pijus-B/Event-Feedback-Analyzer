FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /Event-Feedback-Analyzer

COPY --link pom.xml .

RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline

COPY --link src ./src

RUN --mount=type=cache,target=/root/.m2/repository mvn package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /Event-Feedback-Analyzer

COPY --from=build /Event-Feedback-Analyzer/target/analyzer-1.0.jar analyzer.jar

CMD java -jar analyzer.jar