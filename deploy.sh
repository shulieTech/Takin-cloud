#!/bin/bash
# 获取工作目录
DIRNAME=$(dirname "$0")
PATH=$(cd "$DIRNAME" || pwd)
# 进入工作目录
cd "$PATH" || exit
# maven打包
mvn clean deploy \
  -pl constant,model,sdk \
  -D"altDeploymentRepository"=rdc-releases::default::https://packages.aliyun.com/maven/repository/2101190-release-xxuvBf/
