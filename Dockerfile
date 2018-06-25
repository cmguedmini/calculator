FROM openjdk:8-jre-alpine
MAINTAINER Chawki Mguedmini <c.mguedmini@roam-smart.com>

#install Spring Boot artifact
VOLUME /tmp
ADD target/*.jar app.jar

EXPOSE 9999

RUN sh -c 'touch /app.jar'
CMD java -Djava.security.egd=file:/dev/./urandom -jar /app.jar