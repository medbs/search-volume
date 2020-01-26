FROM openjdk:8u111-jdk-alpine
WORKDIR /
COPY /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8081
