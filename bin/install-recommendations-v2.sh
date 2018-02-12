#!/bin/bash

oc apply -f <(istioctl kube-inject --debug -f ../recommendation/src/main/kubernetes/Deployment-v2.yml)
