package io.shulie.takin.cloud.data.dao.report;

import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;

/**
 * @author moriarty
 */
public interface ReportBusinessActivityDetailDao {

    int insert(ReportBusinessActivityDetailEntity activityDetail);

    int update(ReportBusinessActivityDetailEntity activityDetail);

    ReportBusinessActivityDetailEntity selectById(Long id);

    List<ReportBusinessActivityDetailEntity> selectDetailsByReportId(Long reportId);
}
