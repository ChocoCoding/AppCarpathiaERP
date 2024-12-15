FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar compras_ventas.jar
EXPOSE 8704
ENTRYPOINT ["java", "-jar", "compras_ventas.jar"]