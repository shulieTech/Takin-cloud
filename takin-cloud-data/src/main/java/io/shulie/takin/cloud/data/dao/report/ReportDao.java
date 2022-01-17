package io.shulie.takin.cloud.data.dao.report;

import java.util.Date;
import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.param.report.ReportInsertParam;
import io.shulie.takin.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;

/**
 * @author 无涯
 * @date 2020/12/17 3:30 下午
 */
public interface ReportDao {

    int insert(ReportInsertParam param);

    /**
     * 获取列表
     *
     * @param param -
     * @return -
     */
    List<ReportResult> queryReportList(ReportQueryParam param);

    /**
     * 获取报告
     *
     * @param id 报告主键
     * @return -
     */
    ReportResult selectById(Long id);

    /**
     * 获取当前场景最新一条报告
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getRecentlyReport(Long sceneId);

    /**
     * 更新通过是否通过
     *
     * @param param 入参
     */
    void updateReportConclusion(ReportUpdateConclusionParam param);

    /**
     * 更新报告
     *
     * @param param 参数
     */
    void updateReport(ReportUpdateParam param);

    /**
     * 完成报告
     *
     * @param reportId 报告主键
     */
    void finishReport(Long reportId);

    /**
     * 锁报告
     *
     * @param resultId -
     * @param lock     -
     */
    void updateReportLock(Long resultId, Integer lock);

    /**
     * 根据场景ID获取（临时）压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getTempReportBySceneId(Long sceneId);

    /**
     * 根据场景ID获取压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getReportBySceneId(Long sceneId);


    /**
     * 更新报告结束时间
     * @param resultId
     * @param endTime
     */
    void updateReportEndTime(Long resultId, Date endTime);


    /**
     * 查询报告关联的节点信息
     * @param reportId 报告ID
     * @param nodeType 节点类型
     * @return
     */
    List<ReportBusinessActivityDetailEntity> getReportBusinessActivityDetailsByReportId(Long reportId, NodeTypeEnum nodeType);

    /**
     * 根据场景ID查询正在运行的压测报告
     * @param sceneIds 场景ID
     * @return
     */
    List<ReportEntity> queryReportBySceneIds(List<Long> sceneIds);

    List<ReportBusinessActivityDetailEntity> getActivityByReportIds(List<Long> reportIds);
}
