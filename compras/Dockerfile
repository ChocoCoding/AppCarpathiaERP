FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar compras.jar
EXPOSE 8702
ENTRYPOINT ["java", "-jar", "compras.jar"]