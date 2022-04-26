-- noinspection SqlNoDataSourceInspectionForFile

CREATE TABLE if NOT EXISTS t_callback (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    url VARCHAR(1000) charset utf8 NOT NULL comment '回调路径',
    context BLOB NOT NULL comment '回调内容',
    create_time datetime NOT NULL comment '创建时间',
    completed tinyint(1) DEFAULT 0 NOT NULL comment '是否完成'
 ) comment '回调表';

CREATE TABLE if NOT EXISTS t_callback_log (
    id BIGINT NOT NULL comment '主键',
    callback_id BIGINT NOT NULL comment '回调主键',
    request_url VARCHAR(1000) charset utf8 NOT NULL comment '请求路径',
    request_data BLOB NOT NULL comment '请求数据',
    request_time datetime NOT NULL comment '请求时间',
    response_data BLOB NULL comment '响应数据',
    response_time datetime NOT NULL comment '响应时间',
    completed tinyint(1) DEFAULT 0 NOT NULL comment '是否完成',
    CONSTRAINT t_callback_log_t_callback_id_fk FOREIGN key (callback_id) REFERENCES t_callback (id)
 ) comment '回调日志';

CREATE TABLE if NOT EXISTS t_command (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    create_time datetime NOT NULL comment '创建时间',
    `type` INT NOT NULL comment '命令类型',
    context json NOT NULL comment '命令内容',
    ack_time datetime NOT NULL comment '命令确认时间',
    ack_context json NOT NULL comment '命令确认内容'
) comment '命令表';

CREATE TABLE if NOT EXISTS t_watchman (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    `ref` VARCHAR(512) charset utf8 NOT NULL comment '关键词',
    ref_sign VARCHAR(255) charset utf8 NOT NULL comment '关键词签名'
 ) comment '调度器';

CREATE TABLE if NOT EXISTS t_resource (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    watchman_id BIGINT NOT NULL comment '调度器主键',
    `number` INT NOT NULL comment '需要的数量',
    cpu VARCHAR(255) charset utf8 NOT NULL comment '需要的CPU',
    memory VARCHAR(255) charset utf8 NOT NULL comment '需要的内存',
    limit_cpu VARCHAR(255) charset utf8 NOT NULL comment '限定的CPU',
    limit_memory VARCHAR(255) charset utf8 NOT NULL comment '限定的内存',
    create_time datetime NOT NULL comment '创建时间',
    callback_url VARCHAR(512) charset utf8 NOT NULL comment '状态回调接口路径',
    CONSTRAINT t_resource_t_watchman_id_fk FOREIGN key (watchman_id) REFERENCES t_watchman (id)
 ) comment '资源表';

CREATE TABLE if NOT EXISTS t_job (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    `name` VARCHAR(255) charset utf8 NOT NULL comment '任务名称',
    resource_id BIGINT NOT NULL comment '资源主键',
    duration BIGINT NOT NULL comment '持续时间',
    sampling INT NOT NULL comment '采样率',
    mode INT NOT NULL comment '任务的运行模式',
    callback_url INT NOT NULL comment '状态回调接口路径',
    resource_example_number INT NOT NULL comment '资源实例数量',
    CONSTRAINT t_job_t_resource_id_fk FOREIGN key (resource_id) REFERENCES t_resource (id)
 ) comment '任务' auto_increment = 2;

CREATE TABLE if NOT EXISTS t_metrics_config (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    job_id BIGINT NOT NULL comment '任务主键',
    `ref` VARCHAR(255) charset utf8 NOT NULL comment '关键字',
    context json NULL comment '目标值',
    CONSTRAINT t_metrics_t_job_id_fk FOREIGN key (job_id) REFERENCES t_job (id)
 ) comment '指标信息';

CREATE TABLE if NOT EXISTS t_resource_example (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    resource_id BIGINT NOT NULL comment '资源主键',
    watchman_id BIGINT NOT NULL comment '调度器主键',
    cpu VARCHAR(255) charset utf8 NOT NULL comment '需要的CPU',
    memory VARCHAR(255) charset utf8 NOT NULL comment '需要的内存',
    limit_cpu VARCHAR(255) charset utf8 NOT NULL comment '限定的CPU',
    limit_memory VARCHAR(255) charset utf8 NOT NULL comment '限定的内存',
    create_time datetime NOT NULL comment '创建时间',
    CONSTRAINT t_resource_example_t_resource_id_fk FOREIGN key (resource_id) REFERENCES t_resource (id)
 ) comment '资源实例表';

CREATE TABLE if NOT EXISTS t_job_example (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    job_id BIGINT NOT NULL comment '任务主键',
    resource_example_id BIGINT NOT NULL comment '资源实例主键',
    duration BIGINT NOT NULL comment '持续时长(毫秒)',
    CONSTRAINT t_job_example_t_job_id_fk FOREIGN key (job_id) REFERENCES t_job (id),
    CONSTRAINT t_job_example_t_resource_example_id_fk FOREIGN key (resource_example_id) REFERENCES t_resource_example (id)
 ) comment '任务实例';

CREATE TABLE if NOT EXISTS t_resource_example_event (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    resource_example_id BIGINT NOT NULL comment '资源实例主键',
    `type` INT NOT NULL comment '事件类型',
    context json NOT NULL comment '事件内容',
    `time` datetime NOT NULL comment '时间',
    CONSTRAINT t_resource_example_event_t_resource_example_id_fk FOREIGN key (resource_example_id) REFERENCES t_resource_example (id)
 ) comment '资源实例事件';

CREATE TABLE if NOT EXISTS t_thread_config (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    job_id BIGINT NOT NULL comment '任务主键',
    `ref` VARCHAR(255) charset utf8 NOT NULL comment '关键字',
    mode INT NOT NULL comment '模式',
    context json NOT NULL comment '线程配置',
    CONSTRAINT t_thread_config_t_job_id_fk FOREIGN key (job_id) REFERENCES t_job (id)
 ) comment '线程配置';

CREATE TABLE if NOT EXISTS t_thread_config_example (
    id BIGINT auto_increment comment '主键' PRIMARY key,
    job_id BIGINT NOT NULL comment '任务实例主键',
    `ref` VARCHAR(255) charset utf8 NOT NULL comment '关键字',
    model INT NOT NULL comment '模式',
    context json NOT NULL comment '线程配置',
    update_time datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    CONSTRAINT t_thread_config_example_t_job_id_fk FOREIGN key (job_id) REFERENCES t_job (id)
 ) comment '线程配置实例';

CREATE TABLE if NOT EXISTS t_watchman_event (
    id INT auto_increment comment '主键' PRIMARY key,
    watchman_id BIGINT NOT NULL comment '调度器主键',
    `time` datetime NOT NULL comment '时间',
    `type` INT NOT NULL comment '状态',
    content json NOT NULL comment '事件内容',
    CONSTRAINT t_watchman_event_t_watchman_id_fk FOREIGN key (watchman_id) REFERENCES t_watchman (id)
 ) comment '调度器事件';