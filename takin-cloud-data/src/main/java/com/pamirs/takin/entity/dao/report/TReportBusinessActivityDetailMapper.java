package com.pamirs.takin.entity.dao.report;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.report.ReportBusinessActivityDetail;
import org.apache.ibatis.annotations.Param;

public interface TReportBusinessActivityDetailMapper {

    int insertSelective(ReportBusinessActivityDetail record);

    int updateByPrimaryKeySelective(ReportBusinessActivityDetail record);

    ReportBusinessActivityDetail selectByPrimaryKey(Long id);

    /**
     * @param reportId
     * @return -
     */
    List<ReportBusinessActivityDetail> queryReportBusinessActivityDetailByReportId(@Param("reportId") Long reportId);

    Map<String, Object> selectCountByReportId(Long reportId);
}
