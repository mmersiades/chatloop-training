FROM openjdk:11-slim
EXPOSE 8888

ARG JAR_FILE=target/chatloop-user-training-*[^o].jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-Dserver.port=8888", "-jar", "app.jar"]
