FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar api-gateway.jar
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
EXPOSE 8080
