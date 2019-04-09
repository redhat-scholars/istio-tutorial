####
# This Dockerfile is used in order to build a container that runs the Quarkus application in native (no JVM) mode
#
# Before building the docker image run:
#
# mvn package -Pnative -Dnative-image.docker-build=true
#
# Then, build the image with:
#
# docker build -f src/main/docker/Dockerfile.native -t quarkus/preference .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 quarkus/preference
#
###
FROM registry.fedoraproject.org/fedora-minimal
WORKDIR /work/
COPY target/*-runner /work/preference
RUN chmod 775 /work
EXPOSE 8080
CMD ["./preference", "-Xmx8m", "-Xmn8m", "-Xms8m"]
# CMD ["./preference", "-Dquarkus.http.host=0.0.0.0"]