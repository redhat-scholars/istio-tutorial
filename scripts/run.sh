#!/bin/bash

url=$1
if [ -z "$url" ]
then
    url="istio-ingressgateway-istio-system.$(minishift ip).nip.io/customer"
fi

while true
do curl $url
sleep .5
done