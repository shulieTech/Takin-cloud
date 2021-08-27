package io.shulie.takin.cloud.data.dao.sceneTask;

import io.shulie.takin.cloud.data.model.mysql.ScenePressureTestLogUploadEntity;

/**
 * @author xr.l
 */
public interface SceneTaskPressureTestLogUploadDAO {

    /**
     * 插入一条记录
     * @param entity
     * @return
     */
    int insertRecord(ScenePressureTestLogUploadEntity entity);

    /**
     * 根据sceneId，reportId 查询是否已经存在数据
     * @param entity
     * @return
     */
    int countRecord(ScenePressureTestLogUploadEntity entity);

    /**
     * 根据sceneId，reportId 查询是否已经存在数据
     * @param entity
     * @return
     */
    ScenePressureTestLogUploadEntity selectRecord(ScenePressureTestLogUploadEntity entity);

    /**
     * 更新任务
     * @param entity
     * @return
     */
    int updateRecord(ScenePressureTestLogUploadEntity entity);
}
