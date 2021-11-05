DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
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

