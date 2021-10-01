package io.shulie.takin.cloud.data.dao.scene.task.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.cloud.data.dao.scene.task.SceneTaskPressureTestLogUploadDAO;
import io.shulie.takin.cloud.data.mapper.mysql.SceneJmeterlogUploadMapper;
import io.shulie.takin.cloud.data.model.mysql.ScenePressureTestLogUploadEntity;
import org.springframework.stereotype.Component;

/**
 * @author xr.l
 */
@Component
public class SceneTaskPressureTestLogUploadDAOImpl implements SceneTaskPressureTestLogUploadDAO {

    @Resource
    private SceneJmeterlogUploadMapper uploadMapper;

    @Override
    public int insertRecord(ScenePressureTestLogUploadEntity entity) {
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return  uploadMapper.insert(entity);
    }

    @Override
    public int countRecord(ScenePressureTestLogUploadEntity entity) {
        LambdaQueryWrapper<ScenePressureTestLogUploadEntity> wrapper = new LambdaQueryWrapper<>();
        return uploadMapper.selectCount(wrapper);
    }

    @Override
    public ScenePressureTestLogUploadEntity selectRecord(ScenePressureTestLogUploadEntity entity) {
        LambdaQueryWrapper<ScenePressureTestLogUploadEntity> wrapper = new LambdaQueryWrapper<>();
        return uploadMapper.selectOne(wrapper);
    }

    @Override
    public int updateRecord(ScenePressureTestLogUploadEntity entity) {
        entity.setModifyTime(new Date());
        return uploadMapper.updateById(entity);
    }
}
