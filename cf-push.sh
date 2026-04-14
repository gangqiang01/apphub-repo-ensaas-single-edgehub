#!/bin/bash
cf push -f manifest.yml  --no-start
cf bind-service AndroidAstore-Server  jxpostgres  -c '{"group":"androidlink"}'
cf restart AndroidAstore-Server
