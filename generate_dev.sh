#!/bin/sh
docker run -v `pwd`:/antora --rm -t antora/antora:2.0.0 --pull --stacktrace dev-site.yml
open gh-pages/istio-tutorial/1.3.x/index.html