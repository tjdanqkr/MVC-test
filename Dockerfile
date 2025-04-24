FROM openjdk:21-slim
LABEL authors="qkrtjdan"
COPY build/libs/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
