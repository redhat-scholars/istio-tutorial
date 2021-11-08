#!/bin/bash

kubectl create -f istiofiles/authorization-policy-jwt.yaml -n tutorial

curl -H "Authorization: Bearer $TOKEN" $GATEWAY_URL/customer

TOKEN=$(curl https://gist.githubusercontent.com/lordofthejars/f590c80b8d83ea1244febb2c73954739/raw/21ec0ba0184726444d99018761cf0cd0ece35971/token.role.jwt -s)

curl -H "Authorization: Bearer $TOKEN" $GATEWAY_URL/customer
