# Written against Docker v1.8.0
FROM java:8
MAINTAINER Chris Rebert <code@chrisrebert.com>

WORKDIR /
USER daemon

ADD target/scala-2.11/no-carrier-assembly.jar /app/no-carrier.jar

ENTRYPOINT ["java", "-jar", "/app/no-carrier.jar"]
