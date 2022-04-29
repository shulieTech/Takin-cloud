package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.app.entity.ResourceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
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
     * @param jobId      任务主键
     * @return 资源实例列表
     */
    List<ResourceExampleEntity> listExample(Long resourceId, Long jobId);

    /**
     * 列出资源实例
     *
     * @param resourceId 资源主键
     * @return 资源实例列表
     */
    default List<ResourceExampleEntity> listExample(long resourceId) {
        return listExample(resourceId, null);
    }

    /**
     * 校验资源
     *
     * @param apply 资源申请信息
     * @return true/false
     * @throws JsonProcessingException JSON异常
     */
    boolean check(ApplyResourceRequest apply) throws JsonProcessingException;

    /**
     * 锁定资源
     *
     * @param apply 资源申请信息
     * @return true/false
     * @throws JsonProcessingException JSON异常
     */
    String lock(ApplyResourceRequest apply) throws JsonProcessingException;

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
     * @throws JsonProcessingException JSON异常
     */
    ResourceExampleOverview exampleOverview(Long resourceExampleId) throws JsonProcessingException;

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
