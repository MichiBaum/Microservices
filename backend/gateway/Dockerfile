### STAGE 1: Package ###
FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD
LABEL maintainer="Michael Baumberger"
COPY pom.xml /build/
COPY ./src /build/src/
WORKDIR /build/
# TODO maybe delete skip test
RUN mvn -DskipTests=true package

### STAGE 2: Run ###
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Baumberger"
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/gateway.jar /app/

CMD ["java", "-Xmx200m", "-jar", "gateway.jar"]
