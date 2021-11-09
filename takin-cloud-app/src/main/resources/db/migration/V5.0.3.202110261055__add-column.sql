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
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_report' AND column_name = 'script_analysis_result')
    THEN
        ALTER TABLE `t_report`
            ADD `script_analysis_result` json COMMENT ' 脚本节点树 ' AFTER  `features`;
    END IF;

    /* t_report 表添加 amount_lock_id 字段*/
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_report' AND column_name = 'amount_lock_id')
    THEN
        ALTER TABLE `t_report`
            ADD `amount_lock_id` VARCHAR(100) COMMENT ' 流量计算关联ID ' AFTER  `features`;
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

END $$
DELIMITER ;
CALL add_column;
