#!/bin/bash

kubectl delete -f ./../istiofiles/virtual-service-recommendation-timeout.yml -n tutorial

kubectl patch deployment recommendation-v2 -p '{"spec":{"template":{"spec":{"containers":[{"name":"recommendation", "image":"quay.io/rhdevelopers/istio-tutorial-recommendation:v2"}]}}}}' -n tutorial