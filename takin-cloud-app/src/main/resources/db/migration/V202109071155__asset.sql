/* 修正字段 t_ac_account_uid */
DELIMITER $$
DROP PROCEDURE IF EXISTS `update_column_t_ac_account_uid` $$
CREATE PROCEDURE update_column_t_ac_account_uid()
BEGIN
    IF EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_ac_account' AND column_name = 'uid')
    THEN
        ALTER TABLE `t_ac_account`
            CHANGE uid customer_id bigint(20) COMMENT '租户主键';
    END IF;
END
$$
# 执行并删除
DELIMITER ;
CALL update_column_t_ac_account_uid;
DROP PROCEDURE IF EXISTS `update_column_t_ac_account_uid`;

/* 添加字段 t_ac_account_book_uid */
DELIMITER $$
DROP PROCEDURE IF EXISTS `update_column_t_ac_account_book_uid` $$
CREATE PROCEDURE update_column_t_ac_account_book_uid()
BEGIN
    IF EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_ac_account_book' AND column_name = 'uid')
    THEN
        ALTER TABLE `t_ac_account_book`
            CHANGE uid customer_id bigint(20) COMMENT '租户主键';
    END IF;
END
$$
# 执行并删除
DELIMITER ;
CALL update_column_t_ac_account_book_uid;
DROP PROCEDURE IF EXISTS `update_column_t_ac_account_book_uid`;




