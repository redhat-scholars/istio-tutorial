#!/bin/bash

kubectl exec -it $(kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'|head -1) -n tutorial -c recommendation curl localhost:8080/misbehave

kubectl logs `kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'` -c recommendation -n tutorial

echo "***********************************************"

siege -r 10 -c 4 -v http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer

echo "***********************************************"

kubectl logs `kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'` -c recommendation -n tutorial