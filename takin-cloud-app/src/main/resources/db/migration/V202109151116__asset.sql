ALTER TABLE `t_ac_account_balance`
ADD COLUMN `resource_type` tinyint(4) NULL COMMENT '数据来源,1=压测报告、2=业务活动流量验证、3=脚本调试' AFTER `gmt_update`,
ADD COLUMN `resource_id` bigint(20) NULL COMMENT '来源ID。压测报告取报告ID、流量验证取业务活动ID、脚本调试取脚本ID' AFTER `resource_type`,
ADD COLUMN `resource_name` varchar(20) NULL COMMENT '来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称' AFTER `resource_id`,
ADD COLUMN `creator_id` bigint(20) NULL COMMENT '创建者' AFTER `resource_name`;

ALTER TABLE `trodb_cloud`.`t_ac_account_balance`
ADD INDEX `IDX_ACCOUNT_BALANCE_OUTER_ID`(`outer_id`) USING BTREE;