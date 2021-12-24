package com.pamirs.takin.entity.dao.report;

import java.util.Map;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;

/**
 * @author -
 */
public interface TReportBusinessActivityDetailMapper {

    /**
     * 插入
     *
     * @param record -
     * @return -
     */
    int insertSelective(ReportBusinessActivityDetailEntity record);

    /**
     * 更新
     *
     * @param record -
     * @return -
     */
    int updateByPrimaryKeySelective(ReportBusinessActivityDetailEntity record);

    /**
     * 根据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    ReportBusinessActivityDetailEntity selectByPrimaryKey(Long id);

    /**
     * 查询报告关联的业务活动详情
     *
     * @param reportId 报告主键
     * @return -
     */
    List<ReportBusinessActivityDetailEntity> queryReportBusinessActivityDetailByReportId(@Param("reportId") Long reportId);

    /**
     * 根据报告主键查询关联的业务活动总数
     *
     * @param reportId 报告主键
     * @return 报告主键-总数
     */
    Map<String, Object> selectCountByReportId(Long reportId);
}
