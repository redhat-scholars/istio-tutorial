


1. start.spring.io and select the following:
```
web
actuator
devtools
```

2. Add Controller.java 

3. mvn spring-boot:run and test it localhost:8080

Note: Watch out for live & ready probes as you might see: [customer-2691584122-cs8rz istio-proxy] [2017-11-17 00:35:15.001][12][warning][upstream] external/envoy/source/server/lds_subscription.cc:65] lds: fetch failure: error adding listener: 'http_172.17.0.20_8080' has duplicate address '172.17.0.20:8080' as existing listener

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

7. oc new-project tutorial

8. oc adm policy add-scc-to-user privileged -z default -n tutorial

9. mvn clean package

10. docker build -t example/customer .

11. docker images | grep customer

12. Add istioctl to your $PATH

istioctl version

13. oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n tutorial

14. oc create -f src/main/kubernetes/Service.yml

15. oc expose service customer

16. oc get route

17. curl customer-tutorial.$(minishift ip).nip.io

Note: you may see errors related to preferences being unavailable

18. Check out your Grafana, Jaeger and Service Graph dashboards

Tips:

* To view logs when there is a sidecar

oc logs customer-3857234246-qtczv -c spring-boot

