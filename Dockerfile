FROM adoptopenjdk:17-jre-hotspot

WORKDIR /app

COPY pom.xml .

EXPOSE 8080
EXPOSE 5005

#ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n"

CMD ["mvn", "spring-boot:run"]