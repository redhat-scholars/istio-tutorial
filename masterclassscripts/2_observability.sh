#!/bin/sh

open https://$(kubectl get route grafana -n istio-system --output 'jsonpath={.status.ingress[].host}')
open https://$(kubectl get route kiali -n istio-system --output 'jsonpath={.status.ingress[].host}')
open https://$(kubectl get route jaeger -n istio-system --output 'jsonpath={.status.ingress[].host}')
open https://$(kubectl get route prometheus -n istio-system --output 'jsonpath={.status.ingress[].host}')
