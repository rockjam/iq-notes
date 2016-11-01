FROM openjdk:8u92-jre-alpine
MAINTAINER Nikolay Tatarinov
RUN apk --update add bash
ADD target/docker/stage/var /var
ENTRYPOINT bin/iqnotes
WORKDIR /var/lib/iqnotes
EXPOSE 3000
