FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the fat JAR file
COPY build/libs/DenBot-1.0-all.jar /app/DenBot-1.0-all.jar

# Copy config directory if needed
COPY config /app/config
COPY .env /app/.env

EXPOSE 8080

CMD ["java", "-jar", "/app/DenBot-1.0-all.jar"]
