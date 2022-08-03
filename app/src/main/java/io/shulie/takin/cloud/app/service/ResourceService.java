package io.shulie.takin.cloud.app.service;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.model.request.ApplyResourceRequest;
import io.shulie.takin.cloud.model.resource.ResourceExampleOverview;

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
     * @return <ul>
     * <li>key:调度主键</li>
     * <li>value:分配POD数</li>
     * </ul>
     */
    Map<Long, Integer> check(ApplyResourceRequest apply);

    /**
     * 锁定资源
     *
     * @param apply 资源申请信息
     * @return true/false
     */
    String lock(ApplyResourceRequest apply);

    /**
     * 释放资源
     *
     * @param resourceId 资源主键
     */
    void unlock(long resourceId);

    /**
     * 资源实例概览
     *
     * @param resourceExampleId 资源实例主键
     * @return 概览信息
     */
    ResourceExampleOverview exampleOverview(Long resourceExampleId);

    /**
     * 获取数据对象 - 资源
     *
     * @param id 数据主键
     * @return Entity
     */
    ResourceEntity entity(long id);

    /**
     * 获取数据对象 - 资源实例
     *
     * @param id 数据主键
     * @return Entity
     */
    ResourceExampleEntity exampleEntity(long id);
}
