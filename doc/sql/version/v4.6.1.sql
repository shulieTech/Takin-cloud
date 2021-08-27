-- 新增字段 t_report script_id,脚本id
ALTER TABLE `t_report` ADD COLUMN `script_id` bigint(20) DEFAULT NULL  COMMENT '脚本id';
-- 添加索引
alter table `t_report`
    ADD INDEX idx_script_id ( `script_id` );
-- 数据纠正脚本，用于纠正压测统计
update t_report c set c.script_id = (
SELECT t.scriptId  FROM (SELECT a.id,a.script_id,b.features ->> '$.scriptId' as scriptId FROM t_report a,t_scene_manage b
WHERE a.scene_id =b.id)t  WHERE c.id = t.id)