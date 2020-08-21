#!/usr/bin/env bash
#run all the tests and create a deployable jar inside of ../target/ folder
./mvnw docker:stop clean verify
