FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
EXPOSE 8081
