FROM spectolabs/hoverfly:v0.16.0

RUN apt-get update && apt-get install -y \
  curl

EXPOSE 8080 8888

ADD simulation.json /go/bin

ENTRYPOINT ["/go/bin/hoverfly", "-listen-on-host=0.0.0.0", "-webserver", "-pp=8080"]
CMD [""]