# Stage 1: Download the dependencies
FROM maven:3.9-eclipse-temurin-17 AS dependencies
#Run apk add --no-cache
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

# Stage 2: Build the application
FROM dependencies AS builder
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 3: Run the aaplication
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080


# java -jar app.jar for
ENTRYPOINT ["java","-jar","app.jar"]


