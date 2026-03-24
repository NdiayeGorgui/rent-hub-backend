FROM openjdk:17-oracle
VOLUME /tmp
COPY target/*.jar eureka-registry.jar
ENTRYPOINT ["java", "-jar", "eureka-registry.jar"]
EXPOSE 8761
