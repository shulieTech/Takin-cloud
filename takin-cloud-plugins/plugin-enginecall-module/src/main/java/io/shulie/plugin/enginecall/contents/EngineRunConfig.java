package io.shulie.plugin.enginecall.contents;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import io.shulie.takin.ext.content.enginecall.ScheduleStartRequestExt;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 压测引擎启动配置
 * @Author: liyuanba
 * @Date: 2021/10/29 2:47 下午
 */
@Data
public class EngineRunConfig extends AbstractEntry {
    private Long sceneId;
    private Long taskId;
    private Long customerId;
    /**
     * 数据上报地址
     */
    private String consoleUrl;
    /**
     * cloud回调地址
     */
    private String callbackUrl;
    /**
     * 启动的pod数量
     */
    private Integer podCount;
    /**
     * 脚本文件完整路径和文件名
     */
    private String scriptPath;
    /**
     * 脚本文件所在目录
     */
    private String pressureEnginePathUrl;
    private String extJarPath;
    /**
     * 是否是在本地启动
     */
    private Boolean isLocal;
    /**
     * 调度任务路径
     */
    private String taskDir;
    /**
     * 压测时长
     */
    private Long continuedTime;
    /**
     * 并发线程数
     */
    private Integer expectThroughput;
    /**
     * 压测引擎插件文件位置  一个压测场景可能有多个插件 一个插件也有可能有多个文件
     */
    private List<String> enginePluginsFiles;
    /**
     * 压测配置信息
     */
    private EnginePressureConfig pressureConfig;
    /**
     * 文件
     */
    private List<ScheduleStartRequestExt.DataFile> fileSets;
    private Map<String, String> businessMap;
    private String memSetting;

}
