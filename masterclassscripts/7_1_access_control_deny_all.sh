#!/bin/bash

# kubectl exec recommendation and show that possible communication between recommedation and preference

kubectl apply -f istiofiles/authorization-policy-deny-all.yaml -n tutorial

curl $GATEWAY_URL/customer