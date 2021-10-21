## Stage 1 : Build with Maven builder image
FROM docker.io/library/maven:3.8.3-openjdk-17 AS builder
WORKDIR /usr/src/db2-sql-error
COPY pom.xml ./
RUN mvn dependency:resolve dependency:resolve-plugins
COPY src/main/java/ ./src/main/java/
RUN mvn clean package

## Stage 2 : Create the final runtime image
FROM docker.io/library/openjdk:11.0.12-jre
WORKDIR /usr/src/app
COPY --from=builder ./usr/src/db2-sql-error/target/*.jar ./
CMD java -jar db2-sql-error-*-jar-with-dependencies.jar
