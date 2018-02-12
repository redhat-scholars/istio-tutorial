#!/bin/bash

URL="customer-tutorial.$(minishift ip).nip.io"

while true
do curl $URL
sleep .1
done

