#!/bin/bash

namespace=$1

if [ -z "$namespace" ]; then
    namespace="tutorial"
fi

contentvs=`kubectl get virtualservice -n "$namespace" 2>/dev/null` 

if [ -z "$contentvs" ]; then
    echo "No Virtual Services in $namespace namespace."
else
    contentvs=`awk 'NR>1' <<< "$contentvs"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentvs"`

    for name in ${names[@]}
    do
        if [ "$name" != "customer-gateway" ]; then
            kubectl delete virtualservice "$name" -n "$namespace"
        fi
    done
    
fi

contentdr=`kubectl get destinationrule -n "$namespace" 2>/dev/null`

if [ -z "$contentdr" ]; then
    echo "No Destination Rule in $namespace namespace."
else
    contentdr=`awk 'NR>1' <<< "$contentdr"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentdr"`

    for name in ${names[@]}
    do
        kubectl delete destinationrule "$name" -n "$namespace"
    done
    
fi

contentse=`kubectl get serviceentry -n "$namespace" 2>/dev/null`

if [ -z "$contentse" ]; then
    echo "No Service Entry in $namespace namespace."
else
    contentse=`awk 'NR>1' <<< "$contentse"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentse"`

    for name in ${names[@]}
    do
        kubectl delete serviceentry "$name" -n "$namespace"
    done
    
fi

contentp=`kubectl get policy -n "$namespace" 2>/dev/null`

if [ -z "$contentp" ]; then
    echo "No Policy in $namespace namespace."
else
    contentp=`awk 'NR>1' <<< "$contentp"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentp"`

    for name in ${names[@]}
    do
        kubectl delete policy "$name" -n "$namespace"
    done
    
fi

contentsr=`kubectl get servicerole -n "$namespace" 2>/dev/null`

if [ -z "$contentsr" ]; then
    echo "No ServiceRole in $namespace namespace."
else
    contentsr=`awk 'NR>1' <<< "$contentsr"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentsr"`

    for name in ${names[@]}
    do
        kubectl delete servicerole "$name" -n "$namespace"
    done
    
fi

contentsrb=`kubectl get servicerolebinding -n "$namespace" 2>/dev/null`

if [ -z "$contentsrb" ]; then
    echo "No ServiceRoleBinding in $namespace namespace."
else
    contentsrb=`awk 'NR>1' <<< "$contentsrb"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentsrb"`

    for name in ${names[@]}
    do
        kubectl delete servicerolebinding "$name" -n "$namespace"
    done
    
fi

contentrbc=`kubectl get rbacconfig -n "$namespace" 2>/dev/null`

if [ -z "$contentrbc" ]; then
    echo "No RbacConfig in $namespace namespace."
else
    contentrbc=`awk 'NR>1' <<< "$contentrbc"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentrbc"`

    for name in ${names[@]}
    do
        kubectl delete rbacconfig "$name" -n "$namespace"
    done
    
fi

contentcrbc=`kubectl get ClusterRbacConfig -n "$namespace" 2>/dev/null`

if [ -z "$contentcrbc" ]; then
    echo "No ClusterRbacConfig in $namespace namespace."
else
    contentcrbc=`awk 'NR>1' <<< "$contentcrbc"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentcrbc"`

    for name in ${names[@]}
    do
        kubectl delete ClusterRbacConfig "$name" -n "$namespace"
    done
    
fi
