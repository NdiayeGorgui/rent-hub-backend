FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar api-service.jar
ENTRYPOINT ["java", "-jar", "api-service.jar"]
EXPOSE 8080
