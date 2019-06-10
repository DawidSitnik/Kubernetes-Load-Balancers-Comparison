# cdp1-kube-replica-server
FROM openjdk:8u181-jre-stretch
LABEL maintainer="CDP1 20182 Team 11"

COPY ./target/kube-test-server-0.0.1-SNAPSHOT.jar /app/bin/
WORKDIR /app/bin/

ENTRYPOINT [ "java", "-jar", "kube-test-server-0.0.1-SNAPSHOT.jar" ]

EXPOSE 6011
