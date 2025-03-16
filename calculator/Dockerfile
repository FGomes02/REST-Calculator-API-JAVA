# Build stage
FROM maven:3.9.9-eclipse-temurin-23 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/calculator-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]