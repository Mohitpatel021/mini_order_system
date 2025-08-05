FROM maven:3.9.4-eclipse-temurin-21 AS MAVEN_BUILD
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jre AS RUN_ENV
WORKDIR /app
COPY --from=MAVEN_BUILD /app/target/*.jar /app/app.jar
CMD ["java", "-Xms256m", "-Xmx512m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]