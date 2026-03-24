FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar dispute-service.jar
ENTRYPOINT ["java", "-jar", "dispute-service.jar"]
EXPOSE 8086
