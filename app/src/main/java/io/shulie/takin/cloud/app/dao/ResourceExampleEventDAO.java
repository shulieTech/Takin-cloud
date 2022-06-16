package io.shulie.takin.cloud.app.dao;

import io.shulie.takin.cloud.app.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;

import java.util.List;

/**
 * ClassName:    ResourceExampleEventDAO
 * Package:    io.shulie.takin.cloud.app.dao
 * Description:
 * Datetime:    2022/6/16   11:58
 * Author:   chenhongqiao@shulie.com
 */
public interface ResourceExampleEventDAO{

    public int insert(ResourceExampleEventEntity entity);

    public List<ResourceExampleEventEntity> findByExampleIdAndType(Long exampleId, NotifyEventType type);
}
