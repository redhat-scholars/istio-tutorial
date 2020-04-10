for microservice in customer preference recommendation; do oc apply --filename ${microservice}/kubernetes/Deployment.yml; oc apply --filename ${microservice}/kubernetes/Service.yml; done
