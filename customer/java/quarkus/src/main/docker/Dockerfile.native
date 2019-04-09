####
# This Dockerfile is used in order to build a container that runs the Quarkus application in native (no JVM) mode
#
# Before building the docker image run:
#
# mvn package -Pnative -Dnative-image.docker-build=true
#
# Then, build the image with:
#
# docker build -f src/main/docker/Dockerfile.native -t quarkus/customer .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 quarkus/customer
#
###
FROM registry.fedoraproject.org/fedora-minimal
WORKDIR /work/
COPY target/*-runner /work/customer
RUN chmod 775 /work
EXPOSE 8080
CMD ["./customer", "-Xmx8m", "-Xmn8m", "-Xms8m"]
# CMD ["./customer", "-Dquarkus.http.host=0.0.0.0"]
