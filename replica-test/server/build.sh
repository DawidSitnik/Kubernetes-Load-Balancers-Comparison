#!/bin/bash

cd "$(dirname "$0")"

# Request root privilege
[ "$UID" -eq 0 ] || exec sudo "$0" "$UID" "$(id -g)"

ARG_UID=$1
ARG_GID=$2

# Maven build
mvn clean:clean compiler:compile jar:jar
if [ $? -ne 0 ]
then
    echo "Maven build failed!"
    exit 1
else
    echo ""
fi

# Change owner of target directory
chown -R $ARG_UID:$ARG_GID target/

# Docker build
docker build -t cdp1-kube-replica-server .
