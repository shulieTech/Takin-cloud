package io.shulie.takin.cloud.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author -
 */
public interface ReportMapper extends BaseMapper<ReportEntity> {

    /**
     * 根据场景的ID列表，查询场景最后一个压测报告
     * @param ids 场景的ID列表
     * @return 压测报告列表
     */
    List<ReportEntity> queryBySceneIds(@Param("ids") List<Long> ids);
}