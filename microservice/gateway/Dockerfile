FROM openjdk:16-jdk-alpine3.13 as build
WORKDIR /workspace/app

COPY . .

RUN ./gateway/mvnw install -DskipTests
RUN mkdir -p gateway/target/dependency && (cd gateway/target/dependency; jar -xf ../*.jar)

FROM openjdk:16-jdk-alpine3.13
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","gateway.Application"]