#!/bin/bash

kubectl exec -it $(kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'|head -1) -n tutorial -c recommendation curl localhost:8080/misbehave

open https://$(kubectl get route kiali -n istio-system --output 'jsonpath={.status.ingress[].host}')

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer