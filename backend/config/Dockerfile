### STAGE 1: Run ###
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Baumberger"
WORKDIR /app
COPY ./target/config.jar /app/

EXPOSE 8888

CMD ["java", "-Xmx200m", "-jar", "config.jar"]
