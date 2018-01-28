## Enable Initializers

TODO: ??Simplify this step ???

Since we will be using the Istio Initializers to inject Istio proxy sidecars into the service, we need to enable the Initializers in OpenShift.

```
minishift ssh
```

Edit /var/lib/minishift/openshift.local.config/master/master-config.yaml to look like:

```
(...)
admissionConfig:
  pluginConfig:
    GenericAdmissionWebhook:
      configuration:
        apiVersion: v1
        disable: false
        kind: DefaultAdmissionConfig
      location: ""
    Initializers:
      configuration:
        apiVersion: v1
        disable: false
        kind: DefaultAdmissionConfig
      location: ""
(...)
```
Once update is done, restart the OpenShift cluster using the command `minishift openshift restart`

To validate the initializers api availability,
```
kubectl api-versions | grep admi
```
That should return 
```
apis/admissionregistration.k8s.io/v1alpha1=true
```

## Istio installation script

```bash
#!/bin/bash

curl -LO https://github.com/istio/istio/releases/download/0.4.0/istio-0.4.0-osx.tar.gz

gunzip istio-0.4.0-osx.tar.gz

tar -xvzf istio-0.4.0-osx.tar

cd istio-0.4.0

# make sure we are logged in
oc login $(minishift ip):8443 -u admin -p admin

oc adm policy add-scc-to-user anyuid -z istio-ingress-service-account -n istio-system

oc adm policy add-scc-to-user anyuid -z istio-egress-service-account -n istio-system

oc adm policy add-scc-to-user anyuid -z default -n istio-system

oc create -f install/kubernetes/istio.yaml

oc project istio-system

oc expose svc istio-ingress

oc annotate dc docker-registry sidecar.istio.io/inject='false' -n default

oc annotate dc router 
sidecar.istio.io/inject='false' -n default

oc apply -f install/kubernetes/istio-initializer.yaml

oc adm policy add-cluster-role-to-user cluster-admin -z istio-initializer-service-account -n istio-system

oc apply -f install/kubernetes/addons/prometheus.yaml

oc apply -f install/kubernetes/addons/grafana.yaml

oc apply -f install/kubernetes/addons/servicegraph.yaml

oc expose svc servicegraph

oc expose svc grafana

oc expose svc prometheus

oc process -f https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/all-in-one/jaeger-all-in-one-template.yml | oc create -f -
```
