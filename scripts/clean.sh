#!/bin/bash

namespace=$1

if [ -z "$namespace" ]; then
    namespace="tutorial"
fi

contentvs=`istioctl get virtualservice -n "$namespace" 2>/dev/null` 

if [ -z "$contentvs" ]; then
    echo "No Virtual Services in $namespace namespace."
else
    contentvs=`awk 'NR>1' <<< "$contentvs"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentvs"`

    for name in "${names[@]}"
    do
        istioctl delete virtualservice "$name" -n "$namespace"
    done
    
fi

contentdr=`istioctl get destinationrule -n "$namespace" 2>/dev/null`

if [ -z "$contentdr" ]; then
    echo "No Destination Rule in $namespace namespace."
else
    contentdr=`awk 'NR>1' <<< "$contentdr"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentdr"`

    for name in "${names[@]}"
    do
        istioctl delete destinationrule "$name" -n "$namespace"
    done
    
fi

contentse=`istioctl get serviceentry -n "$namespace" 2>/dev/null`

if [ -z "$contentse" ]; then
    echo "No Service Entry in $namespace namespace."
else
    contentse=`awk 'NR>1' <<< "$contentse"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentse"`

    for name in "${names[@]}"
    do
        istioctl delete serviceentry "$name" -n "$namespace"
    done
    
fi

contentgw=`istioctl get gateway -n "$namespace" 2>/dev/null`

if [ -z "$contentgw" ]; then
    echo "No Gateway in $namespace namespace."
else
    contentgw=`awk 'NR>1' <<< "$contentgw"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentgw"`

    for name in "${names[@]}"
    do
        istioctl delete gateway "$name" -n "$namespace"
    done
    
fi

contentp=`istioctl get policy -n "$namespace" 2>/dev/null`

if [ -z "$contentp" ]; then
    echo "No Policy in $namespace namespace."
else
    contentp=`awk 'NR>1' <<< "$contentp"`

    names=`awk -v namespace="$namespace" '{ {print $1} }' <<< "$contentp"`

    for name in "${names[@]}"
    do
        istioctl delete policy "$name" -n "$namespace"
    done
    
fi