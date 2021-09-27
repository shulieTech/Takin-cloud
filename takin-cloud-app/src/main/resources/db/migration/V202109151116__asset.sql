DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
IF
NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_ac_account_balance' AND column_name='resource_type')
	THEN
ALTER TABLE `t_ac_account_balance`
    ADD COLUMN `resource_type` tinyint(4) NULL COMMENT '数据来源,1=压测报告、2=业务活动流量验证、3=脚本调试' AFTER `gmt_update`;
END IF;

IF
NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_ac_account_balance' AND column_name='resource_id')
	THEN
ALTER TABLE `t_ac_account_balance`
    ADD COLUMN `resource_id` bigint(20) NULL COMMENT '来源ID。压测报告取报告ID、流量验证取业务活动ID、脚本调试取脚本ID' AFTER `resource_type`;
END IF;

IF
NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_ac_account_balance' AND column_name='resource_name')
	THEN
ALTER TABLE `t_ac_account_balance`
    ADD COLUMN `resource_name` varchar(20) NULL COMMENT '来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称' AFTER `resource_id`;
END IF;

IF
NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_ac_account_balance' AND column_name='creator_id')
	THEN
ALTER TABLE `t_ac_account_balance`
    ADD COLUMN `creator_id` bigint(20) NULL COMMENT '创建者' AFTER `resource_name`;
END IF;

IF
NOT EXISTS(SELECT * FROM  information_schema.STATISTICS WHERE table_schema= DATABASE() AND TABLE_NAME='t_ac_account_balance' and INDEX_NAME='IDX_ACCOUNT_BALANCE_OUTER_ID')
	THEN
ALTER TABLE `trodb_cloud`.`t_ac_account_balance`
    ADD INDEX `IDX_ACCOUNT_BALANCE_OUTER_ID`(`outer_id`) USING BTREE;
END IF;

END $$
DELIMITER ;
CALL add_column;

