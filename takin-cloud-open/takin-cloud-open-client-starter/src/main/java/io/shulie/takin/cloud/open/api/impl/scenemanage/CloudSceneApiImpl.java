package io.shulie.takin.cloud.open.api.impl.scenemanage;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.api.impl.aop.annotation.ApiPointCut;
import io.shulie.takin.cloud.open.api.impl.util.UrlBusinessUtil;
import io.shulie.takin.cloud.open.api.scenemanage.CloudSceneApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.req.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.cloud.open.req.scenemanage.SceneIpNumReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * @author 何仲奇
 * @date 2020/10/21 3:03 下午
 */
@Component
public class CloudSceneApiImpl extends CloudCommonApi implements CloudSceneApi {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    @ApiPointCut(name = "cloudScene", errorCode = TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_FILE_ERROR)
    @Override
    public Object updateSceneFileByScriptId(CloudUpdateSceneFileRequest request) {
        return HttpHelper.doPut(UrlBusinessUtil.getSceneMangeUpdateFileUrl(),
            this.getHeaders(request), new TypeReference<ResponseResult<Object>>() {}, request);
    }

    @Override
    public ResponseResult<Long> saveScene(SceneManageWrapperReq req) {
        TakinResponseEntity<ResponseResult<Long>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_URL,
                getHeaders(req), new TypeReference<ResponseResult<Long>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<String> updateScene(SceneManageWrapperReq req) {

        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doPut(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_URL,
                getHeaders(req), new TypeReference<ResponseResult<String>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<String> deleteScene(SceneManageDeleteReq req) {
        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doDelete(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_URL,
                getHeaders(req), new TypeReference<ResponseResult<String>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<SceneManageWrapperResp> getSceneDetail(SceneManageIdReq req) {
        TakinResponseEntity<ResponseResult<SceneManageWrapperResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_DETAIL_URL,
                getHeaders(req), req, new TypeReference<ResponseResult<SceneManageWrapperResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneManageList(CloudUserCommonRequestExt req) {
        TakinResponseEntity<ResponseResult<List<SceneManageListResp>>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_All_LIST_URL,
                getHeaders(req), req, new TypeReference<ResponseResult<List<SceneManageListResp>>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq req) {
        TakinResponseEntity<ResponseResult<List<SceneManageListResp>>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_LIST_URL,
                //过滤
                getHeaders(req), req, new TypeReference<ResponseResult<List<SceneManageListResp>>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");

    }

    @Override
    public ResponseResult<BigDecimal> calcFlow(SceneManageWrapperReq req) {
        TakinResponseEntity<ResponseResult<BigDecimal>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_FLOWCALC_URL,
                getHeaders(req), new TypeReference<ResponseResult<BigDecimal>>() {}, req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<StrategyResp> getIpNum(SceneIpNumReq req) {
        TakinResponseEntity<ResponseResult<StrategyResp>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_IPNUM_URL,
                getHeaders(req), req, new TypeReference<ResponseResult<StrategyResp>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<ScriptCheckResp> checkAndUpdateScript(ScriptCheckAndUpdateReq req) {
        TakinResponseEntity<ResponseResult<ScriptCheckResp>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_CHECK_AND_UPDATE_URL,
                getHeaders(req), new TypeReference<ResponseResult<ScriptCheckResp>>() {},req);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    @Override
    public ResponseResult<List<SceneManageWrapperResp>> queryByIds(SceneManageQueryByIdsReq req) {

        String url = troCloudClientProperties.getUrl() + CloudApiConstant.SCENE_MANAGE_BY_SCENE_IDS;
        List<Long> sceneIds = req.getSceneIds();
        String join = StringUtils.join(sceneIds, ",");
        url = url + "?sceneIds=" + join;
        TakinResponseEntity<ResponseResult<List<SceneManageWrapperResp>>> takinResponseEntity =
            HttpHelper.doGet(url, getHeaders(req), new TypeReference<ResponseResult<List<SceneManageWrapperResp>>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

}
