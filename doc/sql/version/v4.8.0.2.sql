-- 删除没用的表，容易产生误导
drop table t_scene;
drop table t_report_list;

ALTER TABLE `trodb_cloud`.`t_report_business_activity_detail`
    ADD COLUMN `avg_concurrence_num` decimal(10, 2) NULL COMMENT '平均并发数' AFTER `target_tps`;

ALTER TABLE `trodb_cloud`.`t_report`
    ADD COLUMN `pressure_type`  int(2)         NULL DEFAULT 0 COMMENT '施压类型,0:并发,1:tps,2:自定义;不填默认为0' AFTER `total_request`,
    ADD COLUMN `avg_concurrent` decimal(10, 2) NULL COMMENT '平均线程数' AFTER `pressure_type`,
    ADD COLUMN `tps`            integer(0)     NULL COMMENT '目标tps' AFTER `avg_concurrent`;

UPDATE `trodb_cloud`.`t_strategy_config`
SET `strategy_config` = '{\n \"threadNum\":\"80\",\n \"cpuNum\":\"2\",\n \"memorySize\":\"3072\",\n \"tpsNum\":\"5000\"\n}'
WHERE `id` = 1;