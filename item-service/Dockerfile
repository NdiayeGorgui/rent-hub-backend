FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar item-service.jar
ENTRYPOINT ["java", "-jar", "item-service.jar"]
EXPOSE 8082
