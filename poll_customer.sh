#!/bin/bash

while true
do curl customer-springistio.$(minishift ip).nip.io
echo
sleep .1
done

