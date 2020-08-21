#!/usr/bin/env bash

# start a local instance of the app backed by a postgres db
# see ../src/main/resources/application-local.properties
mvn clean package -Dmaven.test.skip=true -Dcheckstyle.skip
mvn docker:stop docker:start
java -jar -Dspring.profiles.active=local ./target/*.jar
