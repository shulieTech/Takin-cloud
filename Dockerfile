FROM swr.cn-east-3.myhuaweicloud.com/shulie-hangzhou/openjdk:8-jdk-alpine
RUN apk update && apk add nfs-utils
WORKDIR /data/takin-cloud
RUN cd / && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ADD https://install-pkg.oss-cn-hangzhou.aliyuncs.com/takin-cloud/lib.zip /data/takin-cloud
COPY  target/takin-cloud-app*.jar  /data/takin-cloud/takin-cloud.jar
ENTRYPOINT ["java","-jar","takin-cloud.jar"]