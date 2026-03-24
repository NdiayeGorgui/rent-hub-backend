FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar dispute-service.jar
ENTRYPOINT ["java", "-jar", "dispute-service.jar"]
EXPOSE 8086
