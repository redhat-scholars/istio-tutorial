#!/bin/bash

kubectl apply -f istiofiles/authorization-policy-allow-customer.yaml -n tutorial

curl $GATEWAY_URL/customer

kubectl apply -f istiofiles/authorization-policy-allow-preference.yaml -n tutorial

curl $GATEWAY_URL/customer

kubectl apply -f istiofiles/authorization-policy-allow-recommendation.yaml -n tutorial

curl $GATEWAY_URL/customer

# kubectl exec recommendation and show that possible communication between recommedation and preference