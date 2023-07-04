FROM openjdk:11-jdk AS build
WORKDIR /tmp
COPY . /tmp
RUN chmod +x ./gradlew && ./gradlew bootJar
RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
  && tar xzvf docker-17.04.0-ce.tgz \
  && mv docker/docker /usr/local/bin \
  && rm -r docker docker-17.04.0-ce.tgz

FROM openjdk:11-jdk
WORKDIR /tmp
COPY --from=build /tmp/build/libs/gallery-1.0.jar /tmp/DessertGallery.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /tmp/DessertGallery.jar"]