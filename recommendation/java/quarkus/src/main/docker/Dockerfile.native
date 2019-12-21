FROM registry.access.redhat.com/ubi8/ubi-minimal
ENV JAEGER_SERVICE_NAME=recommendation\
  JAEGER_ENDPOINT=http://jaeger-collector.istio-system.svc:14268/api/traces\
  JAEGER_PROPAGATION=b3\
  JAEGER_SAMPLER_TYPE=const\
  JAEGER_SAMPLER_PARAM=1
WORKDIR /work/
COPY target/*-runner /work/recommendation
RUN chmod 775 /work
EXPOSE 8080 8778 9779
CMD ["./recommendation", "-Dquarkus.http.host=0.0.0.0", "-Xmx8m", "-Xmn8m", "-Xms8m"]