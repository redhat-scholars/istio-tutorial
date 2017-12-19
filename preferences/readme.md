
Assumes minishift, tested with minshift v1.10.0+10461c6

minishift creation script

~~~~
#!/bin/bash

export PATH=/Users/burr/minishift_1.10.0/:$PATH

minishift profile set istio2-demo
minishift config set memory 8GB
minishift config set cpus 3
minishift config set vm-driver virtualbox
minishift addon enable admin-user
minishift config set openshift-version v3.7.0

MINISHIFT_ENABLE_EXPERIMENTAL=y minishift start --metrics
~~~~

Istio installation script

~~~~
#!/bin/bash

curl -LO https://github.com/istio/istio/releases/download/0.4.0/istio-0.4.0-osx.tar.gz

gunzip istio-0.4.0-osx.tar.gz 

tar -xvzf istio-0.4.0-osx.tar

cd istio-0.4.0

oc login $(minishift ip):8443 -u admin -p admin

oc adm policy add-scc-to-user anyuid -z istio-ingress-service-account -n istio-system

oc adm policy add-scc-to-user anyuid -z istio-egress-service-account -n istio-system

oc adm policy add-scc-to-user anyuid -z default -n istio-system

oc create -f install/kubernetes/istio.yaml

oc project istio-system 

oc expose svc istio-ingress 

oc apply -f install/kubernetes/addons/prometheus.yaml

oc apply -f install/kubernetes/addons/grafana.yaml

oc apply -f install/kubernetes/addons/servicegraph.yaml

oc expose svc servicegraph

oc expose svc grafana

oc process -f https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/all-in-one/jaeger-all-in-one-template.yml | oc create -f -

oc get pods -w

~~~~


1. start.spring.io and select the following:
```
web
actuator
devtools
```

2. Add Controller.java 

3. mvn spring-boot:run and test it localhost:8080

You need to override the default live & ready probes otherwise you get: [customer-2691584122-cs8rz istio-proxy] [2017-11-17 00:35:15.001][12][warning][upstream] external/envoy/source/server/lds_subscription.cc:65] lds: fetch failure: error adding listener: 'http_172.17.0.20_8080' has duplicate address '172.17.0.20:8080' as existing listener

https://github.com/istio/istio/issues/1194

should look like

        livenessProbe:
          exec:
            command: 
            - curl
            - localhost:8080/health

4. eval $(minishift oc-env)

5. eval $(minishift docker-env)

6. oc login

6. oc new-project springistio

7. oc adm policy add-scc-to-user privileged -z default -n springistio



10. mvn package fabric8:build -Dfabric8.mode=kubernetes

11. docker images | grep preferences

12. Add istioctl to your PATH

13. oc apply -f <(istioctl kube-inject -f target/classes/META-INF/fabric8/kubernetes/preferences-deployment.yml) -n springistio

oc get pods -w (wait to see 2/2 Ready)

14. oc create -f target/classes/META-INF/fabric8/kubernetes/preferences-svc.yml

Note: no route for preferences, it is internally consumed

15. Check out your Grafana, Jaeger and Service Graph dashboards

Tips:

* To view logs when there is a sidecar

oc logs customer-3857234246-qtczv -c spring-boot


