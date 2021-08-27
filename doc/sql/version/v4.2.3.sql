alter table t_tro_user
    add column `version` varchar(10) default null comment '客户端版本号 4.2.1' after `role`;

INSERT INTO `t_strategy_config`(`id`, `strategy_name`, `strategy_config`, `status`, `is_deleted`, `create_time`,
                                `update_time`)
VALUES (1, '基础版策略', '{\n \"threadNum\":\"3000\",\n \"cpuNum\":\"4\",\n \"memorySize\":\"8096\"\n}', 0, 0,
        '2020-05-09 21:06:16.889', '2020-08-06 11:53:56.725');

INSERT INTO `t_tro_user`(`id`, `name`, `nick`, `key`, `salt`, `password`, `status`, `model`, `role`, `version`,
                         `is_delete`, `gmt_create`, `gmt_update`)
VALUES (9773, 'testuser', 'testuser', '5b06060a-17cb-4588-bb71-edd7f65035af', '$2a$10$FEqFXLL1KVVjnZKjV60Pwe',
        '$2a$10$FEqFXLL1KVVjnZKjV60Pwe2qMtCAsJZeziHMlVTy5XIVsW7HzQ7ze', 0, 1, 2, '4.2.3', 0, '2020-08-30 14:00:56',
        '2020-08-30 14:00:56');
INSERT INTO `t_ac_account`(`id`, `uid`, `status`, `is_deleted`, `tags`, `features`, `gmt_create`, `gmt_update`)
VALUES (102, 9773, NULL, 0, 0, NULL, '2020-08-30 14:00:56.680', '2020-08-30 14:00:56.680');
INSERT INTO `t_ac_account_book`(`id`, `uid`, `acc_id`, `parent_book_id`, `balance`, `lock_balance`, `total_balance`,
                                `subject`, `direct`, `rule`, `rule_balance`, `start_time`, `end_time`, `status`,
                                `version`, `is_deleted`, `tags`, `features`, `gmt_create`, `gmt_update`)
VALUES (102, 9773, 102, 0, 100000.00000, 0.00000, 100000.00000, NULL, NULL, NULL, 0.00000, NULL, NULL, NULL, NULL, 0, 0,
        NULL, '2020-08-30 14:00:56.764', '2020-08-30 14:00:56.764');
INSERT INTO `t_ac_account_balance`(`acc_id`, `book_id`, `amount`, `balance`, `lock_balance`, `subject`, `direct`,
                                   `remark`, `parent_book_id`, `scene_code`, `status`, `acc_time`, `outer_id`,
                                   `is_deleted`, `tags`, `features`, `gmt_create`, `gmt_update`)
VALUES (102, 102, 100000.00000, 100000.00000, 0.00000, NULL, 0, NULL, 0, 'RECHARGE', NULL, NULL, NULL, 0, 0,
        '{\"cashAmount\":100}', '2020-08-30 14:04:39.687', '2020-08-30 14:04:39.687');
