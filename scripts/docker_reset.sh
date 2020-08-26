#!/usr/bin/env bash

# this will stop and remove ALL containers running locally!

# stop all containers
docker stop $(docker ps -aq)

# remove all containers
docker rm $(docker ps -aq)

# prune all networks
docker network prune

# print summary
docker network ls
docker ps -a


