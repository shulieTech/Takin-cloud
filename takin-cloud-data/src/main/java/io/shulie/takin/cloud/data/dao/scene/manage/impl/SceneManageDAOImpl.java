package io.shulie.takin.cloud.data.dao.scene.manage.impl;

import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryBean;
import io.shulie.takin.cloud.data.converter.senemange.SceneManageEntityConverter;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneManageCreateOrUpdateParam;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageListResult;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageResult;
import io.shulie.takin.cloud.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2020/10/26 4:40 下午
 */
@Component
public class SceneManageDAOImpl
    extends ServiceImpl<SceneManageMapper, SceneManageEntity>
    implements SceneManageDAO, MPUtil<SceneManageEntity> {

    @Override
    public Long insert(SceneManageCreateOrUpdateParam createParam) {
        SceneManageEntity entity = new SceneManageEntity();
        BeanUtils.copyProperties(createParam, entity);
        entity.setUserId(CloudPluginUtils.getContext().getUserId());
        entity.setTenantId(CloudPluginUtils.getContext().getTenantId());
        entity.setEnvCode(CloudPluginUtils.getContext().getEnvCode());
        this.save(entity);
        return entity.getId();
    }

    @Override
    public void update(SceneManageCreateOrUpdateParam updateParam) {
        SceneManageEntity entity = new SceneManageEntity();
        BeanUtils.copyProperties(updateParam, entity);
        entity.setUserId(null);
        entity.setTenantId(null);
        entity.setEnvCode(null);
        this.updateById(entity);
    }

    @Override
    public SceneManageResult getSceneById(Long id) {
        SceneManageEntity entity = this.getById(id);
        if (entity != null) {return BeanUtil.copyProperties(entity, SceneManageResult.class);}
        return null;
    }

    @Override
    public List<SceneManageResult> getPageList(SceneManageQueryBean queryBean) {
        return null;
    }

    @Override
    public SceneManageListResult queryBySceneName(String pressureTestSceneName) {
        LambdaQueryWrapper<SceneManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneManageEntity::getSceneName, pressureTestSceneName);
        SceneManageEntity sceneManageEntity = this.getBaseMapper().selectOne(wrapper);
        return SceneManageEntityConverter.INSTANCE.ofSceneManageEntity(sceneManageEntity);
    }

    @Override
    public List<SceneManageEntity> listFromUpdateScript() {
        return this.list(this.getTenantLQW().select(SceneManageEntity::getId,
            SceneManageEntity::getTenantId, SceneManageEntity::getFeatures));
    }

    @Override
    public SceneManageListResult querySceneManageById(Long sceneId) {
        LambdaQueryWrapper<SceneManageEntity> wrapper = new LambdaQueryWrapper<>();
        if (sceneId != null) {
            wrapper.eq(SceneManageEntity::getId, sceneId);
        }
        SceneManageEntity sceneManageEntities = this.getBaseMapper().selectOne(wrapper);
        return SceneManageEntityConverter.INSTANCE.ofSceneManageEntity(sceneManageEntities);
    }

    @Override
    public SceneManageEntity queueSceneById(Long sceneId) {
        LambdaQueryWrapper<SceneManageEntity> wrapper = new LambdaQueryWrapper<>();
        if (sceneId != null) {
            wrapper.eq(SceneManageEntity::getId, sceneId);
        }
        return this.getBaseMapper().selectOne(wrapper);
    }

    /**
     * 根据场景主键设置场景状态
     *
     * @param sceneId 场景主键
     * @param status  状态值
     * @return 操作影响行数
     */
    @Override
    public int updateStatus(Long sceneId, Integer status) {
        return this.baseMapper.update(
            new SceneManageEntity() {{setStatus(status);}},
            Wrappers.lambdaQuery(SceneManageEntity.class).eq(SceneManageEntity::getId, sceneId));
    }
}
