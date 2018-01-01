# Java (Spring Boot) + Istio on Kubernetes/OpenShift

There are three different and super simple microservices in this system and they are chained together in the following sequence:

customer -> preferences -> recommendations

For now, they have a simple exception handling solution for dealing with 
a missing dependent service, it just returns the error message to the end-user.

**Table of Contents**

<!-- toc -->

* [Prerequisite CLI tools](#prerequisite-cli-tools)
* [Setup minishift](#setup-minishift)
* [Setup environment](#setup-environment)
* [Istio installation script](#istio-installation-script)
* [Deploy customer](#deploy-customer)
* [Deploy preferences](#deploy-preferences)
* [Deploy recommendations](#deploy-recommendations)
* [Updating & redeploying code](#updating-redeploying-code)
* [Tracing](#tracing)
* [Monitoring](#monitoring)
* [Istio RouteRule Changes](#istio-routerule-changes)
  * [recommendations:v2](#recommendationsv2)
* [Changing Istio RouteRules](#changing-istio-routerules)
    * [All users to recommendations:v2](#all-users-to-recommendationsv2)
    * [All users to recommendations:v1](#all-users-to-recommendationsv1)
    * [All users to recommendations v1 and v2](#all-users-to-recommendations-v1-and-v2)
    * [Split traffic between v1 and v2](#split-traffic-between-v1-and-v2)
* [Fault Injection](#fault-injection)
  * [HTTP Error 503](#http-error-503)
  * [Delay](#delay)
* [Retry](#retry)
* [Timeout](#timeout)
* [Smart routing based on user-agent header (Canary Deployment)](#smart-routing-based-on-user-agent-header-canary-deployment)
    * [Set recommendations to all v1](#set-recommendations-to-all-v1)
    * [Set Safari users to v2](#set-safari-users-to-v2)
    * [Set mobile users to v2](#set-mobile-users-to-v2)
    * [Clean up](#clean-up)
* [Mirroring Traffic (Dark Launch)](#mirroring-traffic-dark-launch)
* [Access Control](#access-control)
    * [Whitelist](#whitelist)
    * [Blacklist](#blacklist)
* [Load Balancer](#load-balancer)
* [Circuit Breaker](#circuit-breaker)
    * [Fail Fast with Max Connections & Max Pending Requests](#fail-fast-with-max-connections-max-pending-requests)
    * [Pool ejection](#pool-ejection)
* [Rate Limiting](#rate-limiting)
* [Tips & Tricks](#tips-tricks)

<!-- toc stop -->


## Prerequisite CLI tools 
You will need in this tutorial
* minishift (https://github.com/minishift/minishift/releases)
* docker (https://www.docker.com/docker-mac)
* kubectl (https://kubernetes.io/docs/tasks/tools/install-kubectl/#install-kubectl-binary-via-curl)
* oc (eval $(minishift oc-env))
* mvn (https://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz)
* stern (brew install stern)
* istioctl (will be installed via the steps below)
* curl, gunzip, tar are built-in to MacOS or part of your bash shell
* git (everybody needs the git CLI)

## Setup minishift
Assumes minishift, tested with minshift v1.10.0+10461c6

Minishift creation script
```bash
#!/bin/bash

# add the location of minishift execuatable to PATH
# I also keep other handy tools like kubectl and kubetail.sh 
# in that directory

export PATH=/Users/burr/minishift_1.10.0/:$PATH

minishift profile set istio-work
minishift config set memory 8GB
minishift config set cpus 3
minishift config set vm-driver virtualbox
minishift config set image-caching true
minishift addon enable admin-user
minishift config set openshift-version v3.7.0

MINISHIFT_ENABLE_EXPERIMENTAL=y minishift start --metrics

```
## Setup environment

```bash
eval $(minishift oc-env)
eval $(minishift docker-env)
oc login $(minishift ip):8443 -u admin -p admin
```

## Istio installation script

```bash
#!/bin/bash

curl -LO https://github.com/istio/istio/releases/download/0.4.0/istio-0.4.0-osx.tar.gz

gunzip istio-0.4.0-osx.tar.gz 

tar -xvzf istio-0.4.0-osx.tar

cd istio-0.4.0

# make sure we are logged in
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

oc expose svc prometheus

oc process -f https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/all-in-one/jaeger-all-in-one-template.yml | oc create -f -

```
Wait for Istio's components to be ready

```
oc get pods
NAME                             READY     STATUS    RESTARTS   AGE
grafana-3617079618-4qs2b         1/1       Running   0          4m
istio-ca-1363003450-tfnjp        1/1       Running   0          4m
istio-ingress-1005666339-vrjln   1/1       Running   0          4m
istio-mixer-465004155-zn78n      3/3       Running   0          5m
istio-pilot-1861292947-25hnm     2/2       Running   0          4m
jaeger-210917857-2w24f           1/1       Running   0          4m
prometheus-168775884-dr5dm       1/1       Running   0          4m
servicegraph-1100735962-tdh78    1/1       Running   0          4m
```

And if you need quick access to the OpenShift console

```
minishift console
```

## Deploy customer

Make sure you have are logged in
```
oc status
oc whoami
```
and you have setup the project/namespace

```
oc new-project springistio
oc adm policy add-scc-to-user privileged -z default -n springistio

```

```
cd customer
mvn clean package
docker build -t example/customer .
docker images | grep customer
```
Note: Your very first docker build will take a bit of time as it downloads all the layers.  Subsequent rebuilds of the docker image, updating only the jar/app layer will be very fast.

Currently using the "manual" way of injecting the Envoy sidecar
Add istioctl to your $PATH

```
istioctl version

oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n springistio

oc create -f src/main/kubernetes/Service.yml

oc expose service customer

oc get route

oc get pods -w
```
Waiting for Ready 2/2
```
curl customer-springistio.$(minishift ip).nip.io

```
You should see the following error because preferences is not yet deployed, so you only get a partial response of "C100" plus the error message
```
C100 *I/O error on GET request for "http://preferences:8080/"
```
```
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

oc get pods -w

curl customer-springistio.$(minishift ip).nip.io
```
Preferences returns a value but also an error message based on the missing recommendations service
```
C100 *{"P1":"Red", "P2":"Big"} && I/O error on GET request for "http://recommendations:8080/"
```

```
cd ..
```

## Deploy recommendations
```
cd recommendations

mvn clean package

docker build -t example/recommendations:v1 .

docker images | grep recommendations

oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n springistio

oc create -f src/main/kubernetes/Service.yml

oc get pods -w

curl customer-springistio.$(minishift ip).nip.io
```

it returns

```
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 * 

```
## Updating & redeploying code
When you wish to change code (e.g. editing the .java files) and wish to "redeploy", simply:
```
cd {servicename}

vi src/main/java/com/example/{servicename}/{Servicename}

Controller.java
mvn clean package
docker build -t example/{servicename} .
oc get pods -o jsonpath='{.items[*].metadata.name}' -l app={servicename}
oc get pods -o jsonpath='{.items[*].metadata.name}' -l app={servicename},version=v1

oc delete pod -l app={servicename},version=v1
```
Based on the Deployment configuration, Kubernetes/OpenShift will recreate the pod, based on the new docker image as it attempts to keep the desired replicas available

```
oc describe deployment recommendations | grep Replicas
```

## Tracing
TODO
```
minishift openshift service jaeger-query
```
## Monitoring
TODO
```
minishift openshift service prometheus
minishift openshift service grafana
```

## Istio RouteRule Changes
### recommendations:v2 
We can experiment with Istio routing rules by making a change to RecommendationsController.java like

```
System.out.println("Big Red Dog v2");

return "Clifford v2";
```

The "v2" tag during the docker build is significant.

There is also a 2nd deployment.yml file to label things correctly

```
cd recommendations

mvn clean compile package

docker build -t example/recommendations:v2 .

docker images | grep recommendations
example/recommendations                  v2                  c31e399a9628        5 seconds ago       438MB
example/recommendations                  latest              f072978d9cf6        8 minutes ago      438MB

```
*Important:* back up one directory before applying the deployment yaml
```
cd ..

oc apply -f <(istioctl kube-inject -f kubernetesfiles/recommendations_v2_deployment.yml) -n springistio

oc get pods -w
```
Wait for those pods to show "2/2", the istio-proxy/envoy sidecar is part of that pod

```
curl customer-springistio.$(minishift ip).nip.io
```

you likely see "Clifford v1"

```
curl customer-springistio.$(minishift ip).nip.io
```

you likely see "Clifford v2" as by default you get random load-balancing when there is more than one Pod behind a Service

Make sure you have established "springistio" as the namespace/project that you will be working in, allowing you to skip the -n springistio in subsequent commands

```
oc project springistio 
```

## Changing Istio RouteRules

#### All users to recommendations:v2
```
oc create -f istiofiles/route-rule-recommendations-v2.yml 

curl customer-springistio.$(minishift ip).nip.io
```

you should only see v2 being returned

#### All users to recommendations:v1
Note: "replace" instead of "create" since we are overlaying the previous rule

```
oc replace -f istiofiles/route-rule-recommendations-v1.yml 

oc get routerules

oc get routerules/recommendations-default -o yaml 
```
#### All users to recommendations v1 and v2 
By simply removing the rule

```
oc delete routerules/recommendations-default
```
#### Split traffic between v1 and v2
Canary Deployment scenario: push v2 into the cluster but slowing send end-user traffic to it, if you continue to see success, continue shifting more traffic over time

```
oc get pods -l app=recommendations
NAME                                  READY     STATUS    RESTARTS   AGE
recommendations-v1-3719512284-7mlzw   2/2       Running   6          2h
recommendations-v2-2815683430-vn77w   2/2       Running   0          1h
```
Create the routerule that will send 90% of requests to v1 and 10% to v2
```
oc create -f istiofiles/route-rule-recommendations-v1_and_v2.yml
```
and send in several requests

```bash
#!/bin/bash

while true
do curl customer-springistio.$(minishift ip).nip.io
echo
sleep .1
done
```

In another terminal, change the mixture to be 75/25
```
oc replace -f istiofiles/route-rule-recommendations-v1_and_v2_75_25.yml
```

Clean up
```
oc delete routerule recommendations-v1-v2
```


## Fault Injection
Apply some chaos engineering by throwing in some HTTP errors or network delays.  Understanding failure scenarios is a critical aspect of microservices architecture  (aka distributed computing)

### HTTP Error 503
By default, recommendations v1 and v2 are being randomly load-balanced as that is the default behavior in Kubernetes/OpenShift

```
oc get pods -l app=recommendations
NAME                                  READY     STATUS    RESTARTS   AGE
recommendations-v1-3719512284-7mlzw   2/2       Running   6          18h
recommendations-v2-2815683430-vn77w   2/2       Running   0          3h
```

You can inject 503's, for approximately 50% of the requests
```
oc create -f istiofiles/route-rule-recommendations-503.yml 

curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 * 
curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && 503 Service Unavailable * 
curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 * 
```
Clean up
```
oc delete routerule recommendations-503
```
### Delay
The most insidious of possible distributed computing faults is not a "down" service but a service that is responding slow, potentially causing a cascading failure in your network of services.

```
oc create -f istiofiles/route-rule-recommendations-delay.yml

curl customer-springistio.$(minishift ip).nip.io
```
You will notice many requets to the customer endpoint now have a delay.
If you are monitoring the logs for recommendations v1 and v2, you will also see the delay happens BEFORE the recommendations service is actually called

```
stern recommendations
or
./kubetail.sh
```
Clean up
```
oc delete routerule recommendations-delay
```

## Retry
Instead of failing immediately, retry the Service N more times

We will use Istio and return 503's about 50% of the time.  Send all users to v2 which will throw out some 503's

```
oc create -f istiofiles/route-rule-recommendations-v2_503.yml 
```

Now, if you hit the customer endpoint several times, you should see some 503's

```
curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && 503 Service Unavailable * 
```

Now add the retry rule
```
oc create -f istiofiles/route-rule-recommendations-v2_retry.yml 
```
and you will see it work every time
```
curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 * 
```
Now, delete the retry rule and see the old behavior, some random 503s
```
oc delete routerule recommendations-v2-retry

curl customer-springistio.$(minishift ip).nip.io
```
Now, delete the 503 rule and back to random load-balancing between v1 and v2
```
oc delete routerule recommendations-v2-503
curl customer-springistio.$(minishift ip).nip.io
```

## Timeout
Wait only N seconds before giving up and failing.  At this point, no other route rules should be in effect.  oc get routerules and oc delete routerule rulename if there are some.

First, introduce some wait time in recommendations v2. Update RecommendationsController.java to include a Thread.sleep, making it a slow perfomer

```java
        System.out.println("Big Red Dog v2 " + cnt);

        // begin circuit-breaker example
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
        System.out.println("recommendations ready to return");
        // end circuit-breaker example
        return "Clifford v2 " + cnt ;
```
Rebuild, redeploy
```
cd recommendations

mvn clean compile package

docker build -t example/recommendations:v2 .

docker images | grep recommendations

oc delete pod -l app=recommendations,version=v2

cd ..
```
Hit the customer endpoint a few times, to see the load-balancing between v1 and v2 but with v2 taking a bit of time to respond

```
curl customer-springistio.$(minishift ip).nip.io
curl customer-springistio.$(minishift ip).nip.io
curl customer-springistio.$(minishift ip).nip.io

```        

Then add the timeout rule

```
oc create -f istiofiles/route-rule-recommendations-timeout.yml

curl customer-springistio.$(minishift ip).nip.io
```
You will see it return v1 OR 504 after waiting about 1 second

```
curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 * 
curl customer-springistio.$(minishift ip).nip.io
C100 *{"P1":"Red", "P2":"Big"} && 504 Gateway Timeout * 
```

When completed, delete the timeout rule

```
oc delete routerule recommendations-timeout

```
## Smart routing based on user-agent header (Canary Deployment)

What is your user-agent?

https://www.whoishostingthis.com/tools/user-agent/

Note: the "user-agent" header being forward in the Customer and Preferences controllers in order for route rule modications around recommendations 

#### Set recommendations to all v1
```
oc create -f istiofiles/route-rule-recommendations-v1.yml 
```
#### Set Safari users to v2
```
oc create -f istiofiles/route-rule-safari-recommendations-v2.yml 

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
oc create -f istiofiles/route-rule-mobile-recommendations-v2.yml

curl -A "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4(KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5" http://customer-springistio.192.168.99.102.nip.io/
```

#### Clean up
```
oc delete routerule recommendations-default
```

## Mirroring Traffic (Dark Launch)
Wiretap, eavesdropping
Note: does not seem to work in 0.4.0

```
oc get pods -l app=recommendations
```
You should have 2 pods for recommendations based on the steps above

```
oc get routerules
```
You should have NO routerules
if so "oc delete routerule rulename"

Make sure you are in the main directory

```
oc create -f istiofiles/route-rule-recommendations-v1-mirror-v2.yml 

curl customer-springistio.$(minishift ip).nip.io
```

## Access Control

#### Whitelist

#### Blacklist

## Load Balancer

By default, you will see "round-robin" style load-balancing, but you can change it up, with the RANDOM option being fairly visible to the naked eye.

Add another v2 pod to the mix

```
oc scale deployment recommendations-v2 --replicas=2

```
Wait a bit (oc get pods -w to watch)
and curl the customer endpoint many times

```
curl customer-springistio.$(minishift ip).nip.io
```

Add a 3rd v2 pod to the mix

```
oc scale deployment recommendations-v2 --replicas=2

oc get pods
NAME                                  READY     STATUS    RESTARTS   AGE
customer-1755156816-cjd2z             2/2       Running   0          1h
preferences-3336288630-2cc6f          2/2       Running   0          1h
recommendations-v1-3719512284-bn42p   2/2       Running   0          59m
recommendations-v2-2815683430-97nnf   2/2       Running   0          43m
recommendations-v2-2815683430-d49n6   2/2       Running   0          51m
recommendations-v2-2815683430-tptf2   2/2       Running   0          33m
```

Now poll the customer endpoint

```bash
#!/bin/bash

while true
do curl customer-springistio.$(minishift ip).nip.io
echo
sleep .1
done
```
The results should follow a fairly norma round-robin distribution pattern

```
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 796 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 796 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 326 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 229 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 797 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 797 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 327 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 230 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 798 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 798 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 328 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 231 *
```

Now, add the Random LB DestinationPolicy

```
oc create -f istiofiles/recommendations_lb_policy_app.yml
```

And you should see a very different pattern of which pod is being selected
```
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 807 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 808 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 809 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 810 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 343 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 344 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 249 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 811 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 250 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 251 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 809 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 812 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 813 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 814 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v1 815 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 810 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 811 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 252 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 812 * 
C100 *{"P1":"Red", "P2":"Big"} && Clifford v2 813 * 
```

Clean up
```
oc delete  -f istiofiles/recommendations_lb_policy_app.yml

oc scale deployment recommendations-v2 --replicas=1
```

## Circuit Breaker
Note: Does not work!

#### Fail Fast with Max Connections & Max Pending Requests
Update RecommendationsController.java to include some logic that throws out some 503s.

```java
        System.out.println("Big Red Dog v2 " + cnt);

        /* begin circuit-breaker example
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
        System.out.println("recommendations ready to return");
        // end circuit-breaker example */
        // inject some poor behavior
        if (misbehave) {
            cnt = 0;
            misbehave = false;
            throw new ServiceUnavailableException();            
        } 
        // */   
        return "Clifford v2 " + cnt;
    }

    @RequestMapping("/misbehave")
    public HttpStatus misbehave() {
        this.misbehave = true;
        return HttpStatus.OK;
    }
            
```
Rebuild, redeploy
```
cd recommendations

mvn clean compile package

docker build -t example/recommendations:v2 .

docker images | grep recommendations

oc delete pod -l app=recommendations,version=v2
```

The deletion of the previously running pod will cause Kubernetes/OpenShift to restart it based on the new docker image.

```

cd ..

curl customer-springistio.$(minishift ip).nip.io

```        
Whenever you are hitting v2, you will notice the slowness in the response

Watch the logging output of recommendations

```
Terminal 1:
./kubetail.sh recommendations
or 
brew install stern
stern recommendations

Terminal 2:
curl customer-springistio.$(minishift ip).nip.io
```

Now add the circuit breaker. 

```
istioctl create -f istiofiles/recommendations_cb_policy_version_v2.yml
istioctl get destinationpolicies
```
More information on the fields for the simple circuit-breaker
https://istio.io/docs/reference/config/traffic-rules/destination-policies.html#simplecircuitbreakerpolicy

Add some load by polling the customer endpoint
```bash
#!/bin/bash

while true
do curl customer-springistio.$(minishift ip).nip.io
echo
sleep .5
done
```

or use ab

note: the trailing slash is important
```
ab -n 10 -c 2 http://customer-springistio.192.168.99.104.nip.io/
```

or use gatling

```
cd gatling_test
mvn integration-test
```

If you wish to peer inside the CB

```
istioctl get destinationpolicies recommendations-circuitbreaker -o yaml -n default
```
Clean up
```
istioctl delete -f istiofiles/recommendations_cb_policy_app.yml -n default
```

#### Pool ejection
There is a 2nd circuit-breaker policy yaml file. In this case, we are attempting load-balancing pool ejection.  We want that slow misbehaving recommendations v2 to be kicked out and all requests handled by v1.

Expos the recommendations via an OpenShift Route

```
oc expose service recommendations
```
Up the replica count on v2
```
oc scale deployment recommendations-v2 --replicas=2
```
Hit the newly exposed Route via its url
```
oc get route
curl recommendations-springistio.$(minishift ip).nip.io 
```
By defaul, you will see load-balancing behind that URL, across the 3 pods that are currently in play 
```
istioctl create -f istiofiles/recommendations_cb_policy_app.yml
```
and throw some more requests at the customer endpoint, while also watching the logs for recommendations to see the behavior change.

```bash
#!/bin/bash

while true
do curl customer-springistio.$(minishift ip).nip.io
echo
sleep .1
done
```

Now throw in some misbehavior
```
curl recommendations-springistio.$(minishift ip).nip.io/misbehave 
```


Clean up

```
istioctl delete destinationpolocies recommendations-circuitbreaker -n springistio
```

## Egress
There are two examples of egress routing, one for httpbin.org and one for github

#### Create HTTPBin Java App
```
cd egresshttpbin/

mvn spring-boot:run

curl locahost:8080

ctrl-c

mvn clean package

docker build -t example/egresshttpbin:v1 .

docker images | grep egress

docker run -it -p 8080:8080 --rm example/egresshttpbin:v1

curl $(minishift ip):8080

ctrl-c

docker ps | grep egress

docker ps -a | grep egress

oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n istioegress

oc create -f src/main/kubernetes/Service.yml

oc expose service egresshttpbin

curl egresshttpbin-istioegress.$(minishift ip).nip.io

```

Note: It does not work...yet

```
cd ..
```

#### Create the Github Java App
```
cd egressgithub/

mvn clean package

docker build -t example/egressgithub:v1 .

docker images | grep egress

docker run -it -p 8080:8080 --rm example/egressgithub:v1

curl $(minishift ip):8080
```

Note: it will not work now but it will once Istio-ized

```
ctrl-c

docker ps | grep egress

oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n istioegress

oc create -f src/main/kubernetes/Service.yml

oc expose service egressgithub

curl egressgithub-istioegress.$(minishift ip).nip.io

cd ..
```

#### Istio-ize Egress
```
istioctl create -f istiofiles/egress_httpbin.yml

istioctl get egressrules

curl egresshttpbin-istioegress.$(minishift ip).nip.io
```
or
```
oc get pods

oc exec -it egresshttpbin-v1-1125123520-4599t /bin/bash

curl localhost:8080

curl httpbin.org/user-agent

curl httpbin.org/headers

exit 
```
add a egressrule for google
```
cat <<EOF | istioctl create -f -
apiVersion: config.istio.io/v1alpha2
kind: EgressRule
metadata:
  name: google-egress-rule
spec:
  destination:
    service: www.google.com
  ports:
    - port: 443
      protocol: https
EOF
```
```
oc exec -it egresshttpbin-v1-1125123520-4599t /bin/bash

curl http://www.google.com:443

exit
```

```
istioctl create -f istiofiles/egress_github.yml

curl egressgithub-istioegress.$(minishift ip).nip.io
```

## Rate Limiting
Here we will limit the number of concurrent requests into recommendations v2

Current view of RecommendationsController.java
```java
package com.example.recommendations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@RestController
public class RecommendationsController {
    
    @RequestMapping("/")
    public String getRecommendations() {
        
        System.out.println("Big Red Dog v2");

        // begin timeout and/or circuit-breaker example
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
        System.out.println("recommendations ready to return");
        // end circuit-breaker example */
        // throw new ServiceUnavailableException();
        return "Clifford v2";
    }

}

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ServiceUnavailableException extends RuntimeException {

}
```

Now apply the rate limit handler

```
istioctl create -f istiofiles/recommendations_rate_limit_handler.yml 
```

Now setup the requestcount quota

```
istioctl create -f istiofiles/recommendations_rate_limit_handler.yml 
```

Throw some requests at customer

```bash
#!/bin/bash

while true
do curl customer-springistio.$(minishift ip).nip.io
echo
sleep .1
done
```

And you should see some 429 Too Many Requests 

```
C100 *{"P1":"Red", "P2":"Big"} && 429 Too Many Requests *
```

Clean up

```
istioctl delete -f istiofiles/rate_limit_rule.yml 

istioctl delete -f istiofiles/recommendations_rate_limit_handler.yml
```

## Tips & Tricks

You have two containers in a pod
```
oc get pods -o jsonpath="{.items[*].spec.containers[*].name}" -l app=customer
```
From these images
```
oc get pods -o jsonpath="{.items[*].spec.containers[*].image}" -l app=customer
```
Get the pod ids
```
CPOD=$(oc get pods -o jsonpath='{.items[*].metadata.name}' -l app=customer)
PPOD=$(oc get pods -o jsonpath='{.items[*].metadata.name}' -l app=preferences)
RPOD1=$(oc get pods -o jsonpath='{.items[*].metadata.name}' -l app=recommendations,
version=v1)
RPOD2=$(oc get pods -o jsonpath='{.items[*].metadata.name}' -l app=recommendations,version=v2)
```

The pods all see each other's services
```
oc exec $CPOD -c customer curl http://preferences:8080
oc exec $CPOD -c customer curl http://recommendations:8080
oc exec $RPOD2 -c recommendations curl http://customer:8080
```

```
oc exec $CPOD -c customer curl http://localhost:15000/routes > afile.json
```
Look for "route_config_name": "8080", you should see 3 entries for customer, preferences and recommendations
https://gist.github.com/burrsutter/9117266f84efe124590e9014793c10f6

Now add a new routerule 
```
oc create -f istiofiles/route-rule-recommendations-v2.yml 
```
The review the routes again
```
oc exec $CPOD -c customer curl http://localhost:15000/routes > bfile.json
```

Here is the Before:
https://gist.github.com/burrsutter/9117266f84efe124590e9014793c10f6#file-gistfile1-txt-L41

and

https://gist.github.com/burrsutter/9117266f84efe124590e9014793c10f6#file-gistfile1-txt-L45

And the After:
https://gist.github.com/burrsutter/8b92da2ad0a8ec1b975f5dfa6ddc17f8#file-gistfile1-txt-L41

and

https://gist.github.com/burrsutter/8b92da2ad0a8ec1b975f5dfa6ddc17f8#file-gistfile1-txt-L45


If you need the Pod IP 
```
oc get pods -o jsonpath='{.items[*].status.podIP}' -l app=customer
```

Dive into the istio-proxy container

```
oc exec -it $CPOD -c istio-proxy /bin/bash
cd /etc/istio/proxy
ls
cat envoy-rev3.json
```

Snowdrop Troubleshooting
https://github.com/snowdrop/spring-boot-quickstart-istio/blob/master/TROUBLESHOOT.md