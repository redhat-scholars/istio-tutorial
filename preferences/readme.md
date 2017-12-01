
1. start.spring.io and select the following:
```
web
actuator
devtools
```

2. Add Controller.java 

3. mvn spring-boot:run and test it localhost:8080

4. eval $(minishift oc-env)

5. oc login

6. oc new-project springistio

7. oc adm policy add-scc-to-user privileged -z default -n springistio

8. mvn io.fabric8:fabric8-maven-plugin:3.5.28:setup

Note: this step was already executed on this project

9. eval $(minishift docker-env)

10. mvn package fabric8:build -Dfabric8.mode=kubernetes

11. Add istioctl to your PATH

12. oc apply -f <(istioctl kube-inject -f target/classes/META-INF/fabric8/kubernetes/preferences-deployment.yml) -n springistio

13. oc create -f target/classes/META-INF/fabric8/kubernetes/preferences-svc.yml

Note: no route for preferences, it is internally consumed

14. Check out your Grafana, Jaeger and Service Graph dashboards

Tips:

* To view logs when there is a sidecar

oc logs customer-3857234246-qtczv -c spring-boot

* To add cpu/memory limits

kubectl run --limits='cpu=200m,memory=512Mi' 

* To change the live or ready probe

oc set-probe


