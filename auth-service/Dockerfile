FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
EXPOSE 8081
