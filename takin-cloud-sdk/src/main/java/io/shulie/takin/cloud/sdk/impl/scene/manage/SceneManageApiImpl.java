package io.shulie.takin.cloud.sdk.impl.scene.manage;

import java.util.List;
import java.math.BigDecimal;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import io.shulie.takin.cloud.ext.content.trace.PagingContextExt;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.*;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.entrypoint.scene.manage.SceneManageApi;
import io.shulie.takin.cloud.sdk.model.response.strategy.StrategyResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;

/**
 * @author 何仲奇
 * @date 2020/10/21 3:03 下午
 */
@Service
public class SceneManageApiImpl implements SceneManageApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public void updateSceneFileByScriptId(CloudUpdateSceneFileRequest request) {
        cloudApiSenderService.put(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE_FILE),
            request, new TypeReference<ResponseResult<?>>() {});
    }

    @Override
    public Long saveScene(SceneManageWrapperReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_SAVE),
            req, new TypeReference<ResponseResult<Long>>() {}).getData();
    }

    @Override
    public String updateScene(SceneManageWrapperReq req) {
        return cloudApiSenderService.put(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public String deleteScene(SceneManageDeleteReq req) {
        return cloudApiSenderService.delete(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_DELETE),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public SceneManageWrapperResp getSceneDetail(SceneManageIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_DETAIL),
            req, new TypeReference<ResponseResult<SceneManageWrapperResp>>() {}).getData();
    }

    @Override
    public SceneManageWrapperResp getSceneDetailNoAuth(SceneManageIdReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_DETAIL_NO_AUTH),
            req, new TypeReference<ResponseResult<SceneManageWrapperResp>>() {}).getData();
    }

    @Override
    public List<SceneManageListResp> getSceneManageList(ContextExt req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_LIST),
            req, new TypeReference<ResponseResult<List<SceneManageListResp>>>() {}).getData();
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq req) {
        ResponseResult<List<SceneManageListResp>> result =
            cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_SEARCH),
                req, new TypeReference<ResponseResult<List<SceneManageListResp>>>() {});
        return ResponseResult.success(result.getData(), result.getTotalNum());

    }

    @Override
    public BigDecimal calcFlow(SceneManageWrapperReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_CALC_FLOW),
            req, new TypeReference<ResponseResult<BigDecimal>>() {}).getData();
    }

    @Override
    public StrategyResp getIpNum(SceneIpNumReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_GET_IP_NUMBER),
            req, new TypeReference<ResponseResult<StrategyResp>>() {}).getData();
    }

    @Override
    public ScriptCheckResp checkAndUpdateScript(ScriptCheckAndUpdateReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_CHECK_AND_UPDATE_SCRIPT),
            req, new TypeReference<ResponseResult<ScriptCheckResp>>() {}).getData();

    }

    @Override
    public List<SceneManageWrapperResp> queryByIds(SceneManageQueryByIdsReq req) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_QUERY_BY_IDS),
            req, new TypeReference<ResponseResult<List<SceneManageWrapperResp>>>() {}).getData();
    }

    /**
     * 获取压测中的压测场景列表
     * @param queryReq 请求对象
     * @return
     */
    public List<SceneManageRunningResp> getSceneManageRunningList(SceneManageQueryReq queryReq){
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MANAGE, EntrypointUrl.METHOD_SCENE_MANAGE_QUERY_RUNNING_PAGE_LIST),
                queryReq, new TypeReference<ResponseResult<List<SceneManageRunningResp>>>() {}).getData();
    }
}
