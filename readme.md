# Java (Spring Boot) + Istio on Kubernetes/OpenShift

Istio capabilities explored:
* Jaeger tracing
* Prometheus+Grafana monitoring
* Smart routing between Services (Canary Deployments) including user-agent based routing


There are three different and super simple microservices in this system and they are chained together in the following sequence:

customer -> preferences -> recommendations

For now, they have a simple exception handling solution for dealing with 
a missing dependent service, it just returns the error message to the end-user.

## Setup minishift
Assumes minishift, tested with minshift v1.10.0+10461c6

My creation script
```bash
#!/bin/bash

# add the location of minishift execuatable to PATH
# I also keep other handy tools like kubectl and kubetail.sh 
# in that directory

export PATH=/Users/burr/minishift_1.10.0/:$PATH

minishift profile set istio2-demo
minishift config set memory 8GB
minishift config set cpus 3
minishift config set vm-driver virtualbox
minishift addon enable admin-user
minishift config set openshift-version v3.7.0

MINISHIFT_ENABLE_EXPERIMENTAL=y minishift start --metrics
```
## Setup environment

```
eval $(minishift oc-env)
eval $(minishift docker-env)
```

## Istio installation script

```bash
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

```
## Deploy Customer

Make sure you have are logged in
```
oc status
oc whoami
```
and you have setup the project/namespace
```
oc new-project springistio
```

```
cd customer
mvn clean package
docker build -t example/customer .
docker images | grep customer
```

Currently using the "manual" way of injecting the Envoy sidecar
Add istioctl to your $PATH

```
istioctl version
oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n springistio
oc create -f src/main/kubernetes/Service.yml
oc expose service customer
oc get route
curl customer-springistio.$(minishift ip).nip.io
cd ..
```

## Deploy preferences
```
cd preferences
mvn clean package
docker build -t example/preferences .
docker images | grep preferences
oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n springistio
oc create -f src/main/kubernetes/Service.yml
curl customer-springistio.$(minishift ip).nip.io
cd ..
```

## Deploy recommendations
```
cd recommendations
mvn clean package
docker build -t example/recommendations .
docker images | grep recommendations
oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n springistio
oc create -f src/main/kubernetes/Service.yml
curl customer-springistio.$(minishift ip).nip.io
cd ..
```

## recommendations:v2 
so we can experiment with Istio routing rules by making a change to RecommendationsController.java like

return "Clifford v2";

```
cd recommendations

mvn clean compile package

docker build -t example/recommendations:v2 .

docker images | grep recommendations

cd ..

oc apply -f <(istioctl kube-inject -f zotherfiles/recommendations_v2_deployment.yml) -n springistio

oc get pods -w

curl customer-springistio.$(minishift ip).nip.io
```

you likely see "Clifford v1"

```
curl customer-springistio.$(minishift ip).nip.io
```

you likely see "Clifford v2" as by default you get random oad-balancing when there is more than one Pod behind a Service


```
oc project springistio 
```

(so it does not have to be repeated below)

## Istio Route Rules

#### All users to recommendations:v2
```
oc create -f routerulefiles/route-rule-recommendations-v2.yml 

curl customer-springistio.$(minishift ip).nip.io
```

you should only see v2 being returned

#### All users to recommendations:v1
```
oc replace -f routerulefiles/route-rule-recommendations-v1.yml 

oc get routerules

oc get routerules/recommendations-default -o yaml 
```
#### All users to recommendations v1 and v2
```
oc delete routerules/recommendations-default
```

### Smart routing based on user-agent header

What is your user-agent?

https://www.whoishostingthis.com/tools/user-agent/

Note: the "user-agent" header being forward in the Customer and Preferences controllers in order for route rule modications around recommendations 

#### Set recommendations to all v1
```
oc create -f routerulefiles/route-rule-recommendations-v1.yml 
```
#### Set Safari users to v2
```
oc create -f routerulefiles/route-rule-safari-recommendations-v2.yml 

oc get routerules
```

and test with a Safari (or even Chrome on Mac since it includes Safari in the string).  Safari only sees v2 responses from recommendations

and test with a Firefox browser, it should only see v1 responses from recommendations

```
oc describe routerule recommendations-safari
```

Remove the Safari rule

```
oc delete routerule recommendations-safari
```
#### Set mobile users to v2
```
oc create -f routerulefiles/route-rule-mobile-recommendations-v2.yml

curl -A "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4(KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5" http://customer-springistio.192.168.99.102.nip.io/
```

Clean up
```
oc delete routerule recommendations-safari

oc delete routerule recommendations-default
```