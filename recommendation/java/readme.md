
1. mvn clean package

2. docker build -t example/recommendation:v1 .

3. docker images | grep recommendation

4. Add istioctl to your $PATH

istioctl version

5. oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n tutorial

6. oc create -f src/main/kubernetes/Service.yml

7. curl customer-tutorial.$(minishift ip).nip.io

8. Check out your Grafana, Jaeger and Service Graph dashboards

Tips:

* To view logs when there is a sidecar

oc logs customer-3857234246-qtczv -c spring-boot

