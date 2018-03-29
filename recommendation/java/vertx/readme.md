Recommendation
==============

This is the recommendation microservice, part of the Istio Tutorial demo. Even though this microservice is meant to be executed within a Container on a Pod on Kubernetes/OpenShift, it can still be executed on bare metal.

This is a Vert.x application, composed of a single Verticle and a few handlers. It can be told to misbehave.

Running on the local machine
============================

To run this service for development purposes on your own machine, execute:

```bash
mvn vertx:run
```

If you already have a service running on port `8080`, you can tell which port to use by setting the environment variable
`LISTEN_ON`

```bash
LISTEN_ON=xxxx mvn vertx:run
```

To test, call http://localhost:8080/

```
$ curl http://localhost:8080/
recommendation v1 from 'caju': 3
```

A successful output will look like: `recommendation v1 from 'YOUR_HOSTNAME': 1`

To tell the service to misbehave, call the `/misbehave` endpoint:

```
curl http://localhost:8080/misbehave
```

Subsequent calls will respond with an HTTP Status Code 503. To get the service to return 200 again, run:

```
curl http://localhost:8080/behave
```

Running on OpenShift
====================

The following commands will build a Docker image containing the application, create a Kubernetes `Deployment` and a corresponding `Service`, so that other services can discover the pods via the service name.

```bash
mvn clean package
docker build -t example/recommendation:v1 .
docker images | grep recommendation
oc apply -f ../../kubernetes/Deployment.yml
oc apply -f ../../kubernetes/Service.yml
oc expose service recommendation
```

The last command will expose the service to the outside world, allowing you to make an HTTP call directly from your host machine:

```
curl http://recommendation-tutorial.127.0.0.1.nip.io/
```
