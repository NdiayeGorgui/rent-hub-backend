FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar rental-service.jar
ENTRYPOINT ["java", "-jar", "rental-service.jar"]
EXPOSE 8083
