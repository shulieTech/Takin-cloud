#################################################################### TRO全链路压测系统 #############################################################

1.项目结构
  tro 父项目
   |_ _ _ tro-web
   |_ _ _ tro-entity
   |_ _ _ tro-common

2.项目说明
  该压测项目为springboot项目:
  ① tro-web为web项目,配置统一在resource下
  ② tro-entity为数据库操作jar;
  	 TRODataSourceConfig 该类扫描dao层,配置事务和SqlSessionTemplate。mapper文件放置在该项目的resource下面.
  ③ tro-common为项目公用jar;
  备注: tro-web
        tro-web项目内部分为5部分:
        ① io.shulie.takin.cloud.controller.authority     tro按钮权限控制(调用UUMS)
        ② io.shulie.takin.cloud.controller.confcenter 配置中心接口(1.应用管理接口 2.黑白名单管理接口 3.链路管理接口 4.一级链路管理 5.二级链路管理 6.数据字典)
        ③ io.shulie.takin.cloud.controller.health  TRO健康信息配置
        ④ io.shulie.takin.cloud.controller.monitor 压测监控接口管理
        ⑤ io.shulie.takin.cloud.controller.pmassist 压测辅助接口管理(文件上传下载,数据回传,MQ消费,MQ生产)
        ⑥ io.shulie.takin.cloud.controller.pressurecontrol 压测控制管理(数据构建,压测检测,压测总览,压测执行)
        ⑦ io.shulie.takin.cloud.controller.authority 菜单权限接口

3. 项目涉及系统
   1、 压测监控以及压测执行调用菜鸟接口(外部系统)
   2、 压测中的监控数据来源为AOPS系统(内部系统)
   3、 mq-pt-server(mq生产消息项目,内部系统)
   4、 mq-pt-client(mq消费消息项目,内部系统)

4. 项目版本控制
   git(分支为master和develop)
   代码提交到develop

5. 数据库
   测试:
      mysql:  10.32.112.161 端口:3306 用户名/密码:trodb/Sto@1234!      数据库名为:trodb

6. 中间件
   Redis使用情况
   ② 数据回传失败回馈机制

7. WEB服务器

8. 项目部署
   ① Jenkins打包,AOPS系统部署
   ② web服务器为Jboss
   说明: Jenkins打包git分支develop

9. 测试服务器
   ① 两台4核8G, 10.32.112.161:22
   ② 前端代码部署地址:/opt/nginx/html/tro; 访问地址: http://localhost/tro-web/login/login.action
      后端代码部署地址:/opt/apps/tro-web
