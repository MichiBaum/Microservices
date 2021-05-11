FROM openjdk:16-jdk-alpine3.13 as build
WORKDIR /workspace/app

COPY ./admin/mvnw .
COPY ./admin/.mvn .mvn
COPY ./admin/pom.xml .
COPY ./admin/src src

RUN chmod a+x ./mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:16-jdk-alpine3.13
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","admin.Application"]