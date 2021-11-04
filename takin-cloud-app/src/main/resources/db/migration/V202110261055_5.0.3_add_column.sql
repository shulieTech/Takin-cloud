/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_script_ref' AND column_name = 'file_md5')
    THEN
        ALTER TABLE t_scene_script_ref
            ADD script_identification VARCHAR(1024) COMMENT ' 文件MD5值 ';
    END IF;

END $$
DELIMITER ;
CALL add_column;
