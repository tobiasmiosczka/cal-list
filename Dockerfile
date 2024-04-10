FROM eclipse-temurin:21-alpine
MAINTAINER tobiasmiosczka.github.com
RUN mkdir /opt/app
COPY ./target/cal-list-1.0.0.jar /opt/app
CMD ["sh", "-c", "java -jar \"/opt/app/cal-list-1.0.0-jar-with-dependencies.jar\""]