FROM oracle/graalvm-ce:1.0.0-rc7 as builder

COPY . .

RUN ./mvnw install:install-file -Dfile=/usr/java/latest/jre/lib/svm/builder/svm.jar -DgroupId=com.oracle.substratevm -DartifactId=svm -Dversion=GraalVM-1.0.0-rc7 -Dpackaging=jar
RUN ./mvnw clean package

RUN java -cp target/customer-0.1.jar io.micronaut.graal.reflect.GraalClassLoadingAnalyzer
RUN native-image --no-server \
             --class-path target/customer-0.1.jar \
       -H:ReflectionConfigurationFiles=target/reflect.json \
       -H:EnableURLProtocols=http \
       -H:IncludeResources="logback.xml|application.yml|META-INF/services/*.*" \
       -H:Name=customer \
       -H:Class=com.redhat.developer.demos.customer.Application \
       -H:+ReportUnsupportedElementsAtRuntime \
       -H:+AllowVMInspection \
       --rerun-class-initialization-at-runtime='sun.security.jca.JCAUtil$CachedSecureRandomHolder,javax.net.ssl.SSLContext' \
       --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.ssl.util.ThreadLocalInsecureRandom


FROM oracle/graalvm-ce:1.0.0-rc7

RUN groupadd -r customer && adduser -r -g customer customer
USER customer

ENV JAEGER_SERVICE_NAME=customer\
  JAEGER_ENDPOINT=http://jaeger-collector.istio-system.svc:14268/api/traces\
  JAEGER_PROPAGATION=b3\
  JAEGER_SAMPLER_TYPE=const\
  JAEGER_SAMPLER_PARAM=1

COPY --from=builder ./customer .

EXPOSE 8080
ENTRYPOINT ["./customer"]
