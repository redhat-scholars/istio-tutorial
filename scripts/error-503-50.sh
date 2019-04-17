#!/bin/bash
./scripts/clean.sh
kubectl create -f istiofiles/destination-rule-recommendation.yml -n tutorial
kubectl create -f istiofiles/virtual-service-recommendation-503.yml -n tutorial