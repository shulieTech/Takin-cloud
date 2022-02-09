package io.shulie.takin.cloud.biz.pojo;

import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.TimeUnitEnum;
import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneScriptRefOpen;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Author: liyuanba
 * @Date: 2021/12/29 4:34 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PressureTaskPo extends AbstractEntry {
    /**
     * 压测任务ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 租户主键
     */
    private Long tenantId;
    /**
     * 用户id
     */
    private String envCode;

    /**
     * 管理者id，或者操作者id
     */
    private Long adminId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 场景类型
     */
    private PressureSceneEnum sceneType;

    /**
     * 压测时长
     */
    private Long holdTime;

    /**
     * 压测时长单位
     */
    private TimeUnitEnum holdTimeUnit;

    /**
     * 脚本文件
     */
    private String scriptFile;

    /**
     * 业务活动配置
     */
    private List<SceneBusinessActivityRefOpen> businessActivityConfig;

    /**
     * 业务活动配置
     */
    private List<SceneScriptRefOpen> uploadFiles;

    /**
     * 关联到的插件列表
     */
    private List<EnginePluginsRefOpen> enginePlugins;

    /**
     * 脚本id
     */
    private Long scriptId;

    /**
     * 脚本发布记录id
     */
    private Long scriptDeployId;

    /**
     * 脚本节点信息
     */
    private List<ScriptNode> scriptNodes;

    /**
     * 状态：0压测引擎启动中，1压测中，2压测停止，3失败
     */
    private Integer status;

    /**
     * 压测启动时间
     */
    private Date gmtStart;

    /**
     * 压测结束时间
     */
    private Date gmtEnd;

    /**
     * 启动的pod个数
     */
    private Integer podNum;

    /**
     * 请求的cpu核数，单位:m
     */
    private Long requestCpu;

    /**
     * 限制最大cpu核数，单位:m
     */
    private Long limitCpu;

    /**
     * 请求的内存，单位:M
     */
    private Long requestMemory;

    /**
     * 限制的最大内存，单位:M
     */
    private Long limitMemory;

    /**
     * jvm参数
     */
    private String jvmSettings;

    /**
     * 并发数
     */
    private Integer throughput;

    /**
     * 巡检任务巡间隔时间
     */
    private Long fixTimer;
    /**
     * 循环次数
     */
    private Long loopsNum;

    /**
     * 取样率
     */
    private Integer traceSampling;

    /**
     * 是否继续上次的位置继续读
     */
    private Boolean continueRead;

    /**
     * 最后存活时间，健康监控
     */
    private Date gmtLive;
    /**
     * 消息
     */
    private String message;
}
