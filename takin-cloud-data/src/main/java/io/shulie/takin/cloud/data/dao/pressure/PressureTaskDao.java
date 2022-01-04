package io.shulie.takin.cloud.data.dao.pressure;

import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.param.pressure.PressureTaskQueryParam;

/**
 * @Author: liyuanba
 * @Date: 2021/12/28 3:33 下午
 */
public interface PressureTaskDao {
    /**
     * 新增
     * @return 返回新增记录数
     */
    int insert(PressureTaskEntity entity);

    /**
     * 更新
     * @return 返回更新记录数
     */
    int update(PressureTaskEntity entity);

    /**
     * 删除
     * @return 返回删除记录数
     */
    int delete(Long id);

    /**
     * 查询
     */
    PressureTaskEntity getById(Long id);

    /**
     * 多
     * @param param
     * @return
     */
    PressureTaskQueryParam query(PressureTaskQueryParam param);

    /**
     * 更新状态
     */
    int updateStatus(Long id, Integer status);
}
