package io.shulie.takin.cloud.biz.collector.collector;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 21:08
 */
@Slf4j
public abstract class AbstractIndicators {

    /**
     * 1、判断key是否存在，不存在插入value
     * 2、key存在，比较值大小
     */
    private static final String MAX_SCRIPT =
        "if (redis.call('exists', KEYS[1]) == 0 or redis.call('get', KEYS[1]) < ARGV[1]) then\n" +
            "    redis.call('set', KEYS[1], ARGV[1]);\n" +
            //            "    return 1;\n" +
            "else\n" +
            //            "    return 0;\n" +
            "end";
    private static final String MIN_SCRIPT =
        "if (redis.call('exists', KEYS[1]) == 0 or redis.call('get', KEYS[1]) > ARGV[1]) then\n" +
            "    redis.call('set', KEYS[1], ARGV[1]);\n" +
            //            "    return 1;\n" +
            "else\n" +
            //            "    return 0;\n" +
            "end";
    private static final String UNLOCK_SCRIPT = "if redis.call('exists',KEYS[1]) == 1 then\n" +
        "   redis.call('del',KEYS[1])\n" +
        "else\n" +
        //                    "   return 0\n" +
        "end";
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected EventCenterTemplate eventCenterTemplate;
    @Autowired
    protected SceneManageService sceneManageService;
    private DefaultRedisScript<Void> minRedisScript;
    private DefaultRedisScript<Void> maxRedisScript;
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
    protected String getPressureTaskKey(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (tenantId == null) {
            return String.format("COLLECTOR:TASK:%s:%s", sceneId, reportId);
        }
        return String.format("COLLECTOR:TASK:%s:%s:%S", sceneId, reportId, tenantId);
    }

    public boolean lock(String key, String value) {

        return redisTemplate.execute((RedisCallback<Boolean>)connection -> {
            Boolean bl = connection.set(getLockPrefix(key).getBytes(), value.getBytes(), expiration,
                RedisStringCommands.SetOption.SET_IF_ABSENT);
            return null != bl && bl;
        });
    }

    private String getLockPrefix(String key) {
        return String.format("COLLECTOR LOCK:%s", key);
    }

    public void unlock(String key, String value) {
        redisTemplate.execute(unlockRedisScript, Lists.newArrayList(getLockPrefix(key)), value);
    }

    /**
     * 获取Metrics key
     * 示例：COLLECTOR:TASK:102121:213124512312:1587375600000:登录接口
     *
     * @param taskKey 任务key
     * @param time    时间
     * @return -
     */
    protected String getWindowKey(String taskKey, String transaction, long time) {
        return String.format("%s:%s:%s", taskKey, time, transaction);
    }

    public String getTaskKey(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (tenantId == null) {
            return String.format("%s_%s", sceneId, reportId);
        }
        return String.format("%s_%s_%s", sceneId, reportId, tenantId);
    }

    public static String getRedisTpsLimitKey(Long sceneId, Long reportId, Long tenantId) {
        return String.format("__REDIS_TPS_LIMIT_KEY_%s_%s_%s__", sceneId, reportId, tenantId);
    }

    public static String getRedisTpsAllLimitKey(Long sceneId, Long reportId, Long tenantId) {
        return String.format("__REDIS_TPS_ALL_LIMIT_KEY_%s_%s_%s__", sceneId, reportId, tenantId);
    }

    public static String getRedisTpsPodNumKey(Long sceneId, Long reportId, Long tenantId) {
        return String.format("__REDIS_TPS_POD_NUM_KEY_%s_%s_%s__", sceneId, reportId, tenantId);
    }

    /**
     * 获取Metrics 指标key
     * 示例：COLLECTOR:TASK:102121:213124512312:1587375600000:rt
     *
     * @param indicatorsName 指标名称
     * @return -
     */
    protected String getIndicatorsKey(String windowKey, String indicatorsName) {
        return String.format("%s:%s", windowKey, indicatorsName);
    }

    protected String saCountKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "saCount");
    }

    protected String activeThreadsKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "activeThreads");
    }

    protected String errorKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "error");
    }

    protected String testNameKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "testName");
    }

    /**
     * time 不进行转换
     *
     * @param taskKey 任务key
     * @return -
     */
    protected String last(String taskKey) {
        return getIndicatorsKey(String.format("%s:%s", taskKey, "last"), "last");
    }

    /**
     * 强行自动标识
     *
     * @param taskKey 任务key
     * @return -
     */
    protected String forceCloseTime(String taskKey) {
        return getIndicatorsKey(String.format("%s:%s", taskKey, "forceClose"), "force");
    }

    protected String countKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "count");
    }

    protected String failCountKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "failCount");
    }

    protected String rtKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "rt");
    }

    protected String maxRtKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "maxRt");
    }

    protected String minRtKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "minRt");
    }

    protected String percentDataKey(String taskKey, String transaction, long timeWindow) {
        return getIndicatorsKey(getWindowKey(taskKey, transaction, timeWindow), "percents");
    }

    protected void saveRedisMap(String key, String timestampPodNum, Object value) {
        // 归纳
        redisTemplate.opsForHash().put(key, timestampPodNum, value);
        setTtl(key);
    }

    protected void doubleSaveRedisMap(String key, String timestampPodNum, Double value) {
        // 归纳
        redisTemplate.opsForHash().put(key, timestampPodNum, value);
        setTtl(key);
    }

    protected void longSaveRedisMap(String key, String timestampPodNum, Long value) {
        // 归纳
        redisTemplate.opsForHash().put(key, timestampPodNum, value);
        setTtl(key);
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

    protected void longCumulative(String key, Long value) {
        redisTemplate.opsForValue().increment(key, value);
        setTtl(key);
    }

    protected void intSaveRedisMap(String key, String timestampPodNum, Integer value) {
        // 归纳 数据
        redisTemplate.opsForHash().put(key, timestampPodNum, value);
        setTtl(key);
    }

    protected void setError(String key, String timestampPodNum, String value) {
        redisTemplate.opsForHash().put(key, timestampPodNum, value);
        setTtl(key);
    }

    protected void setMax(String key, Long value) {
        if (redisTemplate.hasKey(key)) {
            long temp = getEventTimeStrap(key);
            if (value > temp) {
                redisTemplate.opsForValue().set(key, value);
            }
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    protected void setMin(String key, Long value) {
        if (redisTemplate.hasKey(key)) {
            long temp = getEventTimeStrap(key);
            if (value < temp) {
                redisTemplate.opsForValue().set(key, value);
            }
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    protected void mostValue(String key, Double value, Integer type) {
        if (0 == type) {
            redisTemplate.execute(maxRedisScript, Lists.newArrayList(key), value);
        } else if (1 == type) {
            redisTemplate.execute(minRedisScript, Lists.newArrayList(key), value);
        }
        setTtl(key);
    }

    private void setTtl(String key) {
        redisTemplate.expire(key, CollectorConstants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
    }

    protected Integer getIntValue(String key) {
        // 数据进行集合
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key)) && redisTemplate.opsForHash().size(key) > 0) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            // 数据聚合
            return map.values().stream()
                .map(t -> Integer.parseInt(t.toString()))
                .reduce(Integer::sum).orElse(0);
        }
        return null;
    }

    protected List<String> getStringValue(String key) {
        // 数据进行集合
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key)) && redisTemplate.opsForHash().size(key) > 0) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            // 数据聚合
            return map.values().stream().map(String::valueOf).collect(Collectors.toList());
        }
        return null;
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

    protected Long getLongValueFromMap(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key)) && redisTemplate.opsForHash().size(key) > 0) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            // 数据聚合
            return map.values().stream().map(String::valueOf)
                .map(Long::valueOf).reduce(Long::sum).orElse(0L);
        }
        return null;
    }

    protected Double getDoubleValue(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        if (null != object) {
            return Double.valueOf(String.valueOf(object));
        }
        return null;
    }

    @PostConstruct
    public void init() {
        minRedisScript = new DefaultRedisScript<>();
        minRedisScript.setResultType(Void.class);
        minRedisScript.setScriptText(MIN_SCRIPT);

        maxRedisScript = new DefaultRedisScript<>();
        maxRedisScript.setResultType(Void.class);
        maxRedisScript.setScriptText(MAX_SCRIPT);

        unlockRedisScript = new DefaultRedisScript<>();
        unlockRedisScript.setResultType(Void.class);
        unlockRedisScript.setScriptText(UNLOCK_SCRIPT);

    }
}
