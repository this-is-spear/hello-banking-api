FROM openjdk:17-jdk

ARG PINPOINT_VERSION
ARG AGENT_ID
ARG APP_NAME
ENV JAVA_PINPOINT_OPTS="-javaagent:/pinpoint-agent/pinpoint-bootstrap-${PINPOINT_VERSION}.jar -Dpinpoint.agentId=${AGENT_ID} -Dpinpoint.applicationName=${APP_NAME}"
ENV JAVA_OPTS="${JAVA_PINPOINT_OPTS} -Duser.timezone=Asia/Seoul -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
COPY ./build/libs/*SNAPSHOT.jar app.jar

CMD echo 'sleep for initialze hbase' && sleep 30 && java -jar ${JAVA_OPTS} app.jar
