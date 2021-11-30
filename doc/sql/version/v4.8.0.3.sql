-- 流量调试版本

ALTER TABLE `trodb_cloud`.`t_scene_manage`
    ADD COLUMN `type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '场景类型:0普通场景，1流量调试' AFTER `pt_config`;

ALTER TABLE `trodb_cloud`.`t_report`
    ADD COLUMN `type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '报告类型；0普通场景，1流量调试' AFTER `status`;