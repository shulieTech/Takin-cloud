package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.model.request.ApplyResourceRequest;

/**
 * 资源服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface ResourceService {
    /**
     * 列出资源实例
     *
     * @param resourceId 资源主键
     * @return 资源实例列表
     */
    List<ResourceExampleEntity> listExample(Long resourceId);

    /**
     * 校验资源
     *
     * @param apply 资源申请信息
     * @return true/false
     */
    boolean check(ApplyResourceRequest apply);

    /**
     * 校验资源
     *
     * @param apply       资源申请信息
     * @param callbackUrl 资源状态变更的回调地址
     * @return true/false
     */
    String lock(ApplyResourceRequest apply, String callbackUrl);
}
