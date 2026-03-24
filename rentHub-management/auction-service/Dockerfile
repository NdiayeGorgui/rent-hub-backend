FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar auction-service.jar
ENTRYPOINT ["java", "-jar", "auction-service.jar"]
EXPOSE 8088
