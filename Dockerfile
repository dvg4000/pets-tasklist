FROM openjdk:17-jdk-slim
COPY target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]