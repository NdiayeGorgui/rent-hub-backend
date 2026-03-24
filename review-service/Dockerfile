FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar review-service.jar
ENTRYPOINT ["java", "-jar", "review-service.jar"]
EXPOSE 8084
