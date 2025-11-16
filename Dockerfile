FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/target/trabajopractico-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]