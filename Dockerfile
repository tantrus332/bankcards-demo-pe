# Build stage
FROM maven:3.9.12-amazoncorretto-17-alpine AS build
WORKDIR /build

# Кэшируем слои с зависимостями отдельно
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код и выполняем сборку (тесты пропускаем, т.к. они проверяются в CI)
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage - используем минимальный образ eclipse-temurin:17-jre-alpine
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk upgrade --no-cache

# Создаем непривилегированного пользователя для безопасности (Best Practice)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Копируем только готовый артефакт из предыдущего этапа
COPY --from=build /build/target/*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
