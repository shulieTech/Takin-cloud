alter table trodb.t_scene_manage
    add `custom_id` bigint default null comment '客户id' before `is_deleted`;

alter table trodb.t_report
    add `custom_id` bigint default null comment '客户id' before `is_deleted`;

CREATE TABLE `t_ac_account`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid`        bigint(20)  DEFAULT NULL COMMENT '用户ID',
    `status`     tinyint(4)  DEFAULT NULL COMMENT '状态',
    `is_deleted` tinyint(1)  DEFAULT '0' COMMENT '是否删除',
    `tags`       bigint(20)  DEFAULT '0' COMMENT '标签',
    `features`   longtext COMMENT '扩展字段',
    `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `IDX_ACCOUNT_UID` (`uid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='账户表';

-- ----------------------------
-- Table structure for t_ac_account_balance
-- ----------------------------
CREATE TABLE `t_ac_account_balance`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `acc_id`         bigint(20)     DEFAULT NULL COMMENT '账户ID（外键）',
    `book_id`        bigint(20)     DEFAULT NULL COMMENT '账本ID（外键）',
    `amount`         decimal(25, 5) DEFAULT '0.00000' COMMENT '当前发生金额',
    `balance`        decimal(25, 5) DEFAULT '0.00000' COMMENT '可用余额',
    `lock_balance`   decimal(25, 5) DEFAULT '0.00000' COMMENT '冻结余额',
    `subject`        int(11)        DEFAULT NULL COMMENT '账本科目',
    `direct`         tinyint(4)     DEFAULT NULL COMMENT '记账方向',
    `remark`         varchar(200)   DEFAULT NULL COMMENT '备注',
    `parent_book_id` bigint(20)     DEFAULT '0' COMMENT '父类ID',
    `scene_code`     varchar(30)    DEFAULT NULL COMMENT '业务代码',
    `status`         tinyint(4)     DEFAULT NULL COMMENT '状态',
    `acc_time`       datetime       DEFAULT NULL COMMENT '记账时间',
    `outer_id`       varchar(100)   DEFAULT NULL COMMENT '外部交易资金流水NO',
    `is_deleted`     tinyint(1)     DEFAULT '0' COMMENT '是否删除',
    `tags`           bigint(20)     DEFAULT '0' COMMENT '标签',
    `features`       longtext COMMENT '扩展字段',
    `gmt_create`     datetime(3)    DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_update`     datetime(3)    DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ACCOUNT_BALANCE_BOOK_ID_OUTER_ID_SCENE_CODE` (`book_id`, `outer_id`, `scene_code`),
    KEY `IDX_ACCOUNT_BALANCE_ACC_ID_BOOK_ID` (`acc_id`, `book_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='账户账本明细表';

-- ----------------------------
-- Table structure for t_ac_account_book
-- ----------------------------
CREATE TABLE `t_ac_account_book`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid`            bigint(20)          DEFAULT NULL COMMENT '用户ID（外键）',
    `acc_id`         bigint(20)          DEFAULT NULL COMMENT '账户ID（外键）',
    `parent_book_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父类ID',
    `balance`        decimal(25, 5)      DEFAULT '0.00000' COMMENT '余额',
    `lock_balance`   decimal(25, 5)      DEFAULT '0.00000' COMMENT '冻结金额',
    `total_balance`  decimal(25, 5)      DEFAULT '0.00000' COMMENT '总金额',
    `subject`        int(11)             DEFAULT NULL COMMENT '科目',
    `direct`         tinyint(4)          DEFAULT NULL COMMENT '记账方向，借或贷',
    `rule`           varchar(500)        DEFAULT NULL COMMENT '规则',
    `rule_balance`   decimal(25, 5)      DEFAULT '0.00000' COMMENT '规则余额',
    `start_time`     datetime(3)         DEFAULT NULL COMMENT '生效时间',
    `end_time`       datetime(3)         DEFAULT NULL COMMENT '失效时间',
    `status`         tinyint(4)          DEFAULT NULL COMMENT '状态',
    `version`        int(10)             DEFAULT NULL COMMENT '版本',
    `is_deleted`     tinyint(1)          DEFAULT '0' COMMENT '是否删除',
    `tags`           bigint(20)          DEFAULT '0' COMMENT '标签',
    `features`       longtext COMMENT '扩展字段',
    `gmt_create`     datetime(3)         DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_update`     datetime(3)         DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `IDX_ACCOUNT_BOOK_ACC_ID` (`acc_id`),
    KEY `IDX_ACCOUNT_BOOK_UID` (`uid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='账户账本表';
