FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar item-service.jar
ENTRYPOINT ["java", "-jar", "item-service.jar"]
EXPOSE 8082
