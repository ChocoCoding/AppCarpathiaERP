# Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -X

# Etapa de ejecución
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar frontend.jar
EXPOSE 8708
ENTRYPOINT ["java", "-jar", "app.jar"]