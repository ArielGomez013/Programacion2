FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app


COPY pom.xml .

COPY src ./src

RUN apk add --no-cache maven 

Run mvn -q -e -B package
# Etapa final

EXPOSE 8080
CMD ["java", "-jar", "target/trabajopractico-0.0.1-SNAPSHOT.jar"]