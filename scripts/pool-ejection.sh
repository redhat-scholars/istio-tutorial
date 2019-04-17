#!/bin/bash
oc scale deployment recommendation-v2 --replicas=2 -n tutorial
./scripts/clean.sh
kubectl create -f istiofiles/destination-rule-recommendation-v1-v2.yml -n tutorial
kubectl create -f istiofiles/virtual-service-recommendation-v1_and_v2_50_50.yml -n tutorial
kubectl replace -f  istiofiles/destination-rule-recommendation_cb_policy_pool_ejection.yml -n tutorial