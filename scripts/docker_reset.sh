#!/usr/bin/env bash

# stop all containers
docker stop $(docker ps -aq)

# remove all containers
docker rm $(docker ps -aq)

# prune all networks
docker network prune

# print summary
docker network ls
docker ps -a


