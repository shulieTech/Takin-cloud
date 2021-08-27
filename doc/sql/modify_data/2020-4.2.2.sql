-- create table `t_report_rt_distribute`(
--     `id` bigint(20) NOT NULL AUTO_INCREMENT,
--     `scene_id` bigint(20) NOT NULL COMMENT '场景id',
--     `report_id` bigint(20) NOT NULL COMMENT '报告id',
--     `scene_business_activity_id` bigint(20) NOT NULL COMMENT '场景业务活动关联id',
--     `business_activity_id` bigint(20) NOT NULL COMMENT '业务活动id',
--     `business_activity_name` varchar(64) NOT NULL COMMENT '业务活动名称',
--     `bind_ref` varchar(64) NOT NULL COMMENT '绑定关系',
--     `distribute_value` varchar(512) NOT NULL COMMENT '分布范围，格式json',
--     `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
--     PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='RT分布表';


alter table t_report_business_activity_detail
    add column `target_tps` decimal(10, 2) default null comment '目标tps' after `tps`;
alter table t_report_business_activity_detail
    add column `target_rt` decimal(10, 2) default null comment '目标rt' after `rt`;
alter table t_report_business_activity_detail
    add column `target_success_rate` decimal(10, 2) default null comment '目标成功率' after `success_rate`;
alter table t_report_business_activity_detail
    add column `target_sa` decimal(10, 2) default null comment '目标sa' after `sa`;

alter table t_report_business_activity_detail
    add column `application_ids` varchar(1024) default null comment '应用ID' after `business_activity_name`;
alter table t_report_business_activity_detail
    add column `bind_ref` varchar(64) default null comment '绑定关系' after `business_activity_name`;
alter table t_report_business_activity_detail
    add column `rt_distribute` varchar(512) default null comment '分布范围，格式json' after `target_rt`;

alter table t_report_business_activity_detail
    add column `pass_flag` tinyint default null comment '是否通过' after `min_rt`;
update t_report_business_activity_detail a ,t_scene_business_activity_ref b
set a.bind_ref=b.bind_ref,
    a.application_ids=b.application_ids
where a.scene_id = b.scene_id
  and a.business_activity_id = b.business_activity_id;
