#!/bin/bash
DIRNAME=$(dirname "$0")
PATH=$(
  cd "$DIRNAME" || exit
  pwd
)
cd "$PATH" || exit
mvn clean deploy \
  -pl constant,model,sdk \
  -D"altDeploymentRepository"=rdc-releases::default::https://packages.aliyun.com/maven/repository/2101190-release-xxuvBf/
