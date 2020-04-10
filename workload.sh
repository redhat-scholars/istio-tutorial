while; do curl $(oc get route/customer --output jsonpath='{.spec.port.targetPort}://{.spec.host}'); done
