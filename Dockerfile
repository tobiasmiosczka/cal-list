FROM eclipse-temurin:21-alpine
MAINTAINER tobiasmiosczka.github.com
COPY target/cal-list-1.0.0.jar cal-list-1.0.0.jar
HEALTHCHECK CMD curl --fail http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/cal-list-1.0.0.jar"]