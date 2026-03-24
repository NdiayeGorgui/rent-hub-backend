FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar payments-service.jar
ENTRYPOINT ["java", "-jar", "payments-service.jar"]
EXPOSE 8087
