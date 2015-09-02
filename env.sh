#!/bin/bash

if [ -z "${APP_NAME}" ]; then
	export APP_NAME="AD-Capital";
fi

if [ -z "${CONTROLLER}" ]; then
	export CONTROLLER="platform";
fi

if [ -z "${APPD_PORT}" ]; then
	export APPD_PORT=8090;
fi

if [ -z "${EVENT_ENDPOINT}" ]; then
        export EVENT_ENDPOINT="platform:9080";
fi

if [ -z "${ACCOUNT_NAME}" ]; then
	export ACCOUNT_NAME="customer1";
fi

if [ -z "${ACCOUNT_ACCESS_KEY}" ]; then
	export ACCOUNT_ACCESS_KEY="your-account-access-key";
fi

export JAVA_OPTS="-Xmx512m -XX:MaxPermSize=128m"
export APPD_JAVA_OPTS="${JAVA_OPTS} -Dappdynamics.controller.hostName=${CONTROLLER} -Dappdynamics.controller.port=${APPD_PORT} -Dappdynamics.agent.applicationName=${APP_NAME} -Dappdynamics.agent.tierName=${TIER_NAME} -Dappdynamics.agent.nodeName=${NODE_NAME}";
export MACHINE_AGENT_JAVA_OPTS="-Dappdynamics.sim.enabled=true ${JAVA_OPTS} ${APPD_JAVA_OPTS}"
export APP_AGENT_JAVA_OPTS="${JAVA_OPTS} ${APPD_JAVA_OPTS} -DjvmRoute=${JVM_ROUTE} -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Dappdynamics.agent.accountName=${ACCOUNT_NAME%%_*} -Dappdynamics.agent.accountAccessKey=${ACCOUNT_ACCESS_KEY}";
export JMX_OPTS="-Dcom.sun.management.jmxremote.port=8888  -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
