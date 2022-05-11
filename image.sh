#!/bin/bash
#cloud分支5.2.7
#cloud-ee分支1.0.5
#pressure-engine分支5.2.4
#jmeter分支5.2.4

source /etc/profile
source ~/.bash_profile

log() {
    echo -e "\033[40;37m$DATE $1\033[0m"
}
# 镜像tag
IMAGE_TAG=""
# 基础镜像md5
BASIC_VERSION=""

# 读取参数
while getopts ':t:v:' opt
do
  case $opt in
  t)
    IMAGE_TAG=$OPTARG
    ;;
  v)
    BASIC_VERSION=$OPTARG
    ;;
  ?)
    echo "未知参数"
    ;;
  esac
done
echo $IMAGE_TAG
if [ -z "$IMAGE_TAG" ]; then
  echo '缺少 -t .'
  exit 1
fi

CURRENT_DIR=$(cd "$(dirname "$0")"; pwd)
CURRENT_USER=`whoami`
#cloud项目地址
CLOUD_WORK_DIR=$CURRENT_DIR
#cloud-ee项目地址
CLOUD_EE_WORK_DIR=$CURRENT_DIR/../takin-ee-cloud
#pressure-engine项目地址
PRESSURE_ENGINE_WORK_DIR=$CURRENT_DIR/../Takin-pressure-engine
#镜像临时目录
IMAGE_TEMP_DIR=$CURRENT_DIR/tmp/image
# docker 名称
DOCKER_NAME=forcecop/takin-cloud
# 制品地址
BUILD_DIR=$IMAGE_TEMP_DIR/.build

log ' >>> 创建临时目录 <<< '
rm -rf $IMAGE_TEMP_DIR
mkdir -p $IMAGE_TEMP_DIR

#如已经打完相关包可以不执行一下操作 只需将包放入 $IMAGE_TEMP_DIR
##########################################

log ' >>> cloud 打包 <<< '
cd $CLOUD_WORK_DIR/takin-cloud-app
echo `pwd`
mvn clean package -D"maven.test.skip"=true

log ' >>> cloud-ee 打包 <<< '
cd $CLOUD_EE_WORK_DIR
#删除旧的jar
rm -rf build/build/*.jar
echo `pwd`
mvn clean package -D"maven.test.skip"=true

log ' >>> pressure-engine 打包 <<< '
cd $PRESSURE_ENGINE_WORK_DIR/build/cmd
echo `pwd`
sh tar.sh



log ' >>> 移动项目包到临时目录 <<< '
#创建plugins目录
mkdir $IMAGE_TEMP_DIR/plugins
#cloud
cp $CLOUD_WORK_DIR/takin-cloud-app/target/*.jar $IMAGE_TEMP_DIR/
cp $CLOUD_WORK_DIR/takin-cloud-plugins/plugin-engine-module/target/plugin-engine-module-*.jar $IMAGE_TEMP_DIR/plugins/
cp $CLOUD_WORK_DIR/takin-cloud-plugins/plugin-engine_call-module/target/plugin-engine_call-module-*.jar $IMAGE_TEMP_DIR/plugins/
#cloud-ee
cp $CLOUD_EE_WORK_DIR/build/build/*.jar $IMAGE_TEMP_DIR/plugins/
#engine
cp $PRESSURE_ENGINE_WORK_DIR/build/target/pressure-engine.tar.gz $IMAGE_TEMP_DIR/

##########################################

log ' >>> 准备Dockerfile文件 <<< '
rm -rf $BUILD_DIR
mkdir $BUILD_DIR

cd $IMAGE_TEMP_DIR
touch Dockerfile
DOCKER_FILE=$BUILD_DIR/Dockerfile
echo $DOCKER_FILE
echo "FROM geekidea/alpine-a:3.9
MAINTAINER shulie <dev@shulie.io>
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \\
          apk update && \\
          apk add openjdk8 && \\
          apk add --no-cache curl && \\
          apk add --no-cache tzdata && \\
          cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \\
          echo \"Asia/Shanghai\" > /etc/timezone
ENV TZ Asia/Shanghai" > $DOCKER_FILE

echo "COPY takin-cloud-app-*.jar /home/opt/flpt/takin-cloud/takin-cloud-app.jar
COPY plugins/* /home/opt/flpt/takin-cloud/plugins/
COPY pressure-engine.tar.gz /home/opt/flpt/pressure-engine/pressure-engine.tar.gz
EXPOSE 10010
ENTRYPOINT [\"sh\", \"-c\", \"java \$JAVA_OPTS -jar /home/opt/flpt/takin-cloud/takin-cloud-app.jar  \"]" >> $DOCKER_FILE
#ENTRYPOINT [\"sh\", \"-c\" \" \$DYNAMIC_CMD \"]" >> $DOCKER_FILE

log ' >>> 制作镜像 <<< '
cp takin-cloud-app-*.jar $BUILD_DIR
cp -r plugins $BUILD_DIR
cp pressure-engine.tar.gz $BUILD_DIR
echo "docker build --platform linux/amd64 -f $DOCKER_FILE -t $DOCKER_NAME:$IMAGE_TAG $BUILD_DIR"
docker build --platform linux/amd64 -f $DOCKER_FILE -t $DOCKER_NAME:$IMAGE_TAG $BUILD_DIR

echo ' >>> 导出docker镜像 <<< '
docker save \
-o "$BUILD_DIR"/takin-cloud-"$IMAGE_TAG".tar \
"$DOCKER_NAME":"$IMAGE_TAG"

echo ' >>> 其他镜像操作 <<< '
HARBOR_IP=192.168.1.89
echo 'tag : docker tag forcecop/takin-cloud:'$IMAGE_TAG $HARBOR_IP'/library/takin-cloud:'$IMAGE_TAG
echo '保存到本地：docker save -o takin-cloud-'$IMAGE_TAG'.tar forcecop/takin-cloud:'$IMAGE_TAG
echo 'push : '
echo '     docker login '$HARBOR_IP
echo '     docker push '$HARBOR_IP'/library/takin-cloud:'$IMAGE_TAG
echo '删除： docker rmi '$imageId' --force'
echo '删除镜像和tag： ./deleteTag.sh -t '$IMAGE_TAG

#rm -f "${DOCKER_FILE}"