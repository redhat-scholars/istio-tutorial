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

oc project springistio (so it does not have to be repeated below)

oc create -f routerulefiles/route-rule-recommendations-v2.yml 

curl customer-springistio.$(minishift ip).nip.io

you should only see v2 being returned

oc replace -f routerulefiles/route-rule-recommendations-v1.yml 

oc get routerules

oc get routerules/recommendations-default -o yaml 

oc delete routerules/recommendations-default

5. Smart routing based on HTTP headers


