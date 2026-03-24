FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar review-service.jar
ENTRYPOINT ["java", "-jar", "review-service.jar"]
EXPOSE 8084
