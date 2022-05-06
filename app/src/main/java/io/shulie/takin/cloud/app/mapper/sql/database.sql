# \n\s+?foreign key
# \n\s+?comment
# \n\s+?primary key

create table if not exists t_callback
(
    id bigint auto_increment comment '主键' primary key,
    url varchar(1000) charset utf8 not null comment '回调路径',
    context blob not null comment '回调内容',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    completed tinyint(1) default 0 not null comment '是否完成'
)comment '回调表';

create table if not exists t_callback_log
(
    id bigint auto_increment comment '主键' primary key,
    callback_id bigint not null comment '回调主键',
    request_url varchar(1000) charset utf8 not null comment '请求路径',
    request_data blob not null comment '请求数据',
    request_time timestamp default CURRENT_TIMESTAMP not null comment '请求时间',
    response_data blob null comment '响应数据',
    response_time datetime null comment '响应时间',
    completed tinyint(1) default 0 not null comment '是否完成',
    constraint t_callback_log_t_callback_id_fk foreign key (callback_id) references t_callback (id)
)comment '回调日志';

create table if not exists t_watchman
(
    id bigint auto_increment comment '主键' primary key,
    ref varchar(512) charset utf8 not null comment '关键词',
    ref_sign varchar(255) charset utf8 not null comment '关键词签名',
    constraint t_watchman_ref_sign_uindex
        unique (ref_sign)
)comment '调度器';

create table if not exists t_command
(
    id bigint auto_increment comment '主键' primary key,
    watchman_id bigint not null comment '调度主键',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    type int not null comment '命令类型',
    content json not null comment '命令内容',
    ack_time datetime null comment '命令确认时间',
    ack_content json null comment '命令确认内容',
    constraint t_command_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id)
)comment '命令表';

create table if not exists t_resource
(
    id bigint auto_increment comment '主键' primary key,
    watchman_id bigint not null comment '调度器主键',
    number int not null comment '需要的数量',
    cpu varchar(255) charset utf8 not null comment '需要的CPU',
    memory varchar(255) charset utf8 not null comment '需要的内存',
    limit_cpu varchar(255) charset utf8 not null comment '限定的CPU',
    limit_memory varchar(255) charset utf8 not null comment '限定的内存',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    callback_url varchar(512) charset utf8 not null comment '状态回调接口路径',
    constraint t_resource_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id)
)comment '资源表';

create table if not exists t_job
(
    id bigint auto_increment comment '主键' primary key,
    name varchar(255) charset utf8 not null comment '任务名称',
    resource_id bigint not null comment '资源主键',
    duration bigint not null comment '持续时间',
    sampling int not null comment '采样率',
    type int not null comment '任务的运行模式',
    callback_url varchar(1000) charset utf8 not null comment '状态回调接口路径',
    resource_example_number int not null comment '资源实例数量',
    constraint t_job_t_resource_id_fk foreign key (resource_id) references t_resource (id)
)comment '任务';

create table if not exists t_metrics_config
(
    id bigint auto_increment comment '主键' primary key,
    job_id bigint not null comment '任务主键',
    ref varchar(255) charset utf8 not null comment '关键字',
    context json null comment '目标值',
    constraint t_metrics_t_job_id_fk foreign key (job_id) references t_job (id)
)comment '指标信息';

create table if not exists t_resource_example
(
    id bigint auto_increment comment '主键' primary key,
    resource_id bigint not null comment '资源主键',
    watchman_id bigint not null comment '调度器主键',
    cpu varchar(255) charset utf8 not null comment '需要的CPU',
    memory varchar(255) charset utf8 not null comment '需要的内存',
    limit_cpu varchar(255) charset utf8 not null comment '限定的CPU',
    limit_memory varchar(255) charset utf8 not null comment '限定的内存',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint t_resource_example_t_resource_id_fk foreign key (resource_id) references t_resource (id)
)comment '资源实例表';

create table if not exists t_job_example
(
    id bigint auto_increment comment '主键' primary key,
    job_id bigint not null comment '任务主键',
    resource_example_id bigint not null comment '资源实例主键',
    name varchar(255) charset utf8 not null comment '名称',
    duration bigint not null comment '持续时长(毫秒)',
    constraint t_job_example_t_job_id_fk foreign key (job_id) references t_job (id),
    constraint t_job_example_t_resource_example_id_fk foreign key (resource_example_id) references t_resource_example (id)
)comment '任务实例';

create table if not exists t_job_example_event
(
    id bigint auto_increment comment '主键' primary key,
    job_example_id bigint not null comment '任务实例主键',
    type int not null comment '事件类型',
    context json not null comment '事件内容',
    time timestamp default CURRENT_TIMESTAMP not null comment '时间',
    constraint t_resource_example_event_t_job_example_id_fk foreign key (job_example_id) references t_job_example (id)
)comment '任务实例事件';

create table if not exists t_job_file
(
    id bigint auto_increment comment '主键' primary key,
    job_example_id bigint not null comment '任务实例主键',
    uri varchar(1000) charset utf8 not null,
    start_point bigint not null comment '读取文件的起始点位',
    end_point mediumtext not null comment '读取文件的结束点位',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint t_job_file_t_job_example_id_fk foreign key (job_example_id) references t_job_example (id)
)comment '任务所需文件';

create table if not exists t_resource_example_event
(
    id bigint auto_increment comment '主键' primary key,
    resource_example_id bigint not null comment '资源实例主键',
    type int not null comment '事件类型',
    context json not null comment '事件内容',
    time timestamp default CURRENT_TIMESTAMP not null comment '时间',
    constraint t_resource_example_event_t_resource_example_id_fk foreign key (resource_example_id) references t_resource_example (id)
)comment '资源实例事件';

create table if not exists t_sla
(
    id bigint auto_increment comment '任务实例' primary key,
    job_id bigint not null comment '任务主键',
    ref varchar(255) charset utf8 not null comment '关键词',
    attach varchar(255) charset utf8 null comment '附加数据',
    formula_target int null comment '算式目标(RT、TPS、SA、成功率)',
    formula_symbol int null comment '算式符号(>=、>、=、<=、<)',
    formula_number double not null comment '算式数值(用户输入)',
    constraint t_sla_t_job_id_fk foreign key (job_id) references t_job (id)
)comment 'Service Level Agreement(服务等级协议)';

create table if not exists t_sla_event
(
    id bigint auto_increment comment '任务实例' primary key,
    job_id bigint not null comment '任务主键',
    sla_id bigint not null comment 'sla主键',
    job_example_id bigint not null comment '任务实例主键',
    ref varchar(255) charset utf8 not null comment '关键词',
    attach varchar(255) charset utf8 null comment '附加数据',
    formula_target int null comment '算式目标(RT、TPS、SA、成功率)',
    formula_symbol int null comment '算式符号(>=、>、=、<=、<)',
    formula_number double not null comment '算式数值(用户输入)',
    number double not null comment '比较的值(实际变化的值)',
    constraint t_sla_event_t_job_example_id_fk foreign key (job_example_id) references t_job_example (id),
    constraint t_sla_event_t_job_id_fk foreign key (job_id) references t_job (id),
    constraint t_sla_event_t_sla_id_fk foreign key (sla_id) references t_sla (id)
)comment 'sla触发记录';

create table if not exists t_thread_config
(
    id bigint auto_increment comment '主键' primary key,
    job_id bigint not null comment '任务主键',
    ref varchar(255) charset utf8 not null comment '关键字',
    mode int not null comment '模式',
    context json not null comment '线程配置',
    constraint t_thread_config_t_job_id_fk foreign key (job_id) references t_job (id)
)comment '线程配置';

create table if not exists t_thread_config_example
(
    id bigint auto_increment comment '主键' primary key,
    serial_number int default 0 not null,
    job_id int not null comment '任务主键',
    job_example_id bigint not null comment '任务实例主键',
    ref varchar(255) charset utf8 not null comment '关键字',
    type int not null comment '类型',
    context json not null comment '线程配置',
    update_time timestamp null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint t_thread_config_example_t_job_example_id_fk foreign key (job_example_id) references t_job_example (id)
)comment '线程配置实例';

create table if not exists t_watchman_event
(
    id int auto_increment comment '主键' primary key,
    watchman_id bigint not null comment '调度器主键',
    time timestamp default CURRENT_TIMESTAMP not null comment '时间',
    type int not null comment '状态',
    context json not null comment '事件内容',
    constraint t_watchman_event_t_watchman_id_fk foreign key (watchman_id) references t_watchman (id)
)comment '调度器事件';

create index t_watchman_event_time_index
    on t_watchman_event (time);

