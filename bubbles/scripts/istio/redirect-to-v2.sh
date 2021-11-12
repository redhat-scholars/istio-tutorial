#!/bin/bash

echo "###### Redirecting Traffic to V2 ######"

kubectl apply -f ../../bubbles-backend/src/main/istio/virtual-service-bubble-backend-v1_and_v2_0_100.yaml