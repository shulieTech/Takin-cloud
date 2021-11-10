package io.shulie.takin.cloud.open.api.impl.io.shulie.takin.cloud.open.api.scene.manage;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;

import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.api.scene.manage.MultipleSceneApi;
import io.shulie.takin.cloud.open.req.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.open.request.scene.manage.SceneRequest;
import io.shulie.takin.cloud.open.request.scene.manage.SynchronizeRequest;
import io.shulie.takin.cloud.open.response.scene.manage.SceneDetailResponse;

/**
 * 混合压测场景SDK接口 - 实现
 *
 * @author 张天赐
 */
@Component
public class MultipleSceneApiImpl extends CloudCommonApi implements MultipleSceneApi {
    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    /**
     * 创建压测场景 - 新
     *
     * @param request 入参
     * @return 场景自增主键
     */
    @Override
    public ResponseResult<Long> create(SceneRequest request) {
        TakinResponseEntity<ResponseResult<Long>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.MULTIPLE_SCENE_CREATE,
                getHeaders(request), new TypeReference<ResponseResult<Long>>() {}, request);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    /**
     * 更新压测场景 - 新
     *
     * @param request 入参
     * @return 操作结果
     */
    @Override
    public ResponseResult<Boolean> update(SceneRequest request) {
        TakinResponseEntity<ResponseResult<Boolean>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.MULTIPLE_SCENE_UPDATE,
                getHeaders(request), new TypeReference<ResponseResult<Boolean>>() {}, request);
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    /**
     * 获取压测场景 - 新
     *
     * @param request 入参
     * @return 场景详情
     */
    @Override
    public ResponseResult<SceneDetailResponse> detail(SceneTaskStartReq request) {
        TakinResponseEntity<ResponseResult<SceneDetailResponse>> takinResponseEntity =
            HttpHelper.doGet(troCloudClientProperties.getUrl() + CloudApiConstant.MULTIPLE_SCENE_DETAIL,
                getHeaders(request), request, new TypeReference<ResponseResult<SceneDetailResponse>>() {});
        if (takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }

    /**
     * 同步场景信息 - 新
     *
     * @param request 入参
     * @return 同步事务标识
     */
    @Override
    public ResponseResult<String> synchronize(SynchronizeRequest request) {
        TakinResponseEntity<ResponseResult<String>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.MULTIPLE_SCENE_SYNCHRONIZE,
                getHeaders(request), new TypeReference<ResponseResult<String>>() {}, request);
        if (takinResponseEntity.getSuccess()) {return takinResponseEntity.getBody();}
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(), "查看cloud日志");
    }
}
