FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar subscription-service.jar
ENTRYPOINT ["java", "-jar", "subscription-service.jar"]
EXPOSE 8089
