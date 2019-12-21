FROM fabric8/java-jboss-openjdk8-jdk:1.5.4
ENV JAVA_OPTIONS=-Dquarkus.http.host=0.0.0.0
ENV JAEGER_SERVICE_NAME=recommendation \
  JAEGER_ENDPOINT=http://jaeger-collector.istio-system.svc:14268/api/traces \
  JAEGER_PROPAGATION=b3 \
  JAEGER_SAMPLER_TYPE=const \
  JAEGER_SAMPLER_PARAM=1
EXPOSE 8080 8778 9779
COPY target/lib/* /deployments/lib/
COPY target/*-runner.jar /deployments/app.jar
ENTRYPOINT [ "/deployments/run-java.sh" ]