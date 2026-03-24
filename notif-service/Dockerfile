FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar notif-service.jar
ENTRYPOINT ["java", "-jar", "notif-service.jar"]
EXPOSE 8085
