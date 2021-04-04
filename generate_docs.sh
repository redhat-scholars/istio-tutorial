#!/bin/sh
docker run -v `pwd`:/antora --rm -t antora/antora:2.2.0 --pull --stacktrace site-gh-pages.yml
INDEX_BASE=gh-pages/istio-tutorial/1.6.x/index.html
case $(uname) in
  Linux )
    xdg-open "$INDEX_BASE"
  ;;
  Darwin )
    open "$INDEX_BASE"
  ;;
  * )
  ;;
esac
