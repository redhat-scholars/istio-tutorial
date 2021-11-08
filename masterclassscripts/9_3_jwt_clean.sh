#!/bin/bash

kubectl delete -f istiofiles/authorization-policy-jwt.yaml -n tutorial
kubectl delete -f istiofiles/enduser-authentication-jwt.yml -n tutorial