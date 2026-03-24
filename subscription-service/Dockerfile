FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar subscription-service.jar
ENTRYPOINT ["java", "-jar", "subscription-service.jar"]
EXPOSE 8089
