#!/bin/bash

url=$1
if [ -z "$url" ]
then
    url="customer-tutorial.$(minishift ip).nip.io"
fi

while true
do curl $url
sleep .5
done