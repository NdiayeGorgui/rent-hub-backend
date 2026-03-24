FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar configuration-service.jar
ENTRYPOINT ["java", "-jar", "configuration-service.jar"]
EXPOSE 8888
