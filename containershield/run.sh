#!/bin/bash

set -e

echo "--------------------------------------------"
echo "Configuring ContainerShield dependencies !!!"
echo "--------------------------------------------"

docker compose up -d

cleanup() {
    echo "--------------------------------------------"
    echo "Stopping ContainerShield dependencies !!!"
    echo "--------------------------------------------"
    docker compose down
}

trap cleanup EXIT INT TERM

echo "--------------------------------------------"
echo "Starting ContainerShield (Spring Boot) !!!"
echo "--------------------------------------------"

./mvnw spring-boot:run
