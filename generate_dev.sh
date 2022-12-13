#!/bin/sh
docker run -v `pwd`:/antora --platform linux/amd64 --rm -t antora/antora:2.3.4 --pull --stacktrace dev-site.yml
