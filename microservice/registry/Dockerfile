### STAGE 1: Run ###
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Baumberger"
WORKDIR /app
COPY ./target/registry.jar /app/

EXPOSE 8761

CMD ["java", "-Xmx200m", "-jar", "registry.jar"]
