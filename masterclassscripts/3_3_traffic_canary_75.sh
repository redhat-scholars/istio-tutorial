#!/bin/sh

kubectl apply -f ./../istiofiles/virtual-service-recommendation-v1_and_v2_75_25.yml -n tutorial

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer