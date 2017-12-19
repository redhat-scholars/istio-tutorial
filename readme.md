1. deploy customer

2. deploy preferences

3. deploy recommendations

4. create recommendations:v2 so we can experiment with Istio routing rules

Make a change to RecommendationsController.java like

return "Clifford v2";

cd recommendations

mvn clean compile package

docker build -t example/recommendations:v2 .

docker images | grep recommendations

cd ..

oc apply -f <(istioctl kube-inject -f zotherfiles/recommendations_v2_deployment.yml) -n springistio

oc get pods -w

curl customer-springistio.$(minishift ip).nip.io

you likely see "Clifford v1"

curl customer-springistio.$(minishift ip).nip.io

you likely see "Clifford v2"

as by default you get random load-balancing when there is more than one Pod behind a Service



