package io.shulie.takin.cloud.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import java.util.List;

public interface SceneBigFileSliceMapper extends BaseMapper<SceneBigFileSliceEntity> {
    int updateBatch(List<SceneBigFileSliceEntity> list);
}