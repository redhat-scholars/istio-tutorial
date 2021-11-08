#!/bin/bash

istioctl experimental authz check $(kubectl get pods -n tutorial|grep customer|awk '{ print $1 }'|head -1) -a

kubectl get pod -o wide

kubectl sniff -i eth0 -o ./capture1.pcap customer-6948b8b959-zdlbz -f '((tcp) and (net 10.130.2.8))' -n tutorial -p -c istio-proxy

curl $GATEWAY_URL/customer

#open wireshark

kubectl apply -f istiofiles/disable-mtls.yml

istioctl experimental authz check $(kubectl get pods -n tutorial|grep customer|awk '{ print $1 }'|head -1) -a


kubectl sniff -i eth0 -o ./capture2.pcap customer-6948b8b959-zdlbz -f 'tcp) and (net 10.130.2.8' -n tutorial -p -c istio-proxy

curl $GATEWAY_URL/customer

# open wireshark