package io.shulie.takin.cloud.app.conf;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.PropertySource;

/**
 * 使用调度器需要的配置
 *
 * <p>一期暂时的方案</p>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Component
@PropertySource("classpath:watchman.properties")
public class WatchmanConfig {
    /**
     * 容器拉取镜像的路径
     */
    @Value("${container.image}")
    private String containerImage;
    /**
     * nfs服务器地址
     */
    @Value("${nfs.server}")
    private String nfsServer;
    /**
     * nfs的共享目录
     */
    @Value("${nfs.directory}")
    private String nfsDirectory;
    /**
     * zooKeeper连接地址
     */
    @Value("${zk.address}")
    private String zkAddress;
    /**
     * ptl日志上传途径
     */
    @Value("${ptl.upload.from}")
    private String ptlUploadFrom;
    /**
     * 是否启用ptl日志文件
     */
    @Value("${ptl.file.enable}")
    private Boolean ptlFileEnable;
    /**
     * ptl文件中，是否只输出错误信息
     */
    @Value("${ptl.file.error.only}")
    private Boolean ptlFileErrorOnly;
    /**
     * ptl文件中，是否只输出连接超时信息
     */
    @Value("${ptl.file.timeout.only}")
    private Boolean ptlFileTimeoutOnly;
    /**
     * 超时阈值
     */
    @Value("${timeout.threshold}")
    private Long timeoutThreshold;
    /**
     * 日志是否阶段
     */
    @Value("${log.cut.off}")
    private Boolean logCutOff;
    /**
     * 日志队列大小
     */
    @Value("${log.queue.size}")
    private Long logQueueSize;
    @Value("${backend.queue.capacity}")
    private Long backendQueueCapacity;
    /**
     * jvm启动参数
     */
    @Value("${java.options}")
    private String javaOptions;
    @Value("${tps.target.level.factor}")
    private Double tpsTargetLevelFactor;
}
