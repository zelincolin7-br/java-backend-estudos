# Estágio 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia e compila o submódulo order-platform
COPY pom.xml .
COPY order-platform/pom.xml ./order-platform/
COPY order-platform/src ./order-platform/src
RUN mvn clean package -pl order-platform -am -DskipTests

# Estágio 2: Imagem final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/order-platform/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
