DROP TABLE IF EXISTS `t_scene_big_file_slice`;
CREATE TABLE `t_scene_big_file_slice` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `scene_id` bigint(20) DEFAULT NULL COMMENT '场景ID',
                                          `script_ref_id` bigint(20) DEFAULT NULL COMMENT '脚本ID',
                                          `file_path` varchar(2000) DEFAULT NULL COMMENT '文件路径',
                                          `file_name` varchar(1000) DEFAULT NULL COMMENT '文件名',
                                          `slice_count` int(20) DEFAULT NULL COMMENT '分片数量',
                                          `slice_info` text COMMENT '文件分片信息',
                                          `status` int(1) DEFAULT NULL COMMENT '状态：0-未分片；1-已分片；2-文件已更改',
                                          `file_update_time` timestamp(3) NULL DEFAULT NULL COMMENT '分片时文件最后更改时间',
                                          `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '分片时间',
                                          `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
