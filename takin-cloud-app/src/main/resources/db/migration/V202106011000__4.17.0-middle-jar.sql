CREATE TABLE IF NOT EXISTS `t_middleware_jar` (
                                    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                    `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
                                    `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
                                    `status` tinyint(3) unsigned DEFAULT '0' COMMENT '支持的包状态, 1 已支持, 2 待支持, 3 无需支持, 4 待验证',
                                    `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
                                    `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
                                    `version` varchar(30) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件版本',
                                    `agv` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId_version, 做唯一标识,',
                                    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_agv` (`agv`) USING BTREE,
                                    KEY `idx_artifact_id` (`artifact_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件包表'