#!/bin/sh

kubectl apply -f customer/kubernetes/Deployment.yml
kubectl create -f customer/kubernetes/Service.yml -n tutorial
kubectl create -f customer/kubernetes/Gateway.yml -n tutorial

kubectl apply -f preference/kubernetes/Deployment.yml
kubectl create -f preference/kubernetes/Service.yml -n tutorial

kubectl apply -f recommendation/kubernetes/Deployment.yml
kubectl create -f recommendation/kubernetes/Service.yml -n tutorial

kubectl apply -f recommendation/kubernetes/Deployment-v2.yml
