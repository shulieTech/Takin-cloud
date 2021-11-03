package io.shulie.takin.ext.content.enginecall;


import lombok.Data;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Data
public class ScheduleRunRequest {

    /**
     * 调度ID
     */
    private Long scheduleId;

    /**
     * 调度参数
     */
    private ScheduleStartRequestExt request;

    /**
     * 策略配置
     */
    private StrategyConfigExt strategyConfig;

    /**
     * 容器中的java内存配置
     */
    private String memSetting;

    /**
     * 数据收集模式:redis，influxdb
     */
    private String collector;

    private String engineRedisAddress;
    private String engineRedisPort;
    private String engineRedisSentinelNodes;
    private String engineRedisSentinelMaster;
    private String engineRedisPassword;
    /**
     * zookeeper
     */
    private String zkServers;

    private Integer logQueueSize;
    private String pressureEngineBackendQueueCapacity;
    /**
     * 采样率
     */
    private Integer traceSampling;
    /**
     * 引擎日志配置
     */
    private PtlLogConfigExt ptlLogConfig;
}
