# Takin-cloud
1.怎么打包
1.1mvn clean install -Dmaven.test.skip；
1.2takin-cloud-app target目录下为主工程包；
1.3可以使用java -jar 启动主工程；

2.带插件启动
2.1在takin-cloud-plugins下面的包目前是插件工程，从target里面找到对应的插件包;
2.2在主工程当前位置新建一个目录./plugins，将插件包移入该目录（也可以选择改配置替换掉插件目录）;
2.3然后使用java -jar 启动主工程 会带插件启动;

3.使用压测引擎
3.1在主工程当前位置新建一个目录./engine，将压测引擎包放入其中，压测引擎名称为pressure-engine.tar.gz;
3.2这步操作不需要重启takin-cloud;
3.3启动压测任务会调用到其中的压测引擎包;

4.常用配置修改
4.1 修改application-local.yml内容（必改）;
4.2 application.yml中mysql/redis/influx等的用户名和密码（没变可以不改）;
4.3 script.temp.path 和 script.path 默认在/data下面；（如果需要修改可以改；script.temp.path需要和takin-web中配置保持一致）;