### STAGE 1: Run ###
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Baumberger"
WORKDIR /app
COPY ./target/gateway.jar /app/

EXPOSE 80

CMD ["java", "-Xmx200m", "-jar", "gateway.jar"]
