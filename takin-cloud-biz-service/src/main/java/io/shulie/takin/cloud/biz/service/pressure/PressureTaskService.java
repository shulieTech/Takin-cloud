package io.shulie.takin.cloud.biz.service.pressure;

import io.shulie.takin.cloud.biz.enums.PressureTaskStatusEnum;
import io.shulie.takin.cloud.biz.pojo.PressureTaskPo;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.param.pressure.PressureTaskQueryParam;
import io.shulie.takin.cloud.ext.content.enginecall.EngineRunConfig;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.sdk.model.request.pressure.StartEngineReq;

/**
 * @Author: liyuanba
 * @Date: 2021/12/29 1:52 下午
 */
public interface PressureTaskService {
    /**
     * 构建压测引擎启动参数
     */
    public EngineRunConfig buildEngineRunConfig(PressureTaskPo po, StrategyConfigExt strategyConfig);
    /**
     * 根据启动参数生成压测任务对象
     */
    public PressureTaskPo buildPressureTask(StartEngineReq req, StrategyConfigExt strategyConfig);

    public String getJobName(PressureTaskEntity task);

    public String getJobName(PressureTaskPo task);

    /**
     * 通过场景id获取最新的一条任务
     */
    public PressureTaskEntity getLastTaskBySceneId(Long sceneId, PressureSceneEnum sceneType);

    /**
     * 通过场景id获取是否有正在运行的压测任务
     */
    public PressureTaskEntity getRunningTaskBySceneId(Long sceneId, PressureSceneEnum sceneType);

    /**
     * 新增压测任务记录
     */
    public int add(PressureTaskPo entity);

    public int update(PressureTaskEntity entity);

    public int updateStatus(Long id, PressureTaskStatusEnum status, String message);

    public int delete(Long id);

    public PressureTaskEntity getById(Long id);

    public PressureTaskQueryParam query(PressureTaskQueryParam param);
}
