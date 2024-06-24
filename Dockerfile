FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build.gradle.kts /app/
COPY gradlew /app/
COPY gradle /app/gradle

RUN chmod +x /app/gradlew

COPY src /app/src
COPY src /app/config

RUN ./gradlew build

COPY build/libs/GompeisHelper-1.0-all.jar /app/GompeisHelper-1.0-all.jar

CMD ["java", "-cp", "/app/GompeisHelper-1.0-all.jar", "com.greenbueller.GompeiHelper.GompeiHelper"]