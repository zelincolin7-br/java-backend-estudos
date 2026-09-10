# Estágio 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia e compila o projeto a partir da raiz
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o JAR compilado do estágio de build da raiz
COPY --from=build /app/target/*.jar app.jar

# Copia a pasta do New Relic para o container
COPY newrelic/ /app/newrelic/

EXPOSE 8080

# Executa o Java com o agente do New Relic ativo
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "app.jar"]