FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar messaging-service.jar
ENTRYPOINT ["java", "-jar", "messaging-service.jar"]
EXPOSE 8090
