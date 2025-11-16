FROM maven:3.9.6-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /app


COPY --from=build /app/target/trabajopractico-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]