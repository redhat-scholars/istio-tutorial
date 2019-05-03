 oc delete project istio-tutorial || true
 while oc get namespace istio-tutorial &> /dev/null;do echo \"Waiting for istio-operator to be deleted\";sleep 10;done
 
 oc new-project istio-tutorial
 oc adm policy add-scc-to-user anyuid -z default -n istio-tutorial
 oc adm policy add-scc-to-user privileged -z default -n istio-tutorial

oc create -n istio-tutorial -f customer/Service.yml
oc create -n istio-tutorial -f customer/Deployment-v1.yml
oc create -n istio-tutorial -f customer/Gateway.yml


oc create -n istio-tutorial -f preference/Service.yml
oc create -n istio-tutorial -f preference/Deployment-v1.yml
# oc create -n istio-tutorial -f preference/Deployment-v2.yml



oc create -n istio-tutorial -f recommendation/Service.yml
oc create -n istio-tutorial -f recommendation/Deployment-v1.yml
oc create -n istio-tutorial -f recommendation/Deployment-v2.yml
oc create -n istio-tutorial -f recommendation/PostgreSQL-deployment.yml
oc create -n istio-tutorial -f recommendation/PostgreSQL-service.yml



