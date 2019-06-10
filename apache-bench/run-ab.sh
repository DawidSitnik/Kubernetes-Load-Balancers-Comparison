#!/bin/bash
cd "$(dirname "$0")"

SERVER_IP=$1
POOL_COUNT=$2
MSG_COUNT=$3
FILE_NAME=$4

ab -c $POOL_COUNT -n $MSG_COUNT -g $FILE_NAME http://${SERVER_IP}/
