package io.shulie.takin.cloud.biz.service.async;

import io.shulie.takin.ext.content.enginecall.ScheduleStartRequestExt;

/**
 * 异步服务
 *
 * @author qianshui
 * @date 2020/10/30 下午7:13
 */
public interface AsyncService {

    void checkStartedTask(ScheduleStartRequestExt startRequest);

    void updateSceneRunningStatus(Long sceneId, Long reportId);
}
