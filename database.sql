# \n\s+?foreign key
# \n\s+?comment
# \n\s+?primary key

create table if not exists t_callback
(
    id             bigint auto_increment comment '主键' primary key,
    url            varchar(1000)                        not null comment '回调路径',
    context        blob                                 not null comment '回调内容',
    create_time    timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    completed      tinyint(1) default 0                 not null comment '是否完成',
    threshold_time timestamp                            null comment '阈值时间'
) comment '回调表';

create table if not exists t_callback_log
(
    id            bigint auto_increment comment '主键' primary key,
    callback_id   bigint                               not null comment '回调主键',
    request_url   varchar(1000)                        not null comment '请求路径',
    request_data  blob                                 not null comment '请求数据',
    request_time  timestamp  default CURRENT_TIMESTAMP not null comment '请求时间',
    response_data blob                                 null comment '响应数据',
    response_time datetime                             null comment '响应时间',
    completed     tinyint(1) default 0                 not null comment '是否完成',
    constraint t_callback_log_t_callback_id_fk foreign key (callback_id) references t_callback (id)
) comment '回调日志';

create table if not exists t_file
(
    id           bigint auto_increment comment '主键' primary key,
    callback_url varchar(255) null comment '回调地址'
) comment '文件';

create table if not exists t_script_verification
(
    id         int auto_increment comment '主键' primary key,
    content    json                               not null comment '任务内容',
    start_time datetime default CURRENT_TIMESTAMP not null comment '开始时间',
    completed  tinyint(1)                         null comment '是否完成',
    message    varchar(255)                       null comment '执行结果',
    end_time   datetime                           null comment '结束时间'
) comment '脚本校验任务';

create table if not exists t_watchman
(
    id         bigint auto_increment comment '主键' primary key,
    ref        varchar(512)            not null comment '关键词',
    ref_sign   varchar(255)            not null comment '关键词签名',
    public_key varchar(512) default '' not null comment '数据加密的公钥',
    constraint t_watchman_ref_sign_uindex unique (ref_sign)
) comment '调度器';

create table if not exists t_command
(
    id          bigint auto_increment comment '主键' primary key,
    watchman_id bigint                              not null comment '调度主键',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    type        int                                 not null comment '命令类型',
    content     json                                not null comment '命令内容',
    ack_time    datetime                            null comment '命令确认时间',
    ack_content json                                null comment '命令确认内容',
    constraint t_command_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id)
) comment '命令表';

create table if not exists t_file_example
(
    id            bigint auto_increment comment '主键' primary key,
    file_id       bigint                not null comment '文件主键',
    watchman_id   bigint                not null comment '调度器主键',
    path          varchar(255)          not null comment '文件路径',
    sign          varchar(255)          not null comment '文件摘要(MD5)',
    download_url  varchar(255)          not null comment '下载地址',
    complete_size bigint     default 0  not null comment '完成的大小',
    total_size    bigint     default -1 not null comment '总大小',
    completed     tinyint(1) default 0  not null comment '已完成',
    message       varchar(255)          null comment '消息',
    constraint t_file_example_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id),
    constraint t_file_manage_list_t_file_manage_id_fk foreign key (file_id) references t_file (id)
) comment '文件实例';

create table if not exists t_resource
(
    id           bigint auto_increment comment '主键' primary key,
    watchman_id  bigint                              not null comment '调度器主键',
    number       int                                 not null comment '需要的数量',
    cpu          varchar(255)                        not null comment '需要的CPU',
    memory       varchar(255)                        not null comment '需要的内存',
    limit_cpu    varchar(255)                        not null comment '限定的CPU',
    limit_memory varchar(255)                        not null comment '限定的内存',
    create_time  timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    callback_url varchar(512)                        not null comment '状态回调接口路径',
    image        varchar(1024)                       null comment '资源镜像信息',
    constraint t_resource_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id)
) comment '资源表';

create table if not exists t_pressure
(
    id                      bigint auto_increment comment '主键' primary key,
    name                    varchar(255)  not null comment '任务名称',
    resource_id             bigint        not null comment '资源主键',
    duration                bigint        not null comment '持续时间',
    sampling                int           not null comment '采样率',
    type                    int           not null comment '任务的运行模式',
    start_option            varchar(1024) null comment '启动选项',
    callback_url            varchar(1000) not null comment '状态回调接口路径',
    resource_example_number int           not null comment '资源实例数量',
    constraint t_pressure_t_resource_id_fk foreign key (resource_id) references t_resource (id)
) comment '任务';

create table if not exists t_calibration
(
    id          bigint auto_increment comment '主键' primary key,
    pressure_id bigint                               not null comment '施压任务主键',
    content     varchar(512)                         not null comment '任务内容',
    completed   tinyint(1) default 0                 not null comment '是否完成',
    start_time  timestamp  default CURRENT_TIMESTAMP not null comment '开始时间',
    end_time    timestamp                            null comment '结束时间',
    constraint t_calibration_t_pressure_id_fk foreign key (pressure_id) references t_pressure (id)
) comment '数据校准任务';

create table if not exists t_metrics_config
(
    id          bigint auto_increment comment '主键' primary key,
    pressure_id bigint       not null comment '压测任务主键',
    ref         varchar(255) not null comment '关键字',
    context     json         null comment '目标值',
    constraint t_metrics_t_pressure_id_fk foreign key (pressure_id) references t_pressure (id)
) comment '指标信息';

create table if not exists t_resource_example
(
    id           bigint auto_increment comment '主键' primary key,
    resource_id  bigint                              not null comment '资源主键',
    watchman_id  bigint                              not null comment '调度器主键',
    cpu          varchar(255)                        not null comment '需要的CPU',
    memory       varchar(255)                        not null comment '需要的内存',
    limit_cpu    varchar(255)                        not null comment '限定的CPU',
    limit_memory varchar(255)                        not null comment '限定的内存',
    create_time  timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    image        varchar(1024)                       null comment '资源镜像信息',
    constraint t_resource_example_t_resource_id_fk foreign key (resource_id) references t_resource (id)
) comment '资源实例表';

create table if not exists t_pressure_example
(
    id                  bigint auto_increment comment '主键' primary key,
    resource_example_id bigint        not null comment '资源实例主键',
    number              int default 0 not null comment '序列号',
    duration            bigint        not null comment '持续时长(毫秒)',
    pressure_id         bigint        not null comment '任务主键',
    constraint t_pressure_example_t_pressure_id_fk foreign key (pressure_id) references t_pressure (id),
    constraint t_pressure_example_t_resource_example_id_fk foreign key (resource_example_id) references t_resource_example (id)
) comment '任务实例';

create table if not exists t_pressure_example_event
(
    id                  bigint auto_increment comment '主键' primary key,
    pressure_example_id bigint                              not null comment '任务实例主键',
    type                int                                 not null comment '事件类型',
    context             json                                not null comment '事件内容',
    time                timestamp default CURRENT_TIMESTAMP not null comment '时间',
    constraint t_pressure_example_event_t_pressure_example_id_fk foreign key (pressure_example_id) references t_pressure_example (id)
) comment '任务实例事件';

create table if not exists t_pressure_file
(
    id                  bigint auto_increment comment '主键' primary key,
    pressure_id         bigint                              not null comment '任务主键',
    pressure_example_id bigint                              not null comment '任务实例主键',
    uri                 varchar(1000)                       not null comment '统一资源描述符',
    type                int                                 not null comment '文件类型',
    start_point         bigint                              not null comment '读取文件的起始点位',
    end_point           mediumtext                          not null comment '读取文件的结束点位',
    create_time         timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint t_pressure_file_t_pressure_example_id_fk foreign key (pressure_example_id) references t_pressure_example (id)
) comment '任务所需文件';

create table if not exists t_resource_example_event
(
    id                  bigint auto_increment comment '主键' primary key,
    resource_example_id bigint                              not null comment '资源实例主键',
    type                int                                 not null comment '事件类型',
    context             json                                not null comment '事件内容',
    time                timestamp default CURRENT_TIMESTAMP not null comment '时间',
    constraint t_resource_example_event_t_resource_example_id_fk foreign key (resource_example_id) references t_resource_example (id)
) comment '资源实例事件';

create table if not exists t_sla
(
    id             bigint auto_increment comment '任务实例' primary key,
    pressure_id    bigint       not null comment '任务主键',
    ref            varchar(255) not null comment '关键词',
    attach         varchar(255) null comment '附加数据',
    formula_target int          null comment '算式目标(RT、TPS、SA、成功率)',
    formula_symbol int          null comment '算式符号(>=、>、=、<=、<)',
    formula_number double       not null comment '算式数值(用户输入)',
    constraint t_sla_t_pressure_id_fk foreign key (pressure_id) references t_pressure (id)
) comment 'Service Level Agreement(服务等级协议)';

create table if not exists t_sla_event
(
    id                  bigint auto_increment comment '任务实例' primary key,
    sla_id              bigint       not null comment 'sla主键',
    pressure_id         bigint       not null comment '任务主键',
    pressure_example_id bigint       not null comment '任务实例主键',
    ref                 varchar(255) not null comment '关键词',
    attach              varchar(255) null comment '附加数据',
    formula_target      int          null comment '算式目标(RT、TPS、SA、成功率)',
    formula_symbol      int          null comment '算式符号(>=、>、=、<=、<)',
    formula_number      double       not null comment '算式数值(用户输入)',
    number              double       not null comment '比较的值(实际变化的值)',
    constraint t_sla_event_t_pressure_example_id_fk foreign key (pressure_example_id) references t_pressure_example (id),
    constraint t_sla_event_t_sla_id_fk foreign key (sla_id) references t_sla (id)
) comment 'sla触发记录';

create table if not exists t_thread_config
(
    id          bigint auto_increment comment '主键' primary key,
    pressure_id bigint       not null comment '任务主键',
    ref         varchar(255) not null comment '关键字',
    mode        int          not null comment '模式',
    context     json         not null comment '线程配置',
    constraint t_thread_config_t_pressure_id_fk foreign key (pressure_id) references t_pressure (id)
) comment '线程配置';

create table if not exists t_thread_config_example
(
    id                  bigint auto_increment comment '主键' primary key,
    serial_number       int default 0 not null,
    pressure_id         int           not null comment '任务主键',
    pressure_example_id bigint        not null comment '任务实例主键',
    ref                 varchar(255)  not null comment '关键字',
    type                int           not null comment '类型',
    context             json          not null comment '线程配置',
    update_time         timestamp     null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint t_thread_config_example_t_pressure_example_id_fk foreign key (pressure_example_id) references t_pressure_example (id)
) comment '线程配置实例';

create table if not exists t_watchman_event
(
    id          int auto_increment comment '主键' primary key,
    watchman_id bigint                              not null comment '调度器主键',
    time        timestamp default CURRENT_TIMESTAMP not null comment '时间',
    type        int                                 not null comment '状态',
    context     json                                not null comment '事件内容',
    constraint t_watchman_event_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id)
) comment '调度器事件';

create index t_watchman_event_time_index on t_watchman_event (time);

create index t_watchman_event_type_index on t_watchman_event (type);

