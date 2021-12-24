package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 场景 mapper
 *
 * @author -
 */
@Mapper
@Deprecated
public interface TSceneManageMapper {

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return -
     */
    int updateByPrimaryKeySelective(SceneManageEntity  record);

    /**
     * 更新状态
     *
     * @param record 入参
     * @return -
     */
    int updateStatus(UpdateStatusBean record);

    /**
     * 查询所有场景信息
     *
     * @return -
     */
    List<SceneManageEntity > selectAllSceneManageList();

    /**
     * 依据主键集合查询
     *
     * @param ids 主键集合
     * @return 查询结果
     */
    List<SceneManageEntity > getByIds(@Param("ids") List<Long> ids);

}
