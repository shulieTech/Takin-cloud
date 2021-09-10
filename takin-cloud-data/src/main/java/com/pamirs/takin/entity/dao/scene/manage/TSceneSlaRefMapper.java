package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneSlaRef;
import org.apache.ibatis.annotations.Param;

public interface TSceneSlaRefMapper {

    int deleteByPrimaryKey(Long id);

    int deleteBySceneId(Long sceneId);

    Long insertSelective(SceneSlaRef record);

    void batchInsert(@Param("items") List<SceneSlaRef> records);

    SceneSlaRef selectByPrimaryKey(Long id);

    List<SceneSlaRef> selectBySceneId(Long sceneId);

    int updateByPrimaryKeySelective(SceneSlaRef record);

}
