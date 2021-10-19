package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneManage;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryBean;
import io.shulie.takin.cloud.common.annotation.DataApartInterceptAnnotation;

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
    int updateByPrimaryKeySelective(SceneManage record);

    /**
     * 更新状态
     *
     * @param record 入参
     * @return -
     */
    int updateStatus(UpdateStatusBean record);

    /**
     * 分页查询
     *
     * @param queryVO -
     * @return -
     */
    @DataApartInterceptAnnotation
    List<SceneManage> getPageList(SceneManageQueryBean queryVO);

    /**
     * 查询所有场景信息
     *
     * @return -
     */
    List<SceneManage> selectAllSceneManageList();

    /**
     * 依据主键集合查询
     *
     * @param ids 主键集合
     * @return 查询结果
     */
    List<SceneManage> getByIds(@Param("ids") List<Long> ids);

}
