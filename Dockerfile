FROM openjdk:11-jdk AS build
WORKDIR /tmp
COPY . /tmp
RUN chmod +x ./gradlew && ./gradlew clean && ./gradlew bootJar

FROM openjdk:11-jdk
WORKDIR /tmp
COPY --from=build /tmp/build/libs/gallery-1.0.jar /tmp/DessertGallery.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /tmp/DessertGallery.jar"]