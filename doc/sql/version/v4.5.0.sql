-- 场景添加扩展字段
alter table t_scene_manage
    add COLUMN `features` text DEFAULT NULL COMMENT '扩展字段';
-- 压测场景添加负责人字段
ALTER TABLE t_scene_manage
    ADD COLUMN `user_id` bigint(20) DEFAULT NULL COMMENT '负责人id' AFTER id;
ALTER TABLE t_scene_manage
    ADD COLUMN `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id';
-- 压测报表添加负责人字段
ALTER TABLE t_report
    ADD COLUMN `user_id` bigint(20) DEFAULT NULL COMMENT '负责人id' AFTER id;
ALTER TABLE t_report
    ADD COLUMN `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id';
