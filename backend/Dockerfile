# Etapa de build
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar apenas o pom.xml e baixar dependências separadamente (cache melhor)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora sim, copiar o restante do código
COPY src ./src

# Fazer o build do projeto (sem testes)
RUN mvn clean package -DskipTests -B

# Etapa de execução
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar o JAR gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080 (padrão do Spring Boot)
EXPOSE 8080

# Comando de inicialização com configurações de JVM otimizadas
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]