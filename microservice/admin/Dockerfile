### STAGE 1: Run ###
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Baumberger"
WORKDIR /app
COPY ./target/admin.jar /app/

EXPOSE 8081

CMD ["java", "-Xmx200m", "-jar", "admin.jar"]
