SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_engine_plugin_files_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_files_ref`;
CREATE TABLE `t_engine_plugin_files_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) NOT NULL COMMENT '插件ID',
  `file_name` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '文件名称',
  `file_path` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '文件路径',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='引擎插件文件信息';

-- ----------------------------
-- Records of t_engine_plugin_files_ref
-- ----------------------------
BEGIN;
INSERT INTO `t_engine_plugin_files_ref` VALUES (1, 1, 'jmeter-plugins-dubbo-all.jar', '/engine/plugins/dubbo/dubbo-all/jmeter-plugins-dubbo-all.jar', '2021-01-29 13:43:59');
INSERT INTO `t_engine_plugin_files_ref` VALUES (2, 2, 'jmeter-plugins-kafka-all.jar', '/engine/plugins/kafka/kafka-all/jmeter-plugins-kafka-all.jar', '2021-01-29 13:44:48');
COMMIT;

-- ----------------------------
-- Table structure for t_engine_plugin_info
-- ----------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件配置信息';

-- ----------------------------
-- Records of t_engine_plugin_info
-- ----------------------------
BEGIN;
INSERT INTO `t_engine_plugin_info` VALUES (1, 'dubbo', 'dubbo-all', '2021-01-06 14:43:58', '2021-01-06 14:43:58', 1);
INSERT INTO `t_engine_plugin_info` VALUES (2, 'kafka', 'kafka-all', '2021-01-06 14:44:33', '2021-01-06 14:44:33', 1);
COMMIT;

-- ----------------------------
-- Table structure for t_engine_plugin_supported_versions
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_supported_versions`;
CREATE TABLE `t_engine_plugin_supported_versions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) DEFAULT NULL COMMENT '插件id',
  `supported_version` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '支持的版本',
  PRIMARY KEY (`id`),
  KEY `index_pressure_engine_support_version_config_id` (`plugin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件支持的版本信息';

-- ----------------------------
-- Records of t_engine_plugin_supported_versions
-- ----------------------------
BEGIN;
INSERT INTO `t_engine_plugin_supported_versions` VALUES (1, 1, 'all');
INSERT INTO `t_engine_plugin_supported_versions` VALUES (2, 2, 'all');
COMMIT;

-- ----------------------------
-- Table structure for t_schedule_record_engine_plugins_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_schedule_record_engine_plugins_ref`;
CREATE TABLE `t_schedule_record_engine_plugins_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `schedule_record_id` bigint(20) NOT NULL COMMENT '调度记录ID',
  `engine_plugin_file_path` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '引擎插件存放文件夹',
  `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='调度记录引擎插件信息';

-- ----------------------------
-- Records of t_schedule_record_engine_plugins_ref
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
