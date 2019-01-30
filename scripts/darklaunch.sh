#!/bin/bash
./scripts/clean.sh
istioctl create -f istiofiles/destination-rule-recommendation-v1-v2.yml -n tutorial
istioctl create -f istiofiles/virtual-service-recommendation-v1-mirror-v2.yml -n tutorial