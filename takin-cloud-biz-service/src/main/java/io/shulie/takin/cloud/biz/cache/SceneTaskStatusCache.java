package io.shulie.takin.cloud.biz.cache;

import java.util.Objects;

import io.shulie.takin.cloud.biz.output.scenetask.SceneRunTaskStatusOutput;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 场景运行状态缓存和读取
 *
 * @author xr.l
 */
@Component
public class SceneTaskStatusCache {

    @Autowired
    private RedisClientUtils redisClientUtils;

    private static final long EXPIRE_TIME = 60 * 60 * 24;

    public void cacheStatus(long sceneId, long reportId, SceneRunTaskStatusEnum statusEnum){
        cacheStatus(sceneId,reportId,statusEnum,null);
    }

    public void cacheStatus(long sceneId, long reportId, SceneRunTaskStatusEnum statusEnum, String msg) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        redisClientUtils.hmset(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY, statusEnum.getText());
        if (statusEnum == SceneRunTaskStatusEnum.FAILED && StringUtils.isNotBlank(msg)) {
            redisClientUtils.hmset(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR,
                String.format(msg, sceneId));
        }
        redisClientUtils.expire(key, EXPIRE_TIME);
    }

    public SceneRunTaskStatusOutput getStatus(long sceneId, long reportId) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        Object status = redisClientUtils.hmget(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY);
        SceneRunTaskStatusOutput output = new SceneRunTaskStatusOutput();
        if (Objects.nonNull(status)) {
            SceneRunTaskStatusEnum statusEnum = SceneRunTaskStatusEnum.getTryRunTaskStatusEnumByText(status.toString());
            output.setTaskStatus(statusEnum.getCode());
            if (SceneRunTaskStatusEnum.FAILED == statusEnum) {
                Object errorObj = redisClientUtils.hmget(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
                if (Objects.nonNull(errorObj)) {
                    output.setErrorMsg(errorObj.toString());
                }
            }
        }
        return output;
    }

    public void cachePodNum(long sceneId,int podNum){
        redisClientUtils.hmset(ScheduleConstants.SCHEDULE_POD_NUM, String.valueOf(sceneId), podNum);
    }
}
