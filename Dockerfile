FROM openjdk:17-jdk

ARG PINPOINT_VERSION
ARG AGENT_ID
ARG APP_NAME
ENV JAVA_OPTS="-javaagent:/pinpoint-agent/pinpoint-bootstrap-${PINPOINT_VERSION}.jar -Dpinpoint.agentId=${AGENT_ID} -Dpinpoint.applicationName=${APP_NAME} -Dspring.profiles.active=${SPRING_PROFILES}"
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

CMD java ${JAVA_OPTS} app.jar
