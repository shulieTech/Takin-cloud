package io.shulie.takin.cloud.open.api.impl.scenetask;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.api.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.open.req.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.open.req.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.cloud.open.req.scenetask.TaskInspectStartReq;
import io.shulie.takin.cloud.open.req.scenetask.TaskInspectStopReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * @author qianshui
 * @date 2020/11/13 上午11:06
 */
@Component
public class CloudTaskApiImpl extends CloudCommonApi implements CloudTaskApi {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    @Override
    public ResponseResult<SceneActionResp> start(SceneTaskStartReq req) {
        TakinResponseEntity<ResponseResult<SceneActionResp>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_TASK_START,
                getHeaders(req), new TypeReference<ResponseResult<SceneActionResp>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<String> stopTask(SceneManageIdReq req) {
        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_TASK_STOP,
                getHeaders(req), new TypeReference<ResponseResult<String>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");

    }

    @Override
    public ResponseResult<SceneActionResp> checkTask(SceneManageIdReq req) {
        TakinResponseEntity<ResponseResult<SceneActionResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_TASK_CHECK,
                getHeaders(req), req, new TypeReference<ResponseResult<SceneActionResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<String> updateSceneTaskTps(SceneTaskUpdateTpsReq req) {
        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_TASK_UPDATE_TPS,
                getHeaders(req), new TypeReference<ResponseResult<String>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<SceneTaskAdjustTpsResp> queryAdjustTaskTps(SceneTaskQueryTpsReq req) {
        TakinResponseEntity<ResponseResult<SceneTaskAdjustTpsResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_TASK_QUERY_ADJUST_TPS,
                getHeaders(req), req, new TypeReference<ResponseResult<SceneTaskAdjustTpsResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<Long> startFlowDebugTask(TaskFlowDebugStartReq req) {
        TakinResponseEntity<ResponseResult<Long>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.START_FLOW_DEBUG_TASK,
                getHeaders(req), new TypeReference<ResponseResult<Long>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<SceneInspectTaskStartResp> startInspectTask(TaskInspectStartReq req) {
        try {
            String url = troCloudClientProperties.getUrl() + CloudApiConstant.START_INSPECT_TASK;
            String apiResultString = HttpUtil.createPost(url)
                .headerMap(getHeaders(req), true)
                .body(JSONObject.toJSONString(req))
                .execute().body();
            return JSONObject.parseObject(apiResultString,
                new com.alibaba.fastjson.TypeReference<ResponseResult<SceneInspectTaskStartResp>>() {});
        } catch (Exception ex) {
            return ResponseResult.fail("500", "请查看cloud日志" + ex.getMessage());
        }
    }

    @Override
    public ResponseResult<SceneInspectTaskStopResp> stopInspectTask(TaskInspectStopReq req) {
        try {
            String url = troCloudClientProperties.getUrl() + CloudApiConstant.STOP_INSPECT_TASK;
            String apiResultString = HttpUtil.createPost(url)
                .headerMap(getHeaders(req), true)
                .body(JSONObject.toJSONString(req))
                .execute().body();
            return JSONObject.parseObject(apiResultString,
                new com.alibaba.fastjson.TypeReference<ResponseResult<SceneInspectTaskStopResp>>() {});
        } catch (Exception ex) {
            return ResponseResult.fail("500", "请查看cloud日志" + ex.getMessage());
        }
    }

    @Override
    public ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(SceneTryRunTaskStartReq req) {
        TakinResponseEntity<ResponseResult<SceneTryRunTaskStartResp>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.START_TRY_RUN_TASK,
                getHeaders(req), new TypeReference<ResponseResult<SceneTryRunTaskStartResp>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<SceneTryRunTaskStatusResp> checkTaskStatus(SceneTryRunTaskCheckReq req) {
        TakinResponseEntity<ResponseResult<SceneTryRunTaskStatusResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.CHECK_TRY_RUN_TASK_STATUS,
                getHeaders(req), req, new TypeReference<ResponseResult<SceneTryRunTaskStatusResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<SceneJobStateResp> checkSceneJobSstatus(SceneManageIdReq req) {
        TakinResponseEntity<ResponseResult<SceneJobStateResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.CHECK_SCENE_JOB_STATUS,
                getHeaders(req), req, new TypeReference<ResponseResult<SceneJobStateResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<SceneStartCheckResp> sceneStartPreCheck(SceneStartPreCheckReq req) {
        TakinResponseEntity<ResponseResult<SceneStartCheckResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_START_PRE_CHECK,
                getHeaders(req), req, new TypeReference<ResponseResult<SceneStartCheckResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }
}
