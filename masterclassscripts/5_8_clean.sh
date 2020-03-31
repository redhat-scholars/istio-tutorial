#!/bin/bash

kubectl delete -f ./../istiofiles/destination-rule-recommendation_cb_policy_version_v2.yml
kubectl create -f ./../istiofiles/destination-rule-recommendation-v1-v2.yml -n tutorial

kubectl exec -it $(kubectl get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'|head -1) -n tutorial -c recommendation curl localhost:8080/behave