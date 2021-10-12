package io.shulie.takin.cloud.open.api.impl.scenemanage;

import java.util.List;
import java.math.BigDecimal;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.cloud.open.api.scenemanage.CloudSceneApi;
import io.shulie.takin.cloud.open.req.scenemanage.SceneIpNumReq;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.cloud.open.api.impl.sender.CloudApiSenderService;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.cloud.open.req.scenemanage.CloudUpdateSceneFileRequest;

/**
 * @author 何仲奇
 * @date 2020/10/21 3:03 下午
 */
@Component
public class CloudSceneApiImpl implements CloudSceneApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public void updateSceneFileByScriptId(CloudUpdateSceneFileRequest request) {
        cloudApiSenderService.put(CloudApiConstant.SCENE_MANAGE_UPDATE_FILE_URL, request,
            new TypeReference<ResponseResult<?>>() {});
    }

    @Override
    public Long saveScene(SceneManageWrapperReq req) {
        return cloudApiSenderService.post(CloudApiConstant.SCENE_MANAGE_URL, req,
                new TypeReference<ResponseResult<Long>>() {})
            .getData();
    }

    @Override
    public String updateScene(SceneManageWrapperReq req) {
        return cloudApiSenderService.put(CloudApiConstant.SCENE_MANAGE_URL, req,
                new TypeReference<ResponseResult<String>>() {})
            .getData();
    }

    @Override
    public String deleteScene(SceneManageDeleteReq req) {
        return cloudApiSenderService.delete(CloudApiConstant.SCENE_MANAGE_URL, req,
                new TypeReference<ResponseResult<String>>() {})
            .getData();
    }

    @Override
    public SceneManageWrapperResp getSceneDetail(SceneManageIdReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_MANAGE_DETAIL_URL, req,
                new TypeReference<ResponseResult<SceneManageWrapperResp>>() {})
            .getData();
    }

    @Override
    public List<SceneManageListResp> getSceneManageList(ContextExt req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_MANAGE_ALL_LIST_URL, req,
                new TypeReference<ResponseResult<List<SceneManageListResp>>>() {})
            .getData();
    }

    @Override
    public List<SceneManageListResp> getSceneList(SceneManageQueryReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_MANAGE_LIST_URL, req,
                new TypeReference<ResponseResult<List<SceneManageListResp>>>() {})
            .getData();

    }

    @Override
    public BigDecimal calcFlow(SceneManageWrapperReq req) {
        return cloudApiSenderService.post(CloudApiConstant.SCENE_MANAGE_FLOWCALC_URL, req,
                new TypeReference<ResponseResult<BigDecimal>>() {})
            .getData();
    }

    @Override
    public StrategyResp getIpNum(SceneIpNumReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_MANAGE_IPNUM_URL, req,
                new TypeReference<ResponseResult<StrategyResp>>() {})
            .getData();
    }

    @Override
    public ScriptCheckResp checkAndUpdateScript(ScriptCheckAndUpdateReq req) {
        return cloudApiSenderService.post(CloudApiConstant.SCENE_MANAGE_CHECK_AND_UPDATE_URL, req,
                new TypeReference<ResponseResult<ScriptCheckResp>>() {})
            .getData();

    }

    @Override
    public List<SceneManageWrapperResp> queryByIds(SceneManageQueryByIdsReq req) {
        return cloudApiSenderService.get(CloudApiConstant.SCENE_MANAGE_BY_SCENE_IDS, req,
                new TypeReference<ResponseResult<List<SceneManageWrapperResp>>>() {})
            .getData();
    }

}
