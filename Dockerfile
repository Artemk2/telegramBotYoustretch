# Используйте базовый образ с поддержкой Java
FROM eclipse-temurin:19-jdk-jammy as base
# Установите рабочую директорию внутри контейнера
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src
COPY src/main/java/ ./src/main/java/

FROM base as development
#CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'"]
CMD ["./mvnw", "spring-boot:run"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:19-jre-jammy as production
EXPOSE 8080
COPY --from=build /app/target/telegramBotExhangeRate-*.jar /telegramBotExhangeRate.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/telegramBotExhangeRate.jar"]
