package io.shulie.takin.cloud.open.api.scene.manage;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.req.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.open.request.scene.manage.SceneRequest;
import io.shulie.takin.cloud.open.request.scene.manage.SynchronizeRequest;
import io.shulie.takin.cloud.open.response.scene.manage.SceneDetailResponse;

/**
 * 混合压测场景SDK接口
 *
 * @author 张天赐
 */
public interface MultipleSceneApi {
    /**
     * 创建压测场景 - 新
     *
     * @param request 入参
     * @return 场景自增主键
     */
    ResponseResult<Long> create(SceneRequest request);

    /**
     * 更新压测场景 - 新
     *
     * @param request 入参
     * @return 操作结果
     */
    ResponseResult<Boolean> update(SceneRequest request);

    /**
     * 获取压测场景 - 新
     *
     * @param request 入参
     * @return 场景详情
     */
    ResponseResult<SceneDetailResponse> detail(SceneTaskStartReq request);

    /**
     * 同步场景信息 - 新
     *
     * @param request 入参
     * @return 同步事务标识
     */
    ResponseResult<String> synchronize(SynchronizeRequest request);
}
