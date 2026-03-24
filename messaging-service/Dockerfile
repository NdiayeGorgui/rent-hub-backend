FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar messaging-service.jar
ENTRYPOINT ["java", "-jar", "messaging-service.jar"]
EXPOSE 8090
