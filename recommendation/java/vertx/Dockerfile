FROM maven:latest as java-builder
COPY . /src
WORKDIR /src
RUN mvn clean package

FROM fabric8/java-jboss-openjdk8-jdk:1.3.1
ENV JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY --from=java-builder /src/target/recommendation.jar /deployments/
