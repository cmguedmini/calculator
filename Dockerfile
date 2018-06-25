FROM maven:3-alpine
MAINTAINER Chawki Mguedmini <c.mguedmini@roam-smart.com>

COPY pom.xml pipeline/

COPY src/ pipeline/src/

WORKDIR pipeline/

RUN mvn clean install

EXPOSE 8090

ENTRYPOINT [ "java", "-jar", "/pipeline/target/jenkins-pipeline.jar"]