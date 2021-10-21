# Takin-cloud

1. 怎么打包
- 1. mvn clean install -Dmaven.test.skip
- 2. takin-cloud-app target目录下为主工程包
- 3. 可以使用java -jar 启动主工程

2. 带插件启动
- 1. 在takin-cloud-plugins下面的包目前是插件工程，从target里面找到对应的插件包
- 2. 在主工程当前位置新建一个目录./plugins，将插件包移入该目录（也可以选择改配置替换掉插件目录）
- 3. 然后使用java -jar 启动主工程 会带插件启动

   

3. 使用压测引擎
- 1. 在主工程当前位置新建一个目录./engine，将压测引擎包放入其中，压测引擎名称为pressure-engine.tar.gz
- 2. 这步操作不需要重启takin-cloud
- 3. 启动压测任务会调用到其中的压测引擎包

4. 常用配置修改
- 1. 修改application-local.yml内容（必改）
- 2. application.yml中mysql/redis/influx等的用户名和密码（没变可以不改）
- 3. script.temp.path 和 script.path 默认在/data下面；（如果需要修改可以改；script.temp.path需要和takin-web中配置保持一致）

5.swagger文档地址
http://localhost:10010/takin-cloud/doc.html

6.修改版本号
- 1. 全局修改 如 takin-cloud-5.0.2 版本号
- 2. 如果涉及到takin-cloud-open模块修改，需要修改 takin-cloud-open-5.0.0 版本号，并进行deploy
