#!/bin/bash

echo "###### Deploying FrontEnd ######"

kubectl apply -f ../../bubbles-frontend/src/main/kubernetes/Deployment.yaml
kubectl apply -f ../../bubbles-frontend/src/main/kubernetes/Service.yaml

kubectl wait --for=condition=available --timeout=600s deployment/bubblefrontend

kubectl apply -f ../../bubbles-frontend/src/main/istio/Gateway.yaml

echo "###### Deploying BackEnd ######"

kubectl apply -f ../../bubbles-backend/src/main/kubernetes/Deployment.yaml
kubectl apply -f ../../bubbles-backend/src/main/kubernetes/Deployment-v2.yaml
kubectl apply -f ../../bubbles-backend/src/main/kubernetes/Service.yaml

kubectl wait --for=condition=available --timeout=600s deployment/bubblebackend
kubectl wait --for=condition=available --timeout=600s deployment/bubblebackend-v2

echo "###### Redirecting Traffic to V1 ######"

kubectl apply -f ../../bubbles-backend/src/main/istio/destination-rule-bubble-backend-v1-v2.yaml
kubectl apply -f ../../bubbles-backend/src/main/istio/virtual-service-bubble-backend-v1_and_v2_100_0.yaml

kubectl get routes -n istio-system

echo "/bubble/index.html"