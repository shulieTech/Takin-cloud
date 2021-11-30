/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_report' AND column_name = 'customer_id')
    THEN
        ALTER TABLE t_report
            ADD customer_id bigint(20) COMMENT '租户字段，customer_id,custom_id已废弃';
    END IF;
END $$
DELIMITER ;
CALL add_column;

update t_report
set customer_id = custom_id;


/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_manage' AND column_name = 'customer_id')
    THEN
        ALTER TABLE t_scene_manage
            ADD customer_id bigint(20) COMMENT '租户字段，customer_id,custom_id已废弃';
    END IF;
END $$
DELIMITER ;
CALL add_column;

update t_scene_manage
set customer_id = custom_id;

/* 更新字段长度 */
ALTER TABLE `trodb_cloud`.`t_scene_jmeterlog_upload`
    MODIFY COLUMN `upload_count` bigint(20) NULL DEFAULT 0 COMMENT '已上传文件大小' AFTER `modify_time`;

ALTER TABLE `trodb_cloud`.`t_warn_detail`
    MODIFY COLUMN `sla_name` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'SLA配置名称' AFTER `sla_id`;