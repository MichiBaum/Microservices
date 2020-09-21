### STAGE 1: Run ###
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Baumberger"
WORKDIR /app
COPY ./target/monitoring.jar /app/

EXPOSE 9000

CMD ["java", "-Xmx200m", "-jar", "monitoring.jar"]
