#!/bin/sh

kubectl logs `kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'` -c recommendation -n tutorial

kubectl apply -f ./../istiofiles/virtual-service-recommendation-v1-mirror-v2.yml -n tutorial

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer

kubectl logs `kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'` -c recommendation -n tutorial