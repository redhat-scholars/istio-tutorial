eval "oc patch servicemeshmemberroll/default --type=json --patch '[{\"op\":\"add\",\"path\":\"/spec/members/0\",\"value\":\"$(oc config view --minify --output 'jsonpath={..namespace}')\"}]' --namespace istio-system"
oc delete limitrange/$(oc config view --minify --output 'jsonpath={..namespace}')-core-resource-limits
