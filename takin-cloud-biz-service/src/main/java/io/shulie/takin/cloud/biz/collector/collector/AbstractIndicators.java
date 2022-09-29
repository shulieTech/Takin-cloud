package io.shulie.takin.cloud.biz.collector.collector;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 21:08
 */
@Slf4j
public abstract class AbstractIndicators {

    private static final String UNLOCK_SCRIPT = "if redis.call('exists',KEYS[1]) == 1 then\n"
        + "redis.call('del',KEYS[1])\n"
        + "else\n"
        + "end";
    @javax.annotation.Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected EventCenterTemplate eventCenterTemplate;
    @Autowired
    protected SceneManageService sceneManageService;
    private DefaultRedisScript<Void> unlockRedisScript;
    private final Expiration expiration = Expiration.seconds((int)CollectorConstants.REDIS_KEY_TIMEOUT);

    /**
     * 压测场景强行关闭预留时间
     */
    @Value("${scene.pressure.forceCloseTime: 20}")
    private Integer forceCloseTime;

    /**
     * 获取Metrics key
     * 示例：COLLECTOR:TASK:102121:213124512312
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @return -
     */
    protected String getPressureTaskKey(Long sceneId, Long reportId, Long customerId) {
        // 兼容原始redis key
        if (customerId == null) {
            return String.format("COLLECTOR:TASK:%s:%s", sceneId, reportId);
        }
        return String.format("COLLECTOR:TASK:%s:%s:%S", sceneId, reportId, customerId);
    }

    public boolean lock(String key, String value) {

        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>)connection -> {
            Boolean bl = connection.set(getLockPrefix(key).getBytes(), value.getBytes(), expiration,
                RedisStringCommands.SetOption.SET_IF_ABSENT);
            return null != bl && bl;
        }));
    }

    private String getLockPrefix(String key) {
        return String.format("COLLECTOR LOCK:%s", key);
    }

    public void unlock(String key, String value) {
        redisTemplate.execute(unlockRedisScript, Lists.newArrayList(getLockPrefix(key)), value);
    }

    public String getTaskKey(Long sceneId, Long reportId, Long customerId) {
        if (customerId == null) {return String.format("%s_%s", sceneId, reportId);}
        return String.format("%s_%s_%s", sceneId, reportId, customerId);
    }

    /**
     * 获取Metrics 指标key
     * 示例：COLLECTOR:TASK:102121:213124512312:1587375600000:rt
     *
     * @param indicatorsName 指标名称
     * @return -
     */
    protected String getIndicatorsKey(String windowKey, String indicatorsName) {
        return windowKey + ":" + indicatorsName;
    }

    /**
     * time 不进行转换
     *
     * @param taskKey 任务key
     * @return -
     */
    protected String last(String taskKey) {
        return getIndicatorsKey(getIndicatorsKey(taskKey, ":last"), "last");
    }

    /**
     * 强行自动标识
     *
     * @param taskKey 任务key
     * @return -
     */
    protected String forceCloseTime(String taskKey) {
        return getIndicatorsKey(getIndicatorsKey(taskKey, "forceClose"), "force");
    }

    protected void setLast(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 强行自动关闭时间
     *
     * @param key          key
     * @param startTime    开始时间
     * @param pressureTime 秒
     */
    protected void setForceCloseTime(String key, Long startTime, Long pressureTime) {
        if (forceCloseTime > 30) {
            // 大于30 强制改成30
            forceCloseTime = 30;
        }
        Long forceTime = startTime + pressureTime * 1000 + forceCloseTime * 1000;
        redisTemplate.opsForValue().set(key, forceTime);
        log.info("redis key:{} 超时时间:{} ", key, forceTime);
    }

    protected void setMax(String key, Long value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            long temp = getEventTimeStrap(key);
            if (value > temp) {
                redisTemplate.opsForValue().set(key, value);
            }
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    protected void setMin(String key, Long value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            long temp = getEventTimeStrap(key);
            if (value < temp) {
                redisTemplate.opsForValue().set(key, value);
            }
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 获取时间搓，取time 求min max
     *
     * @param key key
     * @return -
     */
    protected Long getEventTimeStrap(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        if (null != object) {
            return (long)object;
        }
        return null;
    }

    @PostConstruct
    public void init() {
        unlockRedisScript = new DefaultRedisScript<>();
        unlockRedisScript.setResultType(Void.class);
        unlockRedisScript.setScriptText(UNLOCK_SCRIPT);

    }
}
