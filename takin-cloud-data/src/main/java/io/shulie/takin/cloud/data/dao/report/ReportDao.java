package io.shulie.takin.cloud.data.dao.report;

import java.util.List;

import io.shulie.takin.cloud.data.param.report.ReportDataQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.data.dao.report
 * @date 2020/12/17 3:30 下午
 */
public interface ReportDao {
    /**
     * 获取列表
     * @param param
     * @return
     */
    List<ReportResult> getList(ReportDataQueryParam param);

    /**
     * 获取报告
     * @param id
     * @return
     */
    ReportResult selectById(Long id);

    /**
     * 获取当前场景最新一条报告
     *
     * @param sceneId
     *
     * @return
     */
    ReportResult getRecentlyReport(Long sceneId);

    /**
     * 更新通过是否通过
     * @param param
     */
    void updateReportConclusion(ReportUpdateConclusionParam param);

    /**
     * 更新报告
     * @param param
     */
    void updateReport(ReportUpdateParam param);

    /**
     * 完成报告
     * @param reportId
     *
     */
    void finishReport(Long reportId);

    /**
     * 锁报告
     * @param resultId
     * @param lock
     */
    void updateReportLock(Long resultId, Integer lock);



    /**
     * 根据场景ID获取（临时）压测中的报告ID
     *
     * @param sceneId
     * @return
     */
    ReportResult getTempReportBySceneId(Long sceneId);


    /**
     * 根据场景ID获取压测中的报告ID
     *
     * @param sceneId
     * @return
     */
    ReportResult getReportBySceneId(Long sceneId);
}
