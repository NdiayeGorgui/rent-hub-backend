FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar notif-service.jar
ENTRYPOINT ["java", "-jar", "notif-service.jar"]
EXPOSE 8085
