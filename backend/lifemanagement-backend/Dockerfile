### STAGE 1: Package ###
FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD
MAINTAINER Michael Baumberger
ARG profile=docker
RUN echo 'ACTIVE PROFILE IS: $profile'
COPY pom.xml /build/
COPY ./src /build/src/
WORKDIR /build/
RUN mvn package -P $profile

### STAGE 2: Run ###
FROM openjdk:11-jre-slim
MAINTAINER Michael Baumberger
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/lifemanagement.jar /app/
ENTRYPOINT ["java", "-jar", "lifemanagement.jar"]
