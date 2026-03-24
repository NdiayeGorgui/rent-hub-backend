FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar api-service.jar
ENTRYPOINT ["java", "-jar", "api-service.jar"]
EXPOSE 8080
