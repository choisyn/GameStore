FROM docker.1ms.run/library/maven:3.9.11-eclipse-temurin-21

WORKDIR /app

COPY pom.xml ./
COPY src ./src
COPY .m2repo ./.m2repo

RUN groupadd --system spring \
    && useradd --system --create-home --gid spring spring \
    && mkdir -p /app/uploads \
    && mvn -o -B -Dmaven.repo.local=/app/.m2repo -DskipTests package \
    && cp target/*.jar /app/app.jar \
    && rm -rf /app/.m2repo /app/src /app/target \
    && chown -R spring:spring /app

ENV TZ=Asia/Shanghai
ENV FILE_UPLOAD_PATH=/app/uploads
ENV JAVA_OPTS=""

EXPOSE 8080

USER spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
