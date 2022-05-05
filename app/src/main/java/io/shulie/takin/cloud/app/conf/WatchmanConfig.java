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
}
