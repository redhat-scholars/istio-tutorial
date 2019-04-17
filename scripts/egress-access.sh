#!/bin/bash
oc exec -it $(oc get pods -n tutorial|grep recommendation-v2|awk '{ print $1 }'|head -1) -n tutorial -c recommendation -- curl -v http://worldclockapi.com/api/json/cet/now