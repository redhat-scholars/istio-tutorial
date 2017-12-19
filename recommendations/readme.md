
1. mvn clean package

2. docker build -t example/recommendations .

3. docker images | grep recommendations

4. Add istioctl to your $PATH

istioctl version

5. oc apply -f <(istioctl kube-inject -f src/main/kubernetes/Deployment.yml) -n springistio

6. oc create -f src/main/kubernetes/Service.yml

7. curl customer-springistio.$(minishift ip).nip.io

8. Check out your Grafana, Jaeger and Service Graph dashboards

Tips:

* To view logs when there is a sidecar

oc logs customer-3857234246-qtczv -c spring-boot

