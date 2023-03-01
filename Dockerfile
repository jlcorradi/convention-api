
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY ./target/convention-api.jar convention-api.jar

EXPOSE 8080 5005

CMD ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n", "convention-api.jar"]
