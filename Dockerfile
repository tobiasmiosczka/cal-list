FROM eclipse-temurin:21-alpine
MAINTAINER tobiasmiosczka.github.com
COPY target/cal-list-1.0.0.jar cal-list-1.0.0.jar
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]