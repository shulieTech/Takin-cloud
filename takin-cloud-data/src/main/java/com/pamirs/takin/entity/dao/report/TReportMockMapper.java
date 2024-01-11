package com.pamirs.takin.entity.dao.report;

import com.pamirs.takin.entity.domain.entity.report.ReportMock;
import io.shulie.takin.cloud.sdk.model.request.report.ReportMockQueryReq;

import java.util.List;

/**
 * @author -
 */
public interface TReportMockMapper {

    List<ReportMock> listReportMock(ReportMockQueryReq param);
}
