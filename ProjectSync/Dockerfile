FROM openjdk:17-jdk-slim

ARG PROFILE
ARG ADDITIONAL_OPTS

ENV PROFILE=${PROFILE}
ENV ADDITIONAL_OPTS=${ADDITIONAL_OPTS}

WORKDIR /opt/projectsync

COPY /target/ProjectSync*.jar ProjectSync.jar

SHELL ["/bin/sh", "-c"]

EXPOSE 5005
EXPOSE 8082

CMD java ${ADDITIONAL_OPTS} -jar ProjectSync.jar --spring.profiles.active=${PROFILE}