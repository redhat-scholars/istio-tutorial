#!/bin/bash

kubectl patch deployment recommendation-v2 -p '{"spec":{"template":{"spec":{"containers":[{"name":"recommendation", "image":"quay.io/rhdevelopers/istio-tutorial-recommendation:v2-timeout"}]}}}}' -n tutorial

./../scripts/run.sh http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer