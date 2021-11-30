package io.shulie.takin.cloud.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.ScenePressureTestLogUploadEntity;

/**
 * 场景Jmeter日志上传
 *
 * @author -
 */
public interface SceneJmeterlogUploadMapper extends BaseMapper<ScenePressureTestLogUploadEntity> {
    /**
     * 批量更新
     *
     * @param list 批数据
     * @return 更新结果
     */
    int updateBatch(List<ScenePressureTestLogUploadEntity> list);
}