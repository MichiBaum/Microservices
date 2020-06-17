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

## THE LIFE SAVER
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.6.0/wait /wait
RUN chmod +x /wait

## Launch the wait tool and then your application
CMD /wait && java - jar lifemanagement.jar
