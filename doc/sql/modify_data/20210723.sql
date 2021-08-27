DROP TABLE IF EXISTS `t_engine_plugin_files_ref`;
CREATE TABLE `t_engine_plugin_files_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) NOT NULL COMMENT '插件ID',
  `file_name` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '文件名称',
  `file_path` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '文件路径',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='引擎插件文件信息';


BEGIN;
INSERT INTO `t_engine_plugin_files_ref` VALUES (1, 1, 'jmeter-plugins-dubbo-all.jar', 'extras/jars/jmeter-plugins-dubbo-all.jar', '2021-01-29 13:43:59');
INSERT INTO `t_engine_plugin_files_ref` VALUES (2, 2, 'kafkameter-0.8.jar', '/config/jars/kafkameter-0.8.jar', '2021-01-29 13:44:48');
INSERT INTO `t_engine_plugin_files_ref` VALUES (3, 2, 'kafkameter-2.5.jar', '/config/jars/kafkameter-2.5.jar', '2021-07-12 09:52:40');
COMMIT;


DROP TABLE IF EXISTS `t_engine_plugin_info`;
CREATE TABLE `t_engine_plugin_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_type` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '插件类型',
  `plugin_name` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '插件名称',
  `gmt_create` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `gmt_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态  1 启用， 0 禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_pressure_engine_plugin_config_name` (`plugin_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件配置信息';


BEGIN;
INSERT INTO `t_engine_plugin_info` VALUES (1, 'dubbo', 'dubbo-all', '2021-01-06 14:43:58', '2021-01-06 14:43:58', 1);
INSERT INTO `t_engine_plugin_info` VALUES (2, 'kafka', 'kafka-all', '2021-01-06 14:44:33', '2021-01-06 14:44:33', 1);
COMMIT;

DROP TABLE IF EXISTS `t_engine_plugin_supported_versions`;
CREATE TABLE `t_engine_plugin_supported_versions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) DEFAULT NULL COMMENT '插件id',
  `supported_version` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '支持的版本',
  `file_ref_id` bigint(20) DEFAULT NULL COMMENT '文件id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_pressure_engine_support_version_config_id` (`plugin_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件支持的版本信息';


BEGIN;
INSERT INTO `t_engine_plugin_supported_versions` VALUES (1, 1, 'all', 1);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (2, 2, 'v0.8', 2);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (3, 2, 'v2.5', 3);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
