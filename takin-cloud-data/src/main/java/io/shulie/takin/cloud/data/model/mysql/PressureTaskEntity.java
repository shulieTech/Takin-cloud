package io.shulie.takin.cloud.data.model.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: liyuanba
 * @Date: 2021/12/28 2:54 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_pressure_task")
public class PressureTaskEntity extends BaseDeleteEntity {
    /**
     * 租户主键
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 用户id
     */
    @TableField(value = "env_code")
    private String envCode;

    /**
     * 管理者id，或者操作者id
     */
    @TableField(value = "admin_id")
    private Long adminId;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 场景类型
     */
    @TableField(value = "scene_type")
    private Integer sceneType;

    /**
     * 压测时长，单位:秒
     */
    @TableField(value = "hold_time")
    private Long holdTime;

    /**
     * 压测时长，单位:秒
     */
    @TableField(value = "script_file")
    private String scriptFile;

    /**
     * 业务活动配置
     */
    @TableField(value = "business_activity_config")
    private String businessActivityConfig;

    /**
     * 业务活动配置
     */
    @TableField(value = "upload_files")
    private String uploadFiles;

    /**
     * 关联到的插件列表
     */
    @TableField(value = "engine_plugins")
    private String enginePlugins;

    /**
     * 脚本id
     */
    @TableField(value = "script_id")
    private Long scriptId;

    /**
     * 脚本发布记录id
     */
    @TableField(value = "script_deploy_id")
    private Long scriptDeployId;

    /**
     * 脚本节点信息
     */
    @TableField(value = "script_nodes")
    private String scriptNodes;

    /**
     * 状态：0压测引擎启动中，1压测中，2压测停止，3失败
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 压测启动时间
     */
    @TableField(value = "gmt_start")
    private Date gmtStart;

    /**
     * 压测结束时间
     */
    @TableField(value = "gmt_end")
    private Date gmtEnd;

    /**
     * 启动的pod个数
     */
    @TableField(value = "pod_num")
    private Integer podNum;

    /**
     * 请求的cpu核数，单位m
     */
    @TableField(value = "request_cpu")
    private Long requestCpu;

    /**
     * 限制最大cpu核数，单位m
     */
    @TableField(value = "limit_cpu")
    private Long limitCpu;

    /**
     * 请求的内存，单位M
     */
    @TableField(value = "request_memory")
    private Long requestMemory;

    /**
     * 限制的最大内存，单位M
     */
    @TableField(value = "limit_memory")
    private Long limitMemory;

    /**
     * jvm参数
     */
    @TableField(value = "jvm_settings")
    private String jvmSettings;

    /**
     * 并发数
     */
    @TableField(value = "throughput")
    private Integer throughput;
    /**
     * 巡检任务巡间隔时间
     */
    @TableField(value = "fix_timer")
    private Long fixTimer;

    /**
     * 循环次数
     */
    @TableField(value = "loops_num")
    private Long loopsNum;

    /**
     * 取样率
     */
    @TableField(value = "trace_sampling")
    private Integer traceSampling;

    /**
     * 是否继续上次的位置继续读
     */
    @TableField(value = "continue_read")
    private Boolean continueRead;

    /**
     * 最后存活时间，健康监控
     */
    @TableField(value = "gmt_live")
    private Date gmtLive;
}
