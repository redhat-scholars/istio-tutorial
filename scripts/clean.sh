#!/bin/bash

namesp=$1

if [ -z "$namesp" ]; then
    namespace="tutorial"
fi

contentvs=`kubectl get virtualservice -n "$namesp" 2>/dev/null` 

if [ -z "$contentvs" ]; then
    echo "No Virtual Services in $namesp namespace."
else
    contentvs=`awk 'NR>1' <<< "$contentvs"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentvs"`

    for name in ${names[@]}
    do
        if [ "$name" != "customer-gateway" ]; then
            kubectl delete virtualservice "$name" -n "$namesp"
        fi
    done
    
fi

contentdr=`kubectl get destinationrule -n "$namesp" 2>/dev/null`

if [ -z "$contentdr" ]; then
    echo "No Destination Rule in $namesp namespace."
else
    contentdr=`awk 'NR>1' <<< "$contentdr"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentdr"`

    for name in ${names[@]}
    do
        kubectl delete destinationrule "$name" -n "$namesp"
    done
    
fi

contentse=`kubectl get serviceentry -n "$namesp" 2>/dev/null`

if [ -z "$contentse" ]; then
    echo "No Service Entry in $namesp namespace."
else
    contentse=`awk 'NR>1' <<< "$contentse"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentse"`

    for name in ${names[@]}
    do
        kubectl delete serviceentry "$name" -n "$namesp"
    done
    
fi

contentp=`kubectl get policy -n "$namesp" 2>/dev/null`

if [ -z "$contentp" ]; then
    echo "No Policy in $namesp namespace."
else
    contentp=`awk 'NR>1' <<< "$contentp"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentp"`

    for name in ${names[@]}
    do
        kubectl delete policy "$name" -n "$namesp"
    done
    
fi

contentsr=`kubectl get servicerole -n "$namesp" 2>/dev/null`

if [ -z "$contentsr" ]; then
    echo "No ServiceRole in $namesp namespace."
else
    contentsr=`awk 'NR>1' <<< "$contentsr"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentsr"`

    for name in ${names[@]}
    do
        kubectl delete servicerole "$name" -n "$namesp"
    done
    
fi

contentsrb=`kubectl get servicerolebinding -n "$namesp" 2>/dev/null`

if [ -z "$contentsrb" ]; then
    echo "No ServiceRoleBinding in $namesp namespace."
else
    contentsrb=`awk 'NR>1' <<< "$contentsrb"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentsrb"`

    for name in ${names[@]}
    do
        kubectl delete servicerolebinding "$name" -n "$namesp"
    done
    
fi

contentrbc=`kubectl get rbacconfig -n "$namesp" 2>/dev/null`

if [ -z "$contentrbc" ]; then
    echo "No RbacConfig in $namesp namespace."
else
    contentrbc=`awk 'NR>1' <<< "$contentrbc"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentrbc"`

    for name in ${names[@]}
    do
        kubectl delete rbacconfig "$name" -n "$namesp"
    done
    
fi

contentcrbc=`kubectl get ClusterRbacConfig -n "$namesp" 2>/dev/null`

if [ -z "$contentcrbc" ]; then
    echo "No ClusterRbacConfig in $namesp namespace."
else
    contentcrbc=`awk 'NR>1' <<< "$contentcrbc"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentcrbc"`

    for name in ${names[@]}
    do
        kubectl delete ClusterRbacConfig "$name" -n "$namesp"
    done
    
fi

contentap=`kubectl get authorizationpolicy -n "$namesp" 2>/dev/null`

if [ -z "$contentap" ]; then
    echo "No authorizationpolicy in $namesp namespace."
else
    contentap=`awk 'NR>1' <<< "$contentap"`

    names=`awk -v namespace="$namesp" '{ {print $1} }' <<< "$contentap"`

    for name in ${names[@]}
    do
        kubectl delete authorizationpolicy "$name" -n "$namesp"
    done
    
fi