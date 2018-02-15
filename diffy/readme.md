Quick Demo steps:

Decide what the candidate and master services will be for diffy and install them:

```
kubectl create -f recommendation-candidate.yml
kubectl create -f recommendation-master.yml
```

Then, create the diffy deployment and service. Note, we don't inject the istio-proxy here. Since diffy is itself a proxy, it appears to conflict with the istio proxy. We should try to get this sorted out. 

```
kubectl create -f recommendation-diffy-deployment.yml
kubectl create -f recommendation-diffy-svc.yml
```

Now we have diffy in place configured to send traffic to both master and candidate and compare their payloads. Next, let's install the istio route rule to mirror traffic to diffy:

```
istioctl create -f route-rule-recommendation-v1-diff-mirror-v2.yml
```

use stern to watch the logs to see where the traffic is really going.

You can also see what's going on in diffy:

```
kubectl port-forward $(kubectl get pod | grep diffy | awk '{print $1}') 8888:8888  
```

Then go to [http://localhost:8888](http://localhost:8888)



