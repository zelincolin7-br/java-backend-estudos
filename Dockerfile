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

# Copia o JAR compilado do estágio de build
COPY --from=build /app/order-platform/target/*.jar app.jar

# Copia a pasta do New Relic da raiz do projeto para o container
COPY newrelic/ /app/newrelic/

EXPOSE 8080

# Executa o Java com o agente do New Relic ativo
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "app.jar"]
