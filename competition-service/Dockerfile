# Use multi-stage build for Spring Boot
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copier le fichier pom.xml depuis le dossier competition-service
COPY competition-service/pom.xml .
RUN mvn dependency:go-offline

# Copier le code source
COPY competition-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]