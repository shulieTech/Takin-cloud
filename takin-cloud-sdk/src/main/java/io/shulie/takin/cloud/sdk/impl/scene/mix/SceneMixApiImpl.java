package io.shulie.takin.cloud.sdk.impl.scene.mix;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.entrypoint.scene.mix.SceneMixApi;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SynchronizeRequest;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailV2Response;

/**
 * 混合压测场景管理
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class SceneMixApiImpl implements SceneMixApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    /**
     * 创建压测场景
     *
     * @param request 入参
     * @return 场景自增主键
     */
    @Override
    public Long create(SceneRequest request) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MIX, EntrypointUrl.METHOD_SCENE_MIX_CREATE),
            request, new TypeReference<ResponseResult<Long>>() {}).getData();
    }

    /**
     * 更新压测场景
     *
     * @param request 入参
     * @return 操作结果
     */
    @Override
    public Boolean update(SceneRequest request) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MIX, EntrypointUrl.METHOD_SCENE_MIX_CREATE),
            request, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    /**
     * 获取压测场景
     *
     * @param sceneId 场景主键
     * @return 场景详情
     */
    @Override
    public SceneDetailV2Response detail(SceneManageQueryReq sceneId) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MIX, EntrypointUrl.METHOD_SCENE_MIX_CREATE),
            sceneId, new TypeReference<ResponseResult<SceneDetailV2Response>>() {}).getData();
    }

    /**
     * 同步场景信息
     *
     * @param request 入参
     * @return 同步事务标识
     */
    @Override
    public String synchronize(SynchronizeRequest request) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_SCENE_MIX, EntrypointUrl.METHOD_SCENE_MIX_CREATE),
            request, new TypeReference<ResponseResult<String>>() {}).getData();
    }
}
