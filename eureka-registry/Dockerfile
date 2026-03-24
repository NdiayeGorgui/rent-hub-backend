FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/*.jar eureka-registry.jar
ENTRYPOINT ["java", "-jar", "eureka-registry.jar"]
EXPOSE 8761
