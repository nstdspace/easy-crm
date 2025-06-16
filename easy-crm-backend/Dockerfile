FROM eclipse-temurin:17-jre-focal
COPY build/libs/EasyCrmBackend.jar EasyCrmBackend.jar
ENTRYPOINT ["java",  "-jar", "-Dspring.profiles.active=production", "/EasyCrmBackend.jar"]
EXPOSE 8080
