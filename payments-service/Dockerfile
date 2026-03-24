FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar payments-service.jar
ENTRYPOINT ["java", "-jar", "payments-service.jar"]
EXPOSE 8087
