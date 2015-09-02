FROM centos:centos6

RUN yum -y install unzip
RUN yum -y install tar
RUN yum -y install ntp

# Install Oracle JDK
ADD jdk-7u79-linux-x64.rpm /
RUN rpm -Uvh /jdk-7u79-linux-x64.rpm
RUN rm /jdk-7u79-linux-x64.rpm

ENV JAVA_HOME /usr/java/default
ENV PATH $PATH:$JAVA_HOME/bin

# Install Gradle
RUN curl -O http://downloads.gradle.org/distributions/gradle-2.1-bin.zip
RUN unzip gradle-2.1-bin.zip -d /opt/
RUN rm gradle-2.1-bin.zip
ENV GRADLE_HOME /opt/gradle-2.1
ENV PATH $PATH:$GRADLE_HOME/bin

# Environment vars: Tomcat
ENV TOMCAT_MAJOR_VERSION 8
ENV TOMCAT_MINOR_VERSION 8.0.14
ENV CATALINA_HOME /tomcat

# Install Tomcat
RUN curl -O -k https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz \
    && curl -O -k https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz.md5 \
    && md5sum apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz.md5 \
    && tar zxf apache-tomcat-*.tar.gz \
    && rm apache-tomcat-*.tar.gz* \
    && mv apache-tomcat-${TOMCAT_MINOR_VERSION} tomcat
RUN cd ${CATALINA_HOME}/bin;chmod +x *.sh

ADD AppServerAgent.zip /
RUN unzip -q /AppServerAgent.zip -d ${CATALINA_HOME}/appagent; rm AppServerAgent.zip

ENV ANALYTICS_AGENT_HOME /analytics-agent
ADD AnalyticsAgent.zip /
RUN unzip AnalyticsAgent.zip
RUN rm -f AnalyticsAgent.zip
ADD start-analytics.sh /usr/bin/start-analytics
RUN chmod 744 /usr/bin/start-analytics

ENV PROJECT_HOME /adcapital
RUN mkdir -p ${PROJECT_HOME}
ADD build.gradle ${PROJECT_HOME}/
ADD settings.gradle ${PROJECT_HOME}/
ADD schema.sql ${PROJECT_HOME}/
ADD database.properties ${PROJECT_HOME}/
ADD Models ${PROJECT_HOME}/Models
ADD Portal ${PROJECT_HOME}/Portal
ADD Processor ${PROJECT_HOME}/Processor
ADD QueueReader ${PROJECT_HOME}/QueueReader
ADD Rest ${PROJECT_HOME}/Rest
ADD Verification ${PROJECT_HOME}/Verification

RUN cd ${PROJECT_HOME}; gradle build
RUN cp ${PROJECT_HOME}/Rest/build/libs/Rest.war ${CATALINA_HOME}/webapps/Rest.war
RUN cp ${PROJECT_HOME}/Verification/build/libs/Verification.war ${CATALINA_HOME}/webapps/Verification.war 
RUN cp ${PROJECT_HOME}/QueueReader/build/libs/QueueReader.war ${CATALINA_HOME}/webapps/QueueReader.war
RUN cp ${PROJECT_HOME}/Processor/build/libs/processor.war ${CATALINA_HOME}/webapps/processor.war
RUN cp ${PROJECT_HOME}/Portal/build/libs/portal.war ${CATALINA_HOME}/webapps/portal.war

ADD startup.sh /
ADD env.sh /
RUN chmod 744 /startup.sh

# Note: This command should not return or the container will exit
CMD "/startup.sh"
