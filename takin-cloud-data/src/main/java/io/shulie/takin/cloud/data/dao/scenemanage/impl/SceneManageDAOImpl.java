package io.shulie.takin.cloud.data.dao.scenemanage.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryBean;
import io.shulie.takin.cloud.data.converter.senemange.SceneManageEntityConverter;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
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
 * @Package io.shulie.takin.cloud.data.dao.scenemanage
 * @date 2020/10/26 4:40 下午
 */
@Component
public class SceneManageDAOImpl
    extends ServiceImpl<SceneManageMapper, SceneManageEntity>
    implements SceneManageDAO, MPUtil<SceneManageEntity> {

    @Override
    public Long insert(SceneManageCreateOrUpdateParam createParam) {
        SceneManageEntity sceneManageEntity = new SceneManageEntity();
        BeanUtils.copyProperties(createParam,sceneManageEntity);
        this.save(sceneManageEntity);
        return sceneManageEntity.getId();
    }

    @Override
    public void update(SceneManageCreateOrUpdateParam updateParam) {
        SceneManageEntity sceneManageEntity = new SceneManageEntity();
        BeanUtils.copyProperties(updateParam,sceneManageEntity);
        this.updateById(sceneManageEntity);
    }

    @Override
    public SceneManageResult getSceneById(Long id) {
        SceneManageEntity entity = this.getById(id);
        if(entity == null) {
            return null;
        }
        SceneManageResult result = new SceneManageResult();
        BeanUtils.copyProperties(entity,result);
        return result;
    }

    @Override
    public List<SceneManageResult> getPageList(SceneManageQueryBean queryBean) {
        return null;
    }

    @Override
    public SceneManageListResult queryBySceneName(String pressureTestSceneName) {
        LambdaQueryWrapper<SceneManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneManageEntity::getSceneName,pressureTestSceneName);
        SceneManageEntity sceneManageEntity = this.getBaseMapper().selectOne(wrapper);
        return SceneManageEntityConverter.INSTANCE.ofSceneManageEntity(sceneManageEntity);
    }

    @Override
    public List<SceneManageEntity> listFromUpdateScript() {
        return this.list(this.getTenantLQW().select(SceneManageEntity::getId,
            SceneManageEntity::getCustomerId, SceneManageEntity::getFeatures));
    }

    @Override
    public SceneManageListResult querySceneManageById(Long sceneId) {
        LambdaQueryWrapper<SceneManageEntity> wrapper = new LambdaQueryWrapper<>();
        if (sceneId != null ){
            wrapper.eq(SceneManageEntity::getId,sceneId);
        }
        SceneManageEntity sceneManageEntities = this.getBaseMapper().selectOne(wrapper);
        return SceneManageEntityConverter.INSTANCE.ofSceneManageEntity(sceneManageEntities);
    }
    @Override
    public SceneManageEntity queueSceneById(Long sceneId) {
        LambdaQueryWrapper<SceneManageEntity> wrapper = new LambdaQueryWrapper<>();
        if (sceneId != null ){
            wrapper.eq(SceneManageEntity::getId,sceneId);
        }
        return this.getBaseMapper().selectOne(wrapper);
    }

}
