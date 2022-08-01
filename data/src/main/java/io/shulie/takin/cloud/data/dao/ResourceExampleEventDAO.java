package io.shulie.takin.cloud.data.dao;

import io.shulie.takin.cloud.data.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;

import java.util.List;

/**
 * ClassName:    ResourceExampleEventDAO
 * Package:    io.shulie.takin.cloud.app.dao
 * Description:
 * Datetime:    2022/6/16   11:58
 * Author:   chenhongqiao@shulie.com
 */
public interface ResourceExampleEventDAO {

    int insert(ResourceExampleEventEntity entity);

    List<ResourceExampleEventEntity> findByExampleIdAndType(Long exampleId, NotifyEventType type);

    ResourceExampleEventEntity selectById(Long id);

}
