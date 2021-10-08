package io.shulie.takin.cloud.biz.service.async;

import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;

/**
 * 异步服务
 *
 * @author qianshui
 * @date 2020/10/30 下午7:13
 */
public interface AsyncService {

    /**
     * 检查已启动的任务
     *
     * @param startRequest 入参
     */
    void checkStartedTask(ScheduleStartRequestExt startRequest);

    /**
     * 更新场景运行状态
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     */
    void updateSceneRunningStatus(Long sceneId, Long reportId);
}
