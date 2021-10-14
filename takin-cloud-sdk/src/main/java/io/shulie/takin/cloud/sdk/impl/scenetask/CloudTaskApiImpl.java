package io.shulie.takin.cloud.sdk.impl.scenetask;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.sdk.constant.CloudApiConstant;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.api.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.sdk.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.sdk.req.scenetask.TaskInspectStopReq;
import io.shulie.takin.cloud.sdk.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.sdk.req.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.sdk.req.scenetask.TaskInspectStartReq;
import io.shulie.takin.cloud.sdk.req.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.sdk.req.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.cloud.sdk.req.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.sdk.req.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.cloud.sdk.impl.sender.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.req.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.sdk.req.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.cloud.sdk.req.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.sdk.req.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.sdk.resp.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.cloud.sdk.resp.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.cloud.sdk.resp.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.sdk.resp.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.cloud.sdk.resp.scenemanage.SceneTryRunTaskStatusResp;

/**
 * @author qianshui
 * @date 2020/11/13 上午11:06
 */
@Component
public class CloudTaskApiImpl implements CloudTaskApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public SceneActionResp start(SceneTaskStartReq req) {
        return cloudApiSenderService.post(CloudApiConstant.SCENE_TASK_START, req,
                new TypeReference<ResponseResult<SceneActionResp>>() {})
            .getData();
    }

    @Override
    public String stopTask(SceneManageIdReq req) {
        return cloudApiSenderService.post(CloudApiConstant.SCENE_TASK_STOP, req,
                new TypeReference<ResponseResult<String>>() {})
            .getData();

    }

    @Override
    public SceneActionResp checkTask(SceneManageIdReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_TASK_CHECK, req,
                new TypeReference<ResponseResult<SceneActionResp>>() {})
            .getData();
    }

    @Override
    public String updateSceneTaskTps(SceneTaskUpdateTpsReq req) {
        return cloudApiSenderService.post(CloudApiConstant.SCENE_TASK_UPDATE_TPS, req,
                new TypeReference<ResponseResult<String>>() {})
            .getData();
    }

    @Override
    public SceneTaskAdjustTpsResp queryAdjustTaskTps(SceneTaskQueryTpsReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_TASK_QUERY_ADJUST_TPS, req,
                new TypeReference<ResponseResult<SceneTaskAdjustTpsResp>>() {})
            .getData();
    }

    @Override
    public Long startFlowDebugTask(TaskFlowDebugStartReq req) {
        return cloudApiSenderService.post(CloudApiConstant.START_FLOW_DEBUG_TASK, req,
                new TypeReference<ResponseResult<Long>>() {})
            .getData();
    }

    @Override
    public SceneInspectTaskStartResp startInspectTask(TaskInspectStartReq req) {
        return cloudApiSenderService.post(CloudApiConstant.START_INSPECT_TASK, req,
                new TypeReference<ResponseResult<SceneInspectTaskStartResp>>() {})
            .getData();
    }

    @Override
    public SceneInspectTaskStopResp stopInspectTask(TaskInspectStopReq req) {
        return cloudApiSenderService.post(CloudApiConstant.STOP_INSPECT_TASK, req,
                new TypeReference<ResponseResult<SceneInspectTaskStopResp>>() {})
            .getData();
    }

    @Override
    public SceneTryRunTaskStartResp startTryRunTask(SceneTryRunTaskStartReq req) {
        return cloudApiSenderService.post(CloudApiConstant.START_TRY_RUN_TASK, req,
                new TypeReference<ResponseResult<SceneTryRunTaskStartResp>>() {})
            .getData();
    }

    @Override
    public SceneTryRunTaskStatusResp checkTaskStatus(SceneTryRunTaskCheckReq req) {
        return cloudApiSenderService.get(CloudApiConstant.CHECK_TRY_RUN_TASK_STATUS, req,
                new TypeReference<ResponseResult<SceneTryRunTaskStatusResp>>() {})
            .getData();
    }

    @Override
    public SceneJobStateResp checkSceneJobStatus(SceneManageIdReq req) {
        return cloudApiSenderService.get(CloudApiConstant.CHECK_SCENE_JOB_STATUS, req,
                new TypeReference<ResponseResult<SceneJobStateResp>>() {})
            .getData();
    }

    @Override
    public SceneStartCheckResp sceneStartPreCheck(SceneStartPreCheckReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_START_PRE_CHECK, req,
                new TypeReference<ResponseResult<SceneStartCheckResp>>() {})
            .getData();
    }

    @Override
    public Boolean callBackToWriteBalance(ScriptAssetBalanceReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_TASK_WRITE_BALANCE, req,
                new TypeReference<ResponseResult<Boolean>>() {})
            .getData();
    }
}
