 oc delete project $1 || true
 while oc get namespace $1 &> /dev/null;do echo \"Waiting for $1 to be deleted\";sleep 10;done
 
 oc new-project $1
 oc adm policy add-scc-to-user anyuid -z default -n $1
 oc adm policy add-scc-to-user privileged -z default -n $1

oc create -n $1 -f customer/Service.yml
oc create -n $1 -f customer/Deployment-v1.yml
oc create -n $1 -f customer/Gateway.yml


oc create -n $1 -f preference/Service.yml
oc create -n $1 -f preference/Deployment-v1.yml
# oc create -n $1 -f preference/Deployment-v2.yml



oc create -n $1 -f recommendation/Service.yml
oc create -n $1 -f recommendation/Deployment-v1.yml
oc create -n $1 -f recommendation/Deployment-v2.yml



oc create -n $1 -f traffic-generator/configmap.yml
oc create -n $1 -f traffic-generator/Deployment.yml


