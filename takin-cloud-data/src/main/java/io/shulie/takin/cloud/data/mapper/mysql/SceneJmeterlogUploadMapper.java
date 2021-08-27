package io.shulie.takin.cloud.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.ScenePressureTestLogUploadEntity;
import java.util.List;

public interface SceneJmeterlogUploadMapper extends BaseMapper<ScenePressureTestLogUploadEntity> {
    int updateBatch(List<ScenePressureTestLogUploadEntity> list);
}