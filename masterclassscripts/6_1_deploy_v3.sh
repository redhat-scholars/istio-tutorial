#!/bin/bash

kubectl apply -f ./../recommendation/kubernetes/Deployment-v3.yml -n tutorial

kubectl apply -f ./../istiofiles/destination-rule-recommendation-v1-v2-v3.yml -n tutorial
kubectl create -f ./../istiofiles/virtual-service-recommendation-v3.yml