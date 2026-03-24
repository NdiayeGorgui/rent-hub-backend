FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar rental-service.jar
ENTRYPOINT ["java", "-jar", "rental-service.jar"]
EXPOSE 8083
