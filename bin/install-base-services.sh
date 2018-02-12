#!/bin/bash

oc apply -f <(istioctl kube-inject --debug -f ../customer/src/main/kubernetes/Deployment.yml)
oc apply -f ../customer/src/main/kubernetes/Service.yml
oc apply -f <(istioctl kube-inject --debug -f ../preference/src/main/kubernetes/Deployment.yml)
oc apply -f ../preference/src/main/kubernetes/Service.yml
oc apply -f <(istioctl kube-inject --debug -f ../recommendation/src/main/kubernetes/Deployment.yml)
oc apply -f ../recommendation/src/main/kubernetes/Service.yml

oc expose service customer
oc get route