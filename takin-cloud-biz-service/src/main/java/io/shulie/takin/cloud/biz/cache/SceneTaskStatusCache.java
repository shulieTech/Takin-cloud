package io.shulie.takin.cloud.biz.cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.output.scenetask.SceneRunTaskStatusOutput;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 场景运行状态缓存和读取
 *
 * @author xr.l
 */
@Component
public class SceneTaskStatusCache {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private static final long EXPIRE_TIME = 60 * 60;

    public void cacheStatus(long sceneId, long reportId, SceneRunTaskStatusEnum statusEnum) {
        cacheStatus(sceneId, reportId, statusEnum, null);
    }

    public void cacheStatus(long sceneId, long reportId, SceneRunTaskStatusEnum statusEnum, String msg) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        redisTemplate.opsForHash().put(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY, statusEnum.getText());
        if (statusEnum == SceneRunTaskStatusEnum.FAILED && StringUtils.isNotBlank(msg)) {
            redisTemplate.opsForHash().put(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR,
                String.format(msg, sceneId));
        }
        redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public SceneRunTaskStatusOutput getStatus(long sceneId, long reportId) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        Object status = redisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY);
        SceneRunTaskStatusOutput output = new SceneRunTaskStatusOutput();
        if (Objects.nonNull(status)) {
            SceneRunTaskStatusEnum statusEnum = SceneRunTaskStatusEnum.getTryRunTaskStatusEnumByText(status.toString());
            output.setTaskStatus(statusEnum.getCode());
            if (SceneRunTaskStatusEnum.FAILED == statusEnum) {
                Object errorObj = redisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
                if (Objects.nonNull(errorObj)) {
                    output.setErrorMsg(errorObj.toString());
                }
            }
        }
        return output;
    }

    public void cachePodNum(long sceneId, int podNum) {
        redisTemplate.opsForHash().put(ScheduleConstants.SCHEDULE_POD_NUM, String.valueOf(sceneId), podNum);
    }
}
