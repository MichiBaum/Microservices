FROM openjdk:16-jdk-alpine3.13 as build
WORKDIR /workspace/app

COPY . .

RUN ./registry/mvnw install -DskipTests
RUN mkdir -p registry/target/dependency && (cd registry/target/dependency; jar -xf ../*.jar)

FROM openjdk:16-jdk-alpine3.13
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","registry.Application"]