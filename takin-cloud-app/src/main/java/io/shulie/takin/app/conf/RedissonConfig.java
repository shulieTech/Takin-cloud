package io.shulie.takin.app.conf;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * 分布式锁 redisson config
 *
 * @author liuchuan
 * @date 2021/6/2 12:12 下午
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();

        Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel != null && !CollectionUtils.isEmpty(sentinel.getNodes())) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .setTimeout(10000)
                .setDatabase(redisProperties.getDatabase())
                .setMasterName(redisProperties.getSentinel().getMaster());
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                sentinelServersConfig.setPassword(redisProperties.getPassword());
            }
            for (String node : sentinel.getNodes()) {
                sentinelServersConfig.addSentinelAddress(String.format("redis://%s", node));
            }

            sentinelServersConfig.setCheckSentinelsList(false);
            return Redisson.create(config);
        }
        if (Objects.nonNull(redisProperties.getCluster())){
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            Cluster cluster = redisProperties.getCluster();
            List<String> nodes = cluster.getNodes();
            nodes.stream().filter(Objects::nonNull)
                    .forEach(node ->{
                        clusterServersConfig.addNodeAddress(String.format("redis://%s", node));
                    });
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                clusterServersConfig.setPassword(redisProperties.getPassword());
            }
        }else {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress(
                    String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort()))
                .setDatabase(redisProperties.getDatabase());
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                singleServerConfig.setPassword(redisProperties.getPassword());
            }
        }
        return Redisson.create(config);
    }

}
