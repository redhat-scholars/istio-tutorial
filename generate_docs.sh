#!/bin/sh

docker run -v `pwd`:/antora --rm -t antora/antora:1.1.1 --pull --stacktrace site.yml
