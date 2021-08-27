package io.shulie.takin.cloud.biz.service.async;

import io.shulie.takin.ext.content.enginecall.ScheduleStartRequestExt;

/**
 * @ClassName AsyncService
 * @Description 异步服务
 * @Author qianshui
 * @Date 2020/10/30 下午7:13
 */
public interface AsyncService {

    void checkStartedTask(ScheduleStartRequestExt startRequest);

    void updateSceneRunningStatus(Long sceneId,Long reportId);
}
