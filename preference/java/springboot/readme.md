Preference
==========

This is the preference microservice, part of the Istio Tutorial demo. Even though this microservice is meant to be executed within a Container on a Pod on Kubernetes/OpenShift, it can still be executed on bare metal.

This is a regular Spring Boot application, with OpenTracing and Jaeger dependencies to provide distributed tracing capabilities.

Running on the local machine
============================

To run this service for development purposes on your own machine, execute:

```bash
JAEGER_SERVICE_NAME=preference mvn \
  spring-boot:run \
  -Drun.arguments="--spring.config.location=src/main/resources/application-local.properties"
```

The `application-local.properties` contains sensible values for a setup where all services are running locally. It assumes that the `recommendation` service is running on the port `8080`. If that's not the case, adjust the property file accordingly.

Jaeger is not required to run this example, but if you prefer to have a local Jaeger instance running to see the traces, the easiest is to start via a Docker container:

```bash
docker run \
  --rm \
  -p5775:5775/udp \
  -p6831:6831/udp \
  -p6832:6832/udp \
  -p16686:16686 \
  -p14268:14268 \
  jaegertracing/all-in-one:1.3
```

The default configuration for the Jaeger tracer samples only a small portion of the requests. To trace every incoming request and report the spans to the log file, export the following environment variables and start the application again:

```bash
export JAEGER_REPORTER_LOG_SPANS=true
export JAEGER_SAMPLER_TYPE=const
export JAEGER_SAMPLER_PARAM=1
```

To test, call http://localhost:8180/

```
$ curl http://localhost:8180/
preference => recommendation v1 from 'caju': 3
```

And should generate a trace like this:

![Trace View](trace.png)

Running on OpenShift
====================

The following commands will build a Docker image containing the application, create a Kubernetes `Deployment` and a corresponding `Service`, so that other services can discover the pods via the service name.

```bash
mvn clean package
docker build -t example/preference .
docker images | grep preference
oc apply -f ../../kubernetes/Deployment.yml
oc apply -f ../../kubernetes/Service.yml
oc expose service preference
```

The last command will expose the service to the outside world, allowing you to make an HTTP call directly from your host machine:

```
curl http://preference-tutorial.127.0.0.1.nip.io/
```
