#!/bin/sh
docker run -v `pwd`:/antora --platform linux/amd64 --rm -t antora/antora:2.2.0 --pull --stacktrace site-workshop.yml
open gh-pages/workshop/istio-tutorial/1.1.x/index.html
