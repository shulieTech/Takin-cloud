package io.shulie.takin.cloud.data.mapper.mysql;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author -
 */
public interface ReportMapper extends BaseMapper<ReportEntity> {

    List<ReportEntity>  queryBySceneIds(@Param("ids") List<Long> ids);

    List<Map<String,String>> getEmailByPtId(Long ptId);

}