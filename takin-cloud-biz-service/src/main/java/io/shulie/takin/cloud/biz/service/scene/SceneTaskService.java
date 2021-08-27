package io.shulie.takin.cloud.biz.service.scene;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneActionOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneJobStateOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskQueryTpsOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStartOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStatusOutput;
import io.shulie.takin.cloud.common.bean.task.TaskResult;

/**
 * @Author 莫问
 * @Date 2020-04-22
 */
public interface SceneTaskService {

    /**
     * 启动场景测试
     * @param input
     * @return
     */
    SceneActionOutput start(SceneTaskStartInput input);

    /**
     * 停止场景测试
     *
     * @param sceneId
     * @return
     */
    void stop(Long sceneId);

    /**
     * 检查场景压测启动状态
     *
     * @param sceneId
     * @param reportId
     * @return
     */
    SceneActionOutput checkSceneTaskStatus(Long sceneId,Long reportId);

    /**
     * 处理场景任务事件
     *
     * @param taskResult
     */
    void handleSceneTaskEvent(TaskResult taskResult);

    /**
     * 结束标识，之后并不是pod生命周期结束，而是metric数据传输完毕，将状态回置成压测停止
     *
     * @param param
     * @see CollectorService
     */

    String taskResultNotify(SceneTaskNotifyParam param);

    /**
     * 开始任务试跑
     * @param input
     * @return
     */
    SceneTryRunTaskStartOutput startTryRun(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins);
    /**
     * 调整压测任务的tps
     * @param input
     */
    void updateSceneTaskTps(SceneTaskUpdateTpsInput input);

    /**
     * 查询当前调整压测任务的tps
     * @param input
     * @return
     */
    SceneTaskQueryTpsOutput queryAdjustTaskTps(SceneTaskQueryTpsInput input);

    /**
     * 启动流量调试，返回报告id
     * @param input
     * @return
     */
    Long startFlowDebugTask(SceneManageWrapperInput input,  List<EnginePluginInput> enginePlugins);

    /**
     * 启动巡检场景
     * @param input
     * @return
     */
    SceneInspectTaskStartOutput startInspectTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins);

    /**
     * 停止巡检场景
     * @param sceneId
     * @return
     */
    SceneInspectTaskStopOutput stopInspectTask(Long sceneId);

    /**
     * 查询流量试跑状态
     * @param sceneId
     * @param reportId
     * @param customerId
     * @return
     */
    SceneTryRunTaskStatusOutput checkTaskStatus(Long sceneId, Long reportId);

    int saveUnUploadJmeterLogScene(Long sceneId,Long reportId,Long customerId,Integer taskStatus);

    /**
     * 检查巡检任务状态：压测引擎
     *
     * @param sceneId
     * @return
     */
    SceneJobStateOutput checkSceneJobStatus(Long sceneId);

    SceneTaskStartCheckOutput sceneStartCsvPositionCheck(SceneTaskStartCheckInput input);
}
