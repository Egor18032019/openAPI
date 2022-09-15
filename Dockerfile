FROM adoptopenjdk/openjdk11
#FROM openjdk:11 as build
#FROM adoptopenjdk/openjdk11:ubi

ARG JAR_FILE=target/enrollment-1.0-SNAPSHOT.jar
WORKDIR /opt/app
#Эта команда COPY берет все файлы, расположенные в текущем каталоге, и копирует их в образ.
COPY ${JAR_FILE} app.jar
#docker build --tag java-docker .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]