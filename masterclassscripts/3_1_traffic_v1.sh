#!/bin/sh

kubectl create -f ./../istiofiles/destination-rule-recommendation-v1-v2.yml -n tutorial
kubectl create -f ./../istiofiles/virtual-service-recommendation-v1.yml -n tutorial

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer