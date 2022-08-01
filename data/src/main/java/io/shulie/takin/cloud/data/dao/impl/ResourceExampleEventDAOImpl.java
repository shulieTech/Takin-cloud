package io.shulie.takin.cloud.data.dao.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.cloud.data.dao.ResourceExampleEventDAO;
import io.shulie.takin.cloud.data.entity.ResourceExampleEventEntity;
import io.shulie.takin.cloud.data.mapper.ResourceExampleEventMapper;
import io.shulie.takin.cloud.constant.enums.NotifyEventType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName:    ResourceExampleEventDAO
 * Package:    io.shulie.takin.cloud.app.dao
 * Description:
 * Datetime:    2022/6/16   11:58
 * Author:   chenhongqiao@shulie.com
 */
@Service
public class ResourceExampleEventDAOImpl implements ResourceExampleEventDAO {
    @javax.annotation.Resource
    ResourceExampleEventMapper resourceExampleEventMapper;

    @Override
    public int insert(ResourceExampleEventEntity entity) {
        return resourceExampleEventMapper.insert(entity);
    }

    @Override
    public List<ResourceExampleEventEntity> findByExampleIdAndType(Long exampleId, NotifyEventType type) {
        // 查询条件 - 状态类型
        Wrapper<ResourceExampleEventEntity> statusWrapper = new LambdaQueryWrapper<ResourceExampleEventEntity>()
            .eq(ResourceExampleEventEntity::getResourceExampleId, exampleId)
            .eq(ResourceExampleEventEntity::getType, type.getCode());
        // 执行SQL
        return resourceExampleEventMapper.selectList(statusWrapper);
    }

    @Override
    public ResourceExampleEventEntity selectById(Long id) {
        return resourceExampleEventMapper.selectById(id);
    }
}
