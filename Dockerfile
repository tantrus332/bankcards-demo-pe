# Build
FROM maven:3.9.12-amazoncorretto-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
