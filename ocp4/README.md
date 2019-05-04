
# Installing Istio using Maistra

If you are using Istio on Openshift, Maistra is your friend. It is the [Istio Tailored for Openshift](https://maistra.io/docs/comparison_with_community/comparison-with-istio-community/). In order to install it, just execute `./maistra/deploy-maistra.sh` and it will install [maistra operator](https://maistra.io/docs/comparison_with_istio_community/installer/)


# Installing Istio Tutorial (Install Everything + Traffic Generator)

In order to install it, just execute `./istio-tutorial/deploy-istio-tutorial.sh istio-tutorial` (where istio-tutorial is the namespace desired) and it will deploy everything based on `quay.io/rhdevelopers/istio-tutorial-*` images. If you want to add an local image just change the file and re-run the script.



# Installing Istio Tutorial (Step by Step)

If you want to install step by step, run the following commands:

- `oc new-project istio-tutorial`
- `./istio-tutorial/customer/deploy-customer istio-tutorial`
- `./istio-tutorial/preference/deploy-preference istio-tutorial`
- `./istio-tutorial/recommendation/deploy-recommendation istio-tutorial`

# Automatic Traffic Generator going on Istio Mesh

[Kiali Traffic Generator](https://github.com/kiali/kiali-test-mesh/tree/master/traffic-generator) automatically generates the load for you indefintely. If you want to have the load for specific amount of time, check the traffic-generator configmap property called `duration` and set it to specific time that you want (`duration: 0s` should it make run forever). `rate` controls the flow rate per second (`rate: 1` should send 1 request per second).

To install it, just execute `./istio-tutorial/deploy-traffic-generator.sh istio-tutorial`