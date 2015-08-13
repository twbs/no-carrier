# Written against Docker v1.8.0
FROM java:8
MAINTAINER Chris Rebert <code@chrisrebert.com>

WORKDIR /
USER daemon

RUN ["mkdir", "/app"]
ADD target/scala-2.11/no-carrier-assembly-1.0.jar /app/no-carrier.jar

ENTRYPOINT ["java", "-jar", "/app/no-carrier.jar"]
