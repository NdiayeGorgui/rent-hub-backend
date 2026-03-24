FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar auction-service.jar
ENTRYPOINT ["java", "-jar", "auction-service.jar"]
EXPOSE 8088
