# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw mvnw
COPY pom.xml .

RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/coffee_machine-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "coffee_machine-0.0.1-SNAPSHOT.jar"]