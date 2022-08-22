package io.shulie.takin.cloud.app.conf;

import lombok.Data;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 使用调度器需要的配置
 *
 * <p>一期暂时的方案</p>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Component
@ConfigurationProperties(prefix = "watchman")
public class WatchmanConfig {
    /**
     * nfs目录
     */
    private String nfsPath;
    /**
     * nfs服务器地址
     */
    private String nfsServer;
    /**
     * nfs的共享目录
     */
    private String nfsDirectory;
    /**
     * zooKeeper连接地址
     */
    private String zkAddress;
    /**
     * ptl日志上传途径
     */
    private String ptlUploadFrom;
    /**
     * 是否启用ptl日志文件
     */
    private Boolean ptlFileEnable;
    /**
     * ptl文件中，是否只输出错误信息
     */
    private Boolean ptlFileErrorOnly;
    /**
     * ptl文件中，是否只输出连接超时信息
     */
    private Boolean ptlFileTimeoutOnly;
    /**
     * 超时阈值
     */
    private Long timeoutThreshold;
    /**
     * 日志是否阶段
     */
    private Boolean logCutOff;
    /**
     * 日志队列大小
     */
    private Long logQueueSize;
    /**
     * 后端监听器队列容量
     */
    private Long backendQueueCapacity;
    /**
     * TPS增长因子
     */
    private Double tpsTargetLevelFactor;
    /**
     * 应用程序版本(cloud版本)
     */
    private String applicationVersion;
}
