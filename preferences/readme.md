Note: Skip steps 1 to 9 if you already did them for "customer"

1. start.spring.io and select the following:
```
web
actuator
devtools
```

2. Add Controller.java 

3. mvn spring-boot:run and test it localhost:8080

add fabric8/deployment.yml because you need to override the default live & ready probes otherwise you get: [customer-2691584122-cs8rz istio-proxy] [2017-11-17 00:35:15.001][12][warning][upstream] external/envoy/source/server/lds_subscription.cc:65] lds: fetch failure: error adding listener: 'http_172.17.0.20_8080' has duplicate address '172.17.0.20:8080' as existing listener

https://github.com/istio/istio/issues/1194

should look like

        livenessProbe:
          exec:
            command: 
            - curl
            - localhost:8080/health

4. eval $(minishift oc-env)

5. oc login

6. oc new-project springistio

7. oc adm policy add-scc-to-user privileged -z default -n springistio

8. mvn io.fabric8:fabric8-maven-plugin:3.5.28:setup

Note: this step was already executed on this project

9. eval $(minishift docker-env)

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


