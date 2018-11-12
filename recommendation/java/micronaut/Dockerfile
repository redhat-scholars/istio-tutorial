FROM oracle/graalvm-ce:1.0.0-rc7 as builder

COPY . .

RUN ./mvnw install:install-file -Dfile=/usr/java/latest/jre/lib/svm/builder/svm.jar -DgroupId=com.oracle.substratevm -DartifactId=svm -Dversion=GraalVM-1.0.0-rc7 -Dpackaging=jar
RUN ./mvnw clean package

RUN java -cp target/recommendation-0.1.jar io.micronaut.graal.reflect.GraalClassLoadingAnalyzer
RUN native-image --no-server \
             --class-path target/recommendation-0.1.jar \
       -H:ReflectionConfigurationFiles=target/reflect.json \
       -H:EnableURLProtocols=http \
       -H:IncludeResources="logback.xml|application.yml|META-INF/services/*.*" \
       -H:Name=recommendation \
       -H:Class=com.redhat.developer.demos.recommendation.Application \
       -H:+ReportUnsupportedElementsAtRuntime \
       -H:+AllowVMInspection \
       --rerun-class-initialization-at-runtime='sun.security.jca.JCAUtil$CachedSecureRandomHolder,javax.net.ssl.SSLContext' \
       --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.ssl.util.ThreadLocalInsecureRandom


FROM oracle/graalvm-ce:1.0.0-rc7

RUN groupadd -r recommendation && adduser -r -g recommendation recommendation
USER recommendation

COPY --from=builder ./recommendation .

EXPOSE 8080
ENTRYPOINT ["./recommendation"]
