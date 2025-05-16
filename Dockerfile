# Estágio de construção
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Executa o build e lista o conteúdo do diretório target para depuração
RUN mvn clean package -DskipTests && ls -l /app/target/

# Estágio final
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copia o JAR gerado com o nome correto
COPY --from=build /app/target/Desafio-Livraria-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]