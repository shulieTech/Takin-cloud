package io.shulie.takin.cloud.sdk.impl.scene.task;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import io.shulie.takin.cloud.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskInspectStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskInspectStopReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskStopReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneTaskStopResp;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/11/13 上午11:06
 */
@Service
public class CloudTaskApiImpl implements CloudTaskApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public SceneActionResp start(SceneTaskStartReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_START),
            req, new TypeReference<ResponseResult<SceneActionResp>>() {}).getData();
    }

    @Override
    public String stopTask(SceneManageIdReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_STOP),
            req, new TypeReference<ResponseResult<String>>() {}).getData();

    }

    @Override
    public Integer boltStopTask(SceneManageIdReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_BOLT_STOP),
            req, new TypeReference<ResponseResult<Integer>>() {}).getData();
    }

    @Override
    public SceneActionResp checkTask(SceneManageIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_CHECK_TASK),
            req, new TypeReference<ResponseResult<SceneActionResp>>() {}).getData();
    }

    @Override
    public String updateSceneTaskTps(SceneTaskUpdateTpsReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_UPDATE_TPS),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public SceneTaskAdjustTpsResp queryAdjustTaskTps(SceneTaskQueryTpsReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_ADJUST_TPS),
            req, new TypeReference<ResponseResult<SceneTaskAdjustTpsResp>>() {}).getData();
    }

    @Override
    public Long startFlowDebugTask(TaskFlowDebugStartReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_START_FLOW_DEBUG),
            req, new TypeReference<ResponseResult<Long>>() {}).getData();
    }

    @Override
    public SceneInspectTaskStartResp startInspectTask(TaskInspectStartReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_START_INSPECT),
            req, new TypeReference<ResponseResult<SceneInspectTaskStartResp>>() {}).getData();
    }

    @Override
    public SceneInspectTaskStopResp stopInspectTask(TaskInspectStopReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_STOP_INSPECT),
            req, new TypeReference<ResponseResult<SceneInspectTaskStopResp>>() {}).getData();
    }

    @Override
    public SceneTaskStopResp forceStopInspectTask(TaskStopReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_FORCE_STOP_INSPECT),
            req, new TypeReference<ResponseResult<SceneTaskStopResp>>() {}).getData();
    }

    @Override
    public SceneTryRunTaskStartResp startTryRunTask(SceneTryRunTaskStartReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_START_TRY_RUN),
            req, new TypeReference<ResponseResult<SceneTryRunTaskStartResp>>() {}).getData();
    }

    @Override
    public SceneTryRunTaskStatusResp checkTaskStatus(SceneTryRunTaskCheckReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_CHECK_STATUS),
            req, new TypeReference<ResponseResult<SceneTryRunTaskStatusResp>>() {}).getData();
    }

    @Override
    public SceneJobStateResp checkSceneJobStatus(SceneManageIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_CHECK_JOB_STATUS),
            req, new TypeReference<ResponseResult<SceneJobStateResp>>() {}).getData();
    }

    @Override
    public SceneStartCheckResp sceneStartPreCheck(SceneStartPreCheckReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_START_PRE_CHECK),
            req, new TypeReference<ResponseResult<SceneStartCheckResp>>() {}).getData();
    }

    @Override
    public Boolean callBackToWriteBalance(ScriptAssetBalanceReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_TASK, EntrypointUrl.METHOD_SCENE_TASK_CALL_BACK_TO_WRITE_BALANCE),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }
}
