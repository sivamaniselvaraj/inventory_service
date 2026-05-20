FROM openjdk:27-ea-slim

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]
