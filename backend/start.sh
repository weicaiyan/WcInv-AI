#!/usr/bin/env bash
set -a
source /c/Users/17890/code/WcInv/.env.local
set +a
cd /c/Users/17890/code/WcInv/backend
mvn spring-boot:run -pl wcinv-api -q
