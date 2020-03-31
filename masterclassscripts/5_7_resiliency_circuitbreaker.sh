#!/bin/bash

kubectl apply -f ./../istiofiles/destination-rule-recommendation_cb_policy_version_v2.yml -n tutorial

kubectl logs `kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'` -c recommendation -n tutorial

echo "***********************************************"

siege -r 10 -c 4 -v http://$(kubectl get route istio-ingressgateway -n istio-system --output 'jsonpath={.status.ingress[].host}')/customer

echo "***********************************************"

kubectl logs `kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'` -c recommendation -n tutorial