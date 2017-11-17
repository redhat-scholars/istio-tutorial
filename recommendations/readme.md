
1. start.spring.io and select the following:
```
web
actuator
devtools
```

2. Add Controller.java 

3. mvn spring-boot:run and test it localhost:8080

add fabric8/deployment.yml
because you need to override the default live & ready probes otherwise you get:
[customer-2691584122-cs8rz istio-proxy] [2017-11-17 00:35:15.001][12][warning][upstream] external/envoy/source/server/lds_subscription.cc:65] lds: fetch failure: error adding listener: 'http_172.17.0.20_8080' has duplicate address '172.17.0.20:8080' as existing listener 

https://github.com/istio/istio/issues/1194

should look like
```
        livenessProbe:
          exec:
            command: 
            - curl
            - localhost:8080/health
```
see the one in this project

4. minishift oc-env

5. oc login

6. oc new-project springistio

7. oc adm policy add-scc-to-user privileged -z default -n springistio

8. mvn io.fabric8:fabric8-maven-plugin:3.5.28:setup

9. mvn fabric8:resource

10. mvn fabric8:build

11. minishift docker-env

12. docker build -t recommendations:latest target/docker/recommendations/latest/build

13. Add istioctl to your PATH

14. oc apply -f <(istioctl kube-inject -f target/classes/META-INF/fabric8/kubernetes/recommendations-deployment.yml) -n springistio

15. oc create -f target/classes/META-INF/fabric8/kubernetes/recommendations-svc.yml

note: not exposing this service route as it is for inside the cluster consumption

16. Check out your Grafana, Jaeger and Service Graph dashboards