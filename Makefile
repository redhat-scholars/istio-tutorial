default: cleanup istio new-project microservices

cleanup:
	find /root/projects -maxdepth 1 ! -path /root/projects ! -name ".*" ! -name istio-tutorial | xargs rm -fR
	rm -f /root/*.xml

istio:
	wget -c https://github.com/istio/istio/releases/download/1.0.2/istio-1.0.2-linux.tar.gz -P /root/installation

	tar -xvzf /root/installation/istio-1.0.2-linux.tar.gz -C /root/installation

	oc login -u system:admin
	oc adm policy add-cluster-role-to-user cluster-admin admin

	oc adm policy add-cluster-role-to-user cluster-admin developer
	oc adm policy add-scc-to-user anyuid -z istio-ingress-service-account -n istio-system
	oc adm policy add-scc-to-user anyuid -z default -n istio-system

	oc apply -f /root/installation/istio-1.0.2/install/kubernetes/helm/istio/templates/crds.yaml
	oc apply -f /root/installation/istio-1.0.2/install/kubernetes/istio-demo.yaml

	oc expose svc istio-ingressgateway -n istio-system
	oc expose svc servicegraph -n istio-system
	oc expose svc grafana -n istio-system
	oc expose svc prometheus -n istio-system
	oc expose svc tracing -n istio-system

new-project:
	oc new-project tutorial
	oc adm policy add-scc-to-user privileged -z default -n tutorial

microservices:
	mvn package -f /root/projects/istio-tutorial/customer/java/springboot -DskipTests
	docker build -t example/customer /root/projects/istio-tutorial/customer/java/springboot
	/root/installation/istio-1.0.2/bin/istioctl kube-inject -f /root/projects/istio-tutorial/customer/kubernetes/Deployment.yml | oc apply -n tutorial -f -
	oc create -f /root/projects/istio-tutorial/customer/kubernetes/Service.yml -n tutorial
	oc expose service customer -n tutorial

	mvn package -f /root/projects/istio-tutorial/preference/java/springboot -DskipTests
	docker build -t example/preference:v1 /root/projects/istio-tutorial/preference/java/springboot
	/root/installation/istio-1.0.2/bin/istioctl kube-inject -f /root/projects/istio-tutorial/preference/kubernetes/Deployment.yml | oc apply -n tutorial -f -
	oc create -f /root/projects/istio-tutorial/preference/kubernetes/Service.yml -n tutorial

	mvn package -f /root/projects/istio-tutorial/recommendation/java/vertx -DskipTests
	docker build -t example/recommendation:v1 /root/projects/istio-tutorial/recommendation/java/vertx
	/root/installation/istio-1.0.2/bin/istioctl kube-inject -f /root/projects/istio-tutorial/recommendation/kubernetes/Deployment.yml | oc apply -n tutorial -f -
	oc create -f /root/projects/istio-tutorial/recommendation/kubernetes/Service.yml -n tutorial
