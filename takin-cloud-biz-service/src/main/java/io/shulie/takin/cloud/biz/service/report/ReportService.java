package io.shulie.takin.cloud.biz.service.report;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.Metrices;
import io.shulie.takin.cloud.biz.output.report.ReportOutput;
import io.shulie.takin.cloud.common.bean.sla.WarnQueryParam;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTrendDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;

/**
 * @author 数列科技
 */
public interface ReportService {

    /**
     * 报告列表
     *
     * @param param -
     * @return -
     */
    PageInfo<CloudReportDTO> listReport(ReportQueryParam param);

    /**
     * 报告详情
     *
     * @param reportId 报告主键
     * @return -
     */
    ReportDetailOutput getReportByReportId(Long reportId);

    /**
     * 报告链路趋势
     *
     * @param reportTrendQuery -
     * @return -
     */
    ReportTrendDTO queryReportTrend(ReportTrendQueryParam reportTrendQuery);

    /**
     * 实况报表
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportDetailOutput tempReportDetail(Long sceneId);

    /**
     * 实况链路趋势
     *
     * @param reportTrendQuery -
     * @return -
     */
    ReportTrendDTO queryTempReportTrend(ReportTrendQueryParam reportTrendQuery);

    /**
     * 警告列表
     *
     * @param param -
     * @return -
     */
    PageInfo<WarnDetailOutput> listWarn(WarnQueryParam param);

    /**
     * 查询报告中的业务活动
     *
     * @param reportId 报告主键
     * @return -
     */
    List<BusinessActivityDTO> queryReportActivityByReportId(Long reportId);

    /**
     * 查询报告中的业务活动
     *
     * @param sceneId 场景主键
     * @return -
     */
    List<BusinessActivityDTO> queryReportActivityBySceneId(Long sceneId);

    /**
     * 获取业务活动摘要列表
     *
     * @param reportId 报告主键
     * @return -
     */
    List<BusinessActivitySummaryBean> getBusinessActivitySummaryList(Long reportId);

    /**
     * 获取报告的业务活动数量和压测通过数量
     *
     * @param reportId 报告主键
     * @return -
     */
    Map<String, Object> getReportCount(Long reportId);

    /**
     * 查询正在生成的报告
     *
     * @return -
     */
    Long queryRunningReport();

    /**
     * 获取运行中的报告列表
     *
     * @return -
     */
    List<Long> queryListRunningReport();

    /**
     * 锁定报告
     *
     * @param reportId 报告主键
     * @return -
     */
    Boolean lockReport(Long reportId);

    /**
     * 解锁报告
     *
     * @param reportId 报告主键
     * @return -
     */
    Boolean unLockReport(Long reportId);

    /**
     * 客户端调，报告完成
     *
     * @param reportId 报告主键
     * @return -
     */
    Boolean finishReport(Long reportId);

    /**
     * 强制关闭报告
     *
     * @param reportId 报告主键
     */
    void forceFinishReport(Long reportId);

    /**
     * 新增 customerId
     *
     * @param reportId   报告主键
     * @param sceneId    场景主键
     * @param customerId 租户主键
     * @return -
     */
    List<Metrices> metric(Long reportId, Long sceneId, Long customerId);

    /**
     * 更新扩展字段
     *
     * @param reportId 报告主键
     * @param status   状态
     * @param errKey   错误key
     * @param errMsg   错误message
     */
    void updateReportFeatures(Long reportId, Integer status, String errKey, String errMsg);

    /**
     * 创建告警
     *
     * @param input -
     */
    void addWarn(WarnCreateInput input);

    /**
     * 更新报告是否通过
     *
     * @param input -
     */
    void updateReportConclusion(UpdateReportConclusionInput input);

    /**
     * 更新sla数据
     *
     * @param input -
     */
    void updateReportSlaData(UpdateReportSlaDataInput input);

    /**
     * 获取报告
     *
     * @param id 报告主键
     * @return -
     */
    ReportOutput selectById(Long id);

    /**
     * 更新场景启动失败的报告的状态
     * @param sceneId 场景ID
     * @param reportId 报告ID
     * @param errorMsg 异常信息
     */
    void updateReportOnSceneStartFailed(Long sceneId,Long reportId,String errorMsg);
}
