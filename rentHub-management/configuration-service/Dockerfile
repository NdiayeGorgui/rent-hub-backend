FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar configuration-service.jar
ENTRYPOINT ["java", "-jar", "configuration-service.jar"]
EXPOSE 8888
