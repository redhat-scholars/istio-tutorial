#!/bin/bash

kubectl apply -f istiofiles/enable-mtls.yml

kubectl delete pod -l app=ksniff