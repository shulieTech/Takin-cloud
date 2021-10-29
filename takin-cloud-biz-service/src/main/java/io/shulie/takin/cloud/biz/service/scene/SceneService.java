package io.shulie.takin.cloud.biz.service.scene;

import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;

/**
 * 场景 - 服务
 *
 * @author 张天赐
 */
public interface SceneService {
    /**
     * 创建压测场景
     *
     * @param in 入参
     * @return 场景主键
     */
    Long create(WriteSceneRequest in);
}
