oc new-project istio-operator
oc new-project istio-system


oc apply -n istio-operator -f maistra-operator.yaml
oc apply -n istio-system -f maistra-cr.yaml