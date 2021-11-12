#!/bin/bash

echo "###### Deploying FrontEnd ######"

kubectl apply -f ../../bubbles-frontend/src/main/kubernetes/Deployment.yaml
kubectl apply -f ../../bubbles-frontend/src/main/kubernetes/Service.yaml

kubectl wait --for=condition=available --timeout=600s deployment/bubblefrontend

kubectl apply -f ../../bubbles-frontend/src/main/istio/Gateway.yaml

echo "###### Deploying BackEnd V1 ######"

kubectl apply -f ../../bubbles-backend/src/main/argorollouts/Service.yaml
kubectl apply -f ../../bubbles-backend/src/main/argorollouts/Service-canary.yaml
kubectl apply -f ../../bubbles-backend/src/main/argorollouts/virtual-service-bubble-backend-v1_and_v2_100_0.yaml

kubectl apply -f ../../bubbles-backend/src/main/argorollouts/rollout.yaml

kubectl get routes -n istio-system

echo "/bubble/index.html"