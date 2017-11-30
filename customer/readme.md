
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

12. oc apply -f <(istioctl kube-inject -f target/classes/META-INF/fabric8/kubernetes/customer-deployment.yml) -n springistio

13. oc create -f target/classes/META-INF/fabric8/kubernetes/customer-svc.yml

14. oc expose service customer

15. oc get route

16. curl customer-springistio.$(minishift ip).nip.io

17. Check out your Grafana, Jaeger and Service Graph dashboards
