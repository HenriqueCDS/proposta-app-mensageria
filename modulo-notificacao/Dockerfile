# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Camada de dependências (só é refeita quando o pom.xml muda)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests clean package

# ---------- Runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Roda como usuário sem privilégios
RUN groupadd --system app && useradd --system --gid app --no-create-home app

COPY --from=build --chown=app:app /build/target/*.jar app.jar

USER app

# Segredos e configuração são injetados por variáveis de ambiente (.env / docker-compose)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
