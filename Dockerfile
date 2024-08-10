FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the fat JAR file
COPY build/libs/GompeisHelper-1.0-all.jar /app/GompeisHelper-1.0-all.jar

# Copy config directory if needed
COPY config /app/config
COPY .env /app/.env

CMD ["java", "-jar", "/app/GompeisHelper-1.0-all.jar"]
