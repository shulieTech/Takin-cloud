package io.shulie.takin.cloud.data.dao.middleware.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.cloud.data.dao.middleware.MiddlewareJarDAO;
import io.shulie.takin.cloud.data.mapper.mysql.MiddlewareJarMapper;
import io.shulie.takin.cloud.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.cloud.data.param.middleware.CreateMiddleWareJarParam;
import io.shulie.takin.cloud.data.result.middleware.MiddlewareJarResult;
import io.shulie.takin.cloud.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 中间件包表(MiddlewareJar)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-06-01 10:41:19
 */
@Service
public class MiddlewareJarDAOImpl implements MiddlewareJarDAO, MPUtil<MiddlewareJarEntity> {

    @Autowired
    private MiddlewareJarMapper middlewareJarMapper;


    @Override
    public boolean saveBatch(List<CreateMiddleWareJarParam> createParams) {
        if (CollectionUtil.isEmpty(createParams)) {
            return false;
        }

        List<MiddlewareJarEntity> entities = createParams.stream().map(createParam -> {
            MiddlewareJarEntity middlewareJarEntity = new MiddlewareJarEntity();
            BeanUtils.copyProperties(createParam, middlewareJarEntity);
            return middlewareJarEntity;
        }).collect(Collectors.toList());
        return SqlHelper.retBool(middlewareJarMapper.insertBatch(entities));
    }

    @Override
    public boolean removeByAgvList(List<String> agvList) {
        if (CollectionUtil.isEmpty(agvList)) {
            return false;
        }

        return SqlHelper.retBool(middlewareJarMapper.deleteByAgvList(agvList));
    }

    @Override
    public List<MiddlewareJarResult> listByArtifactIds(List<String> artifactIds) {
        if (CollectionUtil.isEmpty(artifactIds)) {
            return Collections.emptyList();
        }

        List<MiddlewareJarEntity> entities = middlewareJarMapper.selectList(this.getLQW()
            .select(MiddlewareJarEntity::getId, MiddlewareJarEntity::getArtifactId, MiddlewareJarEntity::getVersion,
                MiddlewareJarEntity::getGroupId, MiddlewareJarEntity::getStatus)
            .in(MiddlewareJarEntity::getArtifactId, artifactIds));
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream().map(entity -> {
            MiddlewareJarResult middleWareJarResult = new MiddlewareJarResult();
            BeanUtils.copyProperties(entity, middleWareJarResult);
            return middleWareJarResult;
        }).collect(Collectors.toList());
    }

}

