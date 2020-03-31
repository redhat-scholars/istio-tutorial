#!/bin/bash

kubectl apply -f ./../istiofiles/virtual-service-recommendation-timeout.yml -n tutorial

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer