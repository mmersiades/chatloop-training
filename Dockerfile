FROM openjdk:11-slim
EXPOSE 5000

ARG JAR_FILE=target/chatloop-training-*[^o].jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-Dserver.port=5000", "-jar", "app.jar"]
