FROM fabric8/java-jboss-openjdk8-jdk:1.5.4
ENV JAVA_OPTIONS=-Dquarkus.http.host=0.0.0.0
EXPOSE 8080 8778 9779
COPY target/lib/* /deployments/lib/
COPY target/*-runner.jar /deployments/app.jar
ENTRYPOINT [ "/deployments/run-java.sh" ]