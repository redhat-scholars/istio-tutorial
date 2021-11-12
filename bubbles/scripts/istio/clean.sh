#!/bin/bash

kubectl delete -f ../../bubbles-backend/src/main/istio/destination-rule-bubble-backend-v1-v2.yaml
kubectl delete -f ../../bubbles-backend/src/main/istio/virtual-service-bubble-backend-v1_and_v2_100_0.yaml

kubectl delete -f ../../bubbles-backend/src/main/kubernetes/Deployment.yaml
kubectl delete -f ../../bubbles-backend/src/main/kubernetes/Deployment-v2.yaml
kubectl delete -f ../../bubbles-backend/src/main/kubernetes/Service.yaml

kubectl delete -f ../../bubbles-frontend/src/main/kubernetes/Deployment.yaml
kubectl delete -f ../../bubbles-frontend/src/main/kubernetes/Service.yaml

kubectl delete -f ../../bubbles-frontend/src/main/istio/Gateway.yaml
