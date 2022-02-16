/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
    /* t_scene_script_ref 表添加 file_md5 字段*/
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_script_ref' AND column_name = 'file_md5')
    THEN
        ALTER TABLE `t_scene_script_ref`
            ADD `file_md5` VARCHAR(1024) COMMENT ' 文件MD5值 ' AFTER `upload_path`;
    END IF;

    /* t_report 表添加 script_analysis_result 字段*/
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_report' AND column_name = 'script_node_tree')
    THEN
        ALTER TABLE `t_report`
            ADD `script_node_tree` json COMMENT ' 脚本节点树 ' AFTER `features`;
    END IF;

    /* t_report 表添加 amount_lock_id 字段*/
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_report' AND column_name = 'amount_lock_id')
    THEN
        ALTER TABLE `t_report`
            ADD `amount_lock_id` VARCHAR(100) COMMENT ' 流量计算关联ID ' AFTER `features`;
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_warn_detail' AND column_name = 'bind_ref')
    THEN
        ALTER TABLE `t_warn_detail`
            ADD `bind_ref` VARCHAR(100) COMMENT ' 流量计算关联ID ' AFTER `business_activity_name`;
    END IF;

    /* t_scene_manage 表添加 script_analysis_result 字段*/
    IF
        NOT EXISTS(SELECT *
                   FROM information_schema.columns
                   WHERE table_schema = DATABASE()
                     AND table_name = 't_scene_manage'
                     AND column_name = 'script_analysis_result')
    THEN
        ALTER TABLE `t_scene_manage`
            ADD COLUMN `script_analysis_result` JSON NULL COMMENT '脚本解析结果' AFTER `features`;
    END IF;

    ALTER TABLE `t_ac_account_balance`
        MODIFY COLUMN `resource_name` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称';

END $$
DELIMITER ;
CALL add_column;
