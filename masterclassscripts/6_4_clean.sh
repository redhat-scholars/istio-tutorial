#!/bin/bash

kubectl get configmap istio -n istio-system -o yaml | sed 's/mode: REGISTRY_ONLY/mode: ALLOW_ANY/g' | kubectl replace -n istio-system -f -

kubectl delete -f ./../recommendation/kubernetes/Deployment-v3.yml -n tutorial
kubectl delete -f ./../istiofiles/service-entry-egress-worldclockapi.yml -n tutorial

kubectl delete -f ./../istiofiles/destination-rule-recommendation-v1-v2-v3.yml -n tutorial
kubectl delete -f ./../istiofiles/virtual-service-recommendation-v3.yml