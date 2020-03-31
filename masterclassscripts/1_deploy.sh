#!/bin/sh

### IMPORTANT: tutorial namespace must be created before because to fit correctly with Maistra requirements.

kubectl config set-context $(kubectl config current-context) --namespace=tutorial

kubectl apply -f ./../customer/kubernetes/Deployment.yml -n tutorial
kubectl create -f ./../customer/kubernetes/Service.yml -n tutorial
kubectl create -f ./../customer/kubernetes/Gateway.yml -n tutorial

kubectl apply -f ./../preference/kubernetes/Deployment.yml -n tutorial
kubectl create -f ./../preference/kubernetes/Service.yml -n tutorial

kubectl apply -f ./../recommendation/kubernetes/Deployment.yml -n tutorial
kubectl create -f ./../recommendation/kubernetes/Service.yml -n tutorial

kubectl apply -f ./../recommendation/kubernetes/Deployment-v2.yml -n tutorial