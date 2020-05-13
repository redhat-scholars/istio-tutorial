#!/bin/bash

kubectl apply -f ./../recommendation/kubernetes/Deployment-v2-timeout.yml -n tutorial

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer