#!/bin/bash
./scripts/clean.sh
istioctl create -f istiofiles/destination-rule-recommendation.yml -n tutorial
istioctl create -f istiofiles/virtual-service-recommendation-503.yml -n tutorial