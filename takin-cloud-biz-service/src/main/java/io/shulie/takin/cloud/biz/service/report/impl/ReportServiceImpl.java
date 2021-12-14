package io.shulie.takin.cloud.biz.service.report.impl;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Calendar;
import java.time.Duration;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.common.bean.scenemanage.ScriptNodeSummaryBean;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.open.req.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.open.req.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.open.resp.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.open.resp.report.ReportTrendResp;
import io.shulie.takin.cloud.open.resp.report.ScriptNodeTreeResp;
import io.shulie.takin.ext.content.asset.RealAssectBillExt;
import io.shulie.takin.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.ext.content.response.Response;
import io.shulie.takin.ext.content.script.ScriptNode;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.impl.TimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.github.pagehelper.PageHelper;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.ext.api.AssetExtApi;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import io.shulie.takin.utils.json.JsonHelper;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.MapUtils;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.common.bean.sla.SlaBean;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import org.apache.commons.collections4.CollectionUtils;
import io.shulie.takin.cloud.common.utils.TestTimeUtil;
import com.pamirs.takin.entity.dao.report.TReportMapper;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.ext.content.asset.AssetInvoiceExt;
import com.pamirs.takin.entity.domain.dto.report.Metrices;
import org.springframework.beans.factory.annotation.Value;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.influxdb.InfluxDBUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.plugin.framework.core.PluginManager;
import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.bo.scenemanage.WarnBO;
import io.shulie.takin.cloud.common.bean.sla.WarnQueryParam;
import io.shulie.takin.cloud.biz.output.report.ReportOutput;
import io.shulie.takin.cloud.biz.cloudserver.ReportConverter;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.common.bean.scenemanage.DataBean;
import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import com.pamirs.takin.entity.domain.dto.report.StatReportDTO;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import org.springframework.transaction.annotation.Transactional;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.dao.scene.manage.TWarnDetailMapper;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.service.scene.ReportEventService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskEventService;
import io.shulie.takin.cloud.common.bean.scenemanage.StopReasonBean;
import io.shulie.takin.cloud.common.bean.scenemanage.DistributeBean;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import com.pamirs.takin.entity.domain.dto.report.StatInspectReportDTO;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageResult;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneStopReasonEnum;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import com.pamirs.takin.entity.dao.report.TReportBusinessActivityDetailMapper;
import com.pamirs.takin.entity.domain.entity.report.ReportBusinessActivityDetail;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Resource
    ReportDao reportDao;
    @Resource
    InfluxWriter influxWriter;
    @Resource
    TReportMapper tReportMapper;
    @Resource
    PluginManager pluginManager;
    @Resource
    SceneManageDAO sceneManageDao;
    @Resource
    RedisClientUtils redisClientUtils;
    @Resource
    SceneTaskService sceneTaskService;
    @Resource
    TWarnDetailMapper tWarnDetailMapper;
    @Resource
    ReportEventService reportEventService;
    @Resource
    SceneManageService sceneManageService;
    @Resource
    SceneTaskEventService sceneTaskEventService;
    @Resource
    TReportBusinessActivityDetailMapper tReportBusinessActivityDetailMapper;

    @Value("${report.aggregation.interval}")
    private String reportAggregationInterval;
    /**
     * 压测场景强行关闭预留时间
     */
    @Value("${scene.pressure.forceCloseTime: 20}")
    private Integer forceCloseTime;

    public static final String COMPARE = "<=";

    @Override
    public PageInfo<CloudReportDTO> listReport(ReportQueryParam param) {
        // 补充数据
        CloudPluginUtils.fillReportData(param, null);

        PageHelper.startPage(param.getCurrentPage() + 1, param.getPageSize());
        //默认只查询普通场景的报告
        if (param.getType() == null) {
            param.setType(0);
        }
        List<Report> reportList = tReportMapper.listReport(param);
        if (CollectionUtils.isEmpty(reportList)) {
            return new PageInfo<>(new ArrayList<>(0));
        }
        PageInfo<Report> old = new PageInfo<>(reportList);
        Map<Long, String> errorMsgMap = new HashMap<>();
        for (Report report : reportList) {
            if (report.getConclusion() != null && report.getConclusion() == 0 && report.getFeatures() != null) {
                JSONObject jsonObject = JSON.parseObject(report.getFeatures());
                String key = "error_msg";
                if (jsonObject.containsKey(key)) {
                    errorMsgMap.put(report.getId(), jsonObject.getString(key));
                }
            }
        }
        List<CloudReportDTO> list = ReportConverter.INSTANCE.ofReport(reportList);
        for (CloudReportDTO dto : list) {
            if (errorMsgMap.containsKey(dto.getId())) {
                dto.setErrorMsg(errorMsgMap.get(dto.getId()));
            }
        }
        List<Long> customerIds = list.stream().map(CloudReportDTO::getCustomerId)
            .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(customerIds)) {
            // 获取租户数据
            Map<Long, String> userMap = CloudPluginUtils.getUserNameMap(customerIds);
            list.forEach(data -> CloudPluginUtils.fillCustomerName(data, userMap));
        }
        PageInfo<CloudReportDTO> data = new PageInfo<>(list);
        data.setTotal(old.getTotal());

        return data;
    }

    @Override
    public ReportDetailOutput getReportByReportId(Long reportId) {
        ReportResult report = reportDao.selectById(reportId);
        if (report == null) {
            log.warn("获取报告异常，报告数据不存在。报告ID：{}", reportId);
            return null;
        }
        ReportDetailOutput detail = ReportConverter.INSTANCE.ofReportDetail(report);

        //补充操作用户 && 客户id
        //CloudPluginUtils.fillReportData(report, detail);

        //警告列表
        List<WarnBean> warnList = listWarn(reportId);
        detail.setTaskStatus(report.getStatus());
        if (CollectionUtils.isNotEmpty(warnList)) {
            detail.setWarn(warnList);
            detail.setTotalWarn(warnList.stream().mapToLong(WarnBean::getTotal).sum());
        }
        if (StringUtils.isNotEmpty(report.getFeatures())) {
            detail.setConclusionRemark(
                JSON.parseObject(report.getFeatures()).getString(ReportConstants.FEATURES_ERROR_MSG));
        }
        detail.setTestTotalTime(TestTimeUtil.format(report.getStartTime(), report.getEndTime()));
        List<ScriptNodeSummaryBean> reportNodeDetail = getReportNodeDetail(report.getScriptNodeTree(), reportId);
        detail.setNodeDetail(reportNodeDetail);

        List<BusinessActivitySummaryBean> businessActivities = new ArrayList<>();
        buildFailActivitiesByNodeDetails(reportNodeDetail, businessActivities);
        detail.setBusinessActivity(businessActivities);
        //任务没有完成，提示用户正在生成中
        if (report.getStatus() != ReportConstants.FINISH_STATUS) {
            detail.setTaskStatus(ReportConstants.RUN_STATUS);
        }
        // sla转换对象
        if (StringUtils.isNotBlank(report.getFeatures())) {
            JSONObject jsonObject = JSON.parseObject(report.getFeatures());
            if (jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)) {
                detail.setSlaMsg(
                    JsonHelper.json2Bean(jsonObject.getString(ReportConstants.SLA_ERROR_MSG), SlaBean.class));
            }
        }
        return detail;
    }

    private void buildFailActivitiesByNodeDetails(List<ScriptNodeSummaryBean> reportNodeDetail,
        List<BusinessActivitySummaryBean> result) {
        if (CollectionUtils.isEmpty(reportNodeDetail)) {
            return;
        }
        for (ScriptNodeSummaryBean bean : reportNodeDetail) {
            if (bean.getActivityId() > -1) {
                BusinessActivitySummaryBean summaryBean = new BusinessActivitySummaryBean();
                summaryBean.setBusinessActivityId(bean.getActivityId());
                summaryBean.setBusinessActivityName(bean.getTestName());
                summaryBean.setBindRef(bean.getXpathMd5());
                summaryBean.setApplicationIds(bean.getApplicationIds());
                summaryBean.setAvgRT(bean.getAvgRt());
                summaryBean.setMinRt(bean.getMinRt());
                summaryBean.setMaxRt(bean.getMaxRt());
                summaryBean.setMaxTps(bean.getMaxTps());
                summaryBean.setPassFlag(bean.getPassFlag());
                summaryBean.setSa(bean.getSa());
                summaryBean.setSuccessRate(bean.getSuccessRate());
                summaryBean.setTotalRequest(bean.getTotalRequest());
                summaryBean.setTps(bean.getTps());
                result.add(summaryBean);
                if (CollectionUtils.isNotEmpty(bean.getChildren())){
                    buildFailActivitiesByNodeDetails(bean.getChildren(),result);
                }
            } else if (CollectionUtils.isNotEmpty(bean.getChildren())) {
                buildFailActivitiesByNodeDetails(bean.getChildren(), result);
            }
        }
    }

    private List<ScriptNodeSummaryBean> getReportNodeDetail(String scriptNodeTree, Long reportId) {
        List<ReportBusinessActivityDetail> activities = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportId);
        return getScriptNodeSummaryBeans(scriptNodeTree, activities);
    }

    @Override
    public ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery) {
        return queryReportTrend(reportTrendQuery, false);
    }

    @Override
    public ReportDetailOutput tempReportDetail(Long sceneId) {
        long start = System.currentTimeMillis();
        ReportDetailOutput reportDetail = new ReportDetailOutput();

        ReportResult reportResult = reportDao.getTempReportBySceneId(sceneId);
        if (reportResult == null) {
            reportDetail.setTaskStatus(ReportConstants.FINISH_STATUS);
            return reportDetail;
        }
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(false);
        SceneManageWrapperOutput wrapper = sceneManageService.getSceneManage(sceneId, options);
        reportDetail = ReportConverter.INSTANCE.ofReportDetail(reportResult);
        reportDetail.setUserId(wrapper.getUserId());

        // 补充停止原因
        reportDetail.setStopReasons(getStopReasonBean(sceneId, reportResult.getId()));

        // 查询sla熔断数据
        ReportDetailOutput detailOutput = this.getReportByReportId(reportResult.getId());
        reportDetail.setSlaMsg(detailOutput.getSlaMsg());
        String testPlanXpathMD5 = getTestPlanXpathMD5(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMD5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
            : testPlanXpathMD5;
        StatReportDTO statReport = statTempReport(sceneId, reportResult.getId(), reportResult.getCustomerId(),
            transaction);
        if (statReport == null) {
            log.warn("实况报表:[{}]，暂无数据", reportResult.getId());
        } else {
            reportDetail.setTotalRequest(statReport.getTotalRequest());
            reportDetail.setAvgRt(statReport.getAvgRt());
            reportDetail.setAvgTps(statReport.getTps());
            reportDetail.setSa(statReport.getSa());
            reportDetail.setSuccessRate(statReport.getSuccessRate());
            reportDetail.setAvgConcurrent(statReport.getAvgConcurrenceNum());
        }
        reportDetail.setSceneName(wrapper.getPressureTestSceneName());
        //最大并发
        Integer maxConcurrence = getMaxConcurrence(sceneId, reportResult.getId(), reportResult.getCustomerId(),
            transaction);
        if (Objects.nonNull(statReport) && Objects.nonNull(statReport.getAvgConcurrenceNum())) {
            maxConcurrence = Math.max(maxConcurrence, statReport.getAvgConcurrenceNum().intValue());
        }
        reportDetail.setConcurrent(maxConcurrence);
        reportDetail.setTotalWarn(tWarnDetailMapper.countReportTotalWarn(reportResult.getId()));
        reportDetail.setTaskStatus(reportResult.getStatus());
        reportDetail.setTestTime(getTaskTime(reportResult.getStartTime(), new Date(), wrapper.getTotalTestTime()));
        reportDetail.setTestTotalTime(
            String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

        // 补充操作人
        CloudPluginUtils.fillReportData(reportResult, reportDetail);
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportResult.getId());
        if (StringUtils.isNotBlank(reportResult.getScriptNodeTree())) {
            String nodeTree = reportResult.getScriptNodeTree();
            Map<String, Map<String, Object>> resultMap = new HashMap<>(reportBusinessActivityDetails.size());
            reportBusinessActivityDetails.stream()
                .filter(Objects::nonNull)
                .forEach(ref -> {
                    StatReportDTO data = statTempReport(sceneId, reportResult.getId(), reportResult.getCustomerId(),
                        ref.getBindRef());
                    Map<String, Object> objectMap = fillTempMap(data, ref);
                    resultMap.put(ref.getBindRef(), objectMap);
                });
            String resultTree = JsonPathUtil.putNodesToJson(nodeTree, resultMap);
            reportDetail.setNodeDetail(JsonUtil.parseArray(resultTree,
                ScriptNodeSummaryBean.class));
        } else {
            List<ScriptNodeSummaryBean> nodeDetails = reportBusinessActivityDetails.stream()
                .filter(Objects::nonNull)
                .map(ref -> {
                    StatReportDTO data = statTempReport(sceneId, reportResult.getId(), reportResult.getCustomerId(),
                        ref.getBindRef());
                    return new ScriptNodeSummaryBean() {{
                        setName(ref.getBusinessActivityName());
                        setTestName(ref.getBusinessActivityName());
                        setXpathMd5(ref.getBindRef());
                        if (Objects.nonNull(data)) {
                            setAvgRt(new DataBean(data.getAvgRt(), ref.getTargetRt()));
                            setSa(new DataBean(data.getSa(), ref.getTargetSa()));
                            setTps(new DataBean(data.getTps(), ref.getTargetTps()));
                            setSuccessRate(new DataBean(data.getSuccessRate(), ref.getTargetSuccessRate()));
                            setAvgConcurrenceNum(data.getAvgConcurrenceNum());
                            setTotalRequest(data.getTotalRequest());
                        } else {
                            setAvgRt(new DataBean("0", ref.getTargetRt()));
                            setSa(new DataBean("0", ref.getTargetSa()));
                            setTps(new DataBean("0", ref.getTargetTps()));
                            setSuccessRate(new DataBean("0", ref.getTargetSuccessRate()));
                            setAvgConcurrenceNum(new BigDecimal(0));
                            setTotalRequest(0L);
                        }
                    }};
                }).collect(Collectors.toList());
            reportDetail.setNodeDetail(nodeDetails);

        }
        //检查任务是否超时
        boolean taskIsTimeOut = checkSceneTaskIsTimeOut(reportResult, wrapper);
        if (wrapper.getStatus().intValue() == SceneManageStatusEnum.PTING.getValue().intValue() && taskIsTimeOut) {
            log.info("报表[{}]超时，通知调度马上停止压测", reportResult.getId());
            //报告正在生成中
            reportDetail.setTaskStatus(ReportConstants.RUN_STATUS);
            //重置时间
            reportDetail.setTestTime(
                String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

            //主动通知暂停事件，注意有可能会被多次触发
            sceneTaskEventService.callStopEvent(reportResult);
        }
        log.info("实时监测metric数据：tempReportDetail-运行时间：{}", System.currentTimeMillis() - start);
        return reportDetail;
    }

    private String getTestPlanXpathMD5(String scriptNodeTree) {
        if (StringUtils.isBlank(scriptNodeTree)) {
            return null;
        }
        List<ScriptNode> currentNodeByType = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.TEST_PLAN.name());
        if (CollectionUtils.isNotEmpty(currentNodeByType) && currentNodeByType.size() == 1) {
            return currentNodeByType.get(0).getXpathMd5();
        }
        return null;
    }

    /**
     * 组装停止原因
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @return -
     */
    private List<StopReasonBean> getStopReasonBean(Long sceneId, Long reportId) {
        List<StopReasonBean> stopReasons = Lists.newArrayList();

        // 查询sla熔断数据
        ReportDetailOutput detailOutput = this.getReportByReportId(reportId);
        if (detailOutput.getSlaMsg() != null) {
            StopReasonBean slaReasonBean = new StopReasonBean();
            slaReasonBean.setType(SceneStopReasonEnum.SLA.getType());
            slaReasonBean.setDescription(SceneStopReasonEnum.toSlaDesc(detailOutput.getSlaMsg()));
            stopReasons.add(slaReasonBean);
        }
        // 检查压力节点 情况
        String pressureNodeKey = String.format(SceneTaskRedisConstants.PRESSURE_NODE_ERROR_KEY + "%s_%s", sceneId,
            reportId);
        Object pressureNodeStartError = redisClientUtils.hmget(pressureNodeKey,
            SceneTaskRedisConstants.PRESSURE_NODE_START_ERROR);
        if (Objects.nonNull(pressureNodeStartError)) {
            // 组装压力节点异常显示数据
            StopReasonBean stopReasonBean = new StopReasonBean();
            stopReasonBean.setType(SceneStopReasonEnum.PRESSURE_NODE.getType());
            stopReasonBean.setDescription(SceneStopReasonEnum.toDesc(pressureNodeStartError.toString()));
            stopReasons.add(stopReasonBean);
            //  持久化
            ReportResult reportResult = reportDao.selectById(reportId);
            getReportFeatures(reportResult, ReportConstants.PRESSURE_MSG, pressureNodeStartError.toString());
            ReportUpdateParam param = new ReportUpdateParam();
            param.setId(reportId);
            param.setFeatures(reportResult.getFeatures());
            param.setGmtUpdate(new Date());
            reportDao.updateReport(param);
            //sceneTaskService.stop(sceneId);
        }

        // 查询压测引擎是否有异常
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        Object errorObj = redisClientUtils.hmget(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
        if (Objects.nonNull(errorObj)) {
            // 组装压测引擎异常显示数据
            StopReasonBean engineReasonBean = new StopReasonBean();
            engineReasonBean.setType(SceneStopReasonEnum.ENGINE.getType());
            engineReasonBean.setDescription(SceneStopReasonEnum.toEngineDesc(errorObj.toString()));
            stopReasons.add(engineReasonBean);
            //  持久化
            ReportResult reportResult = reportDao.selectById(reportId);
            getReportFeatures(reportResult, ReportConstants.PRESSURE_MSG, errorObj.toString());
            ReportUpdateParam param = new ReportUpdateParam();
            param.setId(reportId);
            param.setFeatures(reportResult.getFeatures());
            param.setGmtUpdate(new Date());
            reportDao.updateReport(param);
            // 进行中断操作
            log.info("检测到压测引擎异常，触发中断场景【{}】压测,异常原因：{}", sceneId, errorObj);
            sceneTaskService.stop(sceneId);
        }
        return stopReasons;
    }

    @Override
    public ReportTrendResp queryTempReportTrend(ReportTrendQueryReq reportTrendQuery) {
        return queryReportTrend(reportTrendQuery, true);
    }

    @Override
    public PageInfo<WarnDetailOutput> listWarn(WarnQueryParam param) {
        PageHelper.startPage(param.getCurrentPage() + 1, param.getPageSize());
        List<WarnDetail> warnDetailList = tWarnDetailMapper.listWarn(param);
        if (CollectionUtils.isEmpty(warnDetailList)) {
            return new PageInfo<>();
        }
        PageInfo<WarnDetail> old = new PageInfo<>(warnDetailList);
        List<WarnDetailOutput> list = ReportConverter.INSTANCE.ofWarnDetail(warnDetailList);
        PageInfo<WarnDetailOutput> data = new PageInfo<>(list);
        data.setTotal(old.getTotal());
        return data;
    }

    @Override
    public List<BusinessActivityDTO> queryReportActivityByReportId(Long reportId) {
        List<ReportBusinessActivityDetail> reportBusinessActivityDetailList = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportId);
        return ReportConverter.INSTANCE.ofBusinessActivity(reportBusinessActivityDetailList);
    }

    @Override
    public List<BusinessActivityDTO> queryReportActivityBySceneId(Long sceneId) {
        ReportResult reportResult = reportDao.getReportBySceneId(sceneId);
        if (reportResult != null) {
            return queryReportActivityByReportId(reportResult.getId());
        }
        return Lists.newArrayList();
    }

    /**
     * SLA警告信息
     *
     * @return -
     */
    private List<WarnBean> listWarn(Long reportId) {
        List<WarnBO> warnBOList = tWarnDetailMapper.summaryWarnByReportId(reportId);
        return ReportConverter.INSTANCE.ofWarn(warnBOList);
    }

    /**
     * 业务活动概况
     *
     * @return -
     */
    @Override
    public NodeTreeSummaryResp getNodeSummaryList(Long reportId) {
        //List<BusinessActivitySummaryBean> list = Lists.newArrayList();
        //查询业务活动的概况
        List<ReportBusinessActivityDetail> reportBusinessActivityDetailList = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportId);
        NodeTreeSummaryResp resp = new NodeTreeSummaryResp();
        if (CollectionUtils.isEmpty(reportBusinessActivityDetailList)) {
            return resp;
        }
        ReportResult reportResult = reportDao.selectById(reportId);
        if (Objects.isNull(reportResult)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "未查询到报告" + reportId);
        }
        resp.setScriptNodeSummaryBeans(getScriptNodeSummaryBeans(reportResult.getScriptNodeTree(),
            reportBusinessActivityDetailList));
        return resp;
    }

    /**
     * 处理节点链路明细
     *
     * @param nodeTree 节点树
     * @param details  业务活动详情
     * @return -
     */
    private List<ScriptNodeSummaryBean> getScriptNodeSummaryBeans(String nodeTree,
        List<ReportBusinessActivityDetail> details) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        if (StringUtils.isNotBlank(nodeTree)) {
            details.stream().filter(Objects::nonNull)
                .forEach(detail -> {
                    Map<String, Object> objectMap = fillReportMap(detail);
                    if (Objects.nonNull(objectMap)) {
                        resultMap.put(detail.getBindRef(), objectMap);
                    }
                });
            if (resultMap.size() > 0) {
                String nodesToJson = JsonPathUtil.putNodesToJson(nodeTree, resultMap);
                return JSONArray.parseArray(nodesToJson, ScriptNodeSummaryBean.class);
            }
        }
        return details.stream().filter(Objects::nonNull)
            .map(detail -> {
                ScriptNodeSummaryBean bean = new ScriptNodeSummaryBean();
                bean.setXpathMd5(detail.getBindRef());
                bean.setTestName(detail.getBusinessActivityName());
                bean.setTotalRequest(detail.getRequest());
                bean.setAvgConcurrenceNum(detail.getAvgConcurrenceNum());
                bean.setSuccessRate(new DataBean(detail.getSuccessRate(), detail.getTargetSuccessRate()));
                bean.setTps(new DataBean(detail.getTps(), detail.getTargetTps()));
                bean.setMaxTps(detail.getMaxTps());
                bean.setAvgRt(new DataBean(detail.getRt(), detail.getTargetRt()));
                bean.setMaxRt(detail.getMaxRt());
                bean.setMinRt(detail.getMinRt());
                bean.setPassFlag((Optional.ofNullable(detail.getPassFlag()).orElse(0)));
                bean.setDistribute(getDistributes(detail.getRtDistribute()));
                bean.setApplicationIds(detail.getApplicationIds());
                bean.setActivityId(detail.getBusinessActivityId());
                return bean;
            }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getReportCount(Long reportId) {
        Map<String, Object> dataMap = tReportBusinessActivityDetailMapper.selectCountByReportId(reportId);
        if (MapUtils.isEmpty(dataMap)) {
            dataMap = Maps.newHashMap();
        }
        dataMap.put("warnCount", tWarnDetailMapper.countReportTotalWarn(reportId));
        return dataMap;
    }

    @Override
    public Long queryRunningReport() {
        Report report = tReportMapper.selectOneRunningReport();
        return report == null ? null : report.getId();
    }

    @Override
    public List<Long> queryListRunningReport() {
        List<Report> report = tReportMapper.selectListRunningReport();
        return CollectionUtils.isEmpty(report) ? null : report.stream().map(Report::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryListPressuringReport() {
        List<Report> report = tReportMapper.selectListPressuringReport();
        return CollectionUtils.isEmpty(report) ? null : report.stream().map(Report::getId).collect(Collectors.toList());
    }

    @Override
    public Boolean lockReport(Long reportId) {
        log.info("web -> cloud lock reportId【{}】,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        if (checkReportError(reportResult)) {
            return false;
        }
        if (ReportConstants.RUN_STATUS != reportResult.getStatus()) {
            return false;
        }
        long lastUpdate = 0;
        if (null != reportResult.getGmtUpdate()) {
            lastUpdate = reportResult.getGmtUpdate().getTime();
        }
        //+10分钟,如果锁了10分钟了，则认为锁失效,可以重新锁
        lastUpdate += 10 * 60 * 1000;
        if (ReportConstants.LOCK_STATUS == reportResult.getLock() && lastUpdate > System.currentTimeMillis()) {
            log.error("异常代码【{}】,异常内容：锁定报告异常 --> 报告{}状态锁定状态，不能再次锁定",
                TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        reportDao.updateReportLock(reportId, ReportConstants.LOCK_STATUS);
        log.info("报告{}锁定成功", reportId);
        return true;
    }

    private boolean checkReportError(ReportResult reportResult) {
        // 锁定报告前提也是要有结束时间
        if (reportResult == null) {
            log.error("io.shulie.takin.cloud.biz.service.report.impl.ReportServiceImpl#checkReportError"
                + "reportResult 是 null");
            return true;
        }
        if (reportResult.getEndTime() == null) {
            log.error("报告{} endTime 为null", reportResult.getId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean unLockReport(Long reportId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult.getType() != PressureSceneEnum.DEFAULT.getCode() && ReportConstants.LOCK_STATUS != reportResult.getLock()) {
            log.error("异常代码【{}】,异常内容：解锁报告异常 --> 报告{}非锁定状态，不能解锁",
                TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        // 解锁
        reportDao.updateReportLock(reportId, ReportConstants.RUN_STATUS);
        log.info("报告{}解锁成功", reportId);
        return true;
    }

    @Override
    public Boolean finishReport(Long reportId) {
        log.info("web -> cloud finish reportId【{}】,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        //只有常规模式需要生成报告内容
        if (reportResult.getType() != PressureSceneEnum.DEFAULT.getCode()){
            reportDao.finishReport(reportId);
            sceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId())
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING,
                        SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
            return true;
        }
        if (checkReportError(reportResult)) {
            return false;
        }
        //判断报告是否已经汇总，如果没有汇总，先汇总报告，然后在更新报告状态
        if (Objects.isNull(reportResult.getEndTime())
            || (Objects.isNull(reportResult.getTotalRequest()) && Objects.isNull(reportResult.getAvgTps()))){
            TaskResult taskResult = new TaskResult();
            taskResult.setSceneId(reportResult.getSceneId());
            taskResult.setTaskId(reportResult.getId());
            taskResult.setCustomerId(reportResult.getCustomerId());
            modifyReport(taskResult);
            reportResult = reportDao.selectById(reportId);
        }
        if (ReportConstants.RUN_STATUS != reportResult.getStatus()) {
            log.info("报告状态不正确：reportId=" + reportId + ", status=" + reportResult.getStatus());
            return false;
        }

        // 两个地方关闭压测引擎，版本不同，关闭方式不同
        //更新场景 压测引擎停止 ---> 待启动
        SceneManageResult sceneManage = sceneManageDao.getSceneById(reportResult.getSceneId());
        //如果是强制停止 不需要更新
        log.info("finish scene {}, state :{}", reportResult.getSceneId(), Optional.ofNullable(sceneManage)
            .map(SceneManageResult::getStatus)
            .map(SceneManageStatusEnum::getSceneManageStatusEnum)
            .map(SceneManageStatusEnum::getDesc).orElse("未找到场景"));
        if (sceneManage != null && !sceneManage.getType().equals(SceneManageStatusEnum.FORCE_STOP.getValue())) {
            sceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId())
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING,
                        SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }
        //报告结束应该放在场景之后
        reportDao.finishReport(reportId);
        log.info("报告{} finish done", reportId);

        return true;
    }

    @Override
    public void forceFinishReport(Long reportId) {
        // 更新场景
        ReportResult reportResult = reportDao.selectById(reportId);
        // 完成报告
        if (reportResult.getStatus() != ReportConstants.FINISH_STATUS) {
            log.info("{}报告触发强制停止", reportId);
            reportDao.finishReport(reportId);
        }

        sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(),
            reportResult.getCustomerId())
            .checkEnum(SceneManageStatusEnum.getAll()).updateEnum(SceneManageStatusEnum.FORCE_STOP).build());

    }

    /**
     * 实况报表取值
     *
     * @return -
     */
    private StatReportDTO statTempReport(Long sceneId, Long reportId, Long customerId, String transaction) {
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " count as totalRequest, fail_count as failRequest, avg_tps as tps , avg_rt as avgRt, sa_count as saCount,"
                + " active_threads as avgConcurrenceNum");
        influxDbSql.append(" from ");
        influxDbSql.append(InfluxDBUtil.getMeasurement(sceneId, reportId, customerId));
        influxDbSql.append(" where ");
        influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");
        influxDbSql.append(" order by time desc limit 1");
        return influxWriter.querySingle(influxDbSql.toString(), StatReportDTO.class);
    }

    /**
     * 获取最大并发数
     *
     * @param sceneId     场景ID
     * @param reportId    报告ID
     * @param customerId  租户ID
     * @param transaction 节点
     * @return -
     */
    private Integer getMaxConcurrence(Long sceneId, Long reportId, Long customerId, String transaction) {
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(" max(active_threads) as maxConcurrenceNum");
        influxDbSql.append(" from ");
        influxDbSql.append(InfluxDBUtil.getMeasurement(sceneId, reportId, customerId));
        influxDbSql.append(" where ");
        influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");
        influxDbSql.append(" order by time desc limit 1");
        StatReportDTO statReportDTO = influxWriter.querySingle(influxDbSql.toString(), StatReportDTO.class);
        if (Objects.nonNull(statReportDTO)) {
            return statReportDTO.getMaxConcurrenceNum();
        }
        return 0;
    }

    /**
     * 巡检报告取值
     */
    private StatInspectReportDTO statInspectReport(Long sceneId, Long reportId, Long customerId, String transaction,
        String startTime, String endTime) {
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " sum(count) as totalRequest,mean(avg_tps) as avgTps , sum(sum_rt)/sum(count) as avgRt , mean"
                + "(success_rate) as avgSuccessRate");
        influxDbSql.append(" from ");
        influxDbSql.append(InfluxDBUtil.getMeasurement(sceneId, reportId, customerId));
        influxDbSql.append(" where ");
        influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");
        influxDbSql.append(" and time >= ").append("'").append(startTime).append("'");
        influxDbSql.append(" and time <= ").append("'").append(endTime).append("' tz('Asia/Shanghai')");
        return influxWriter.querySingle(influxDbSql.toString(), StatInspectReportDTO.class);
    }

    /**
     * 查看报表实况
     *
     * @param reportTrendQuery 报表查询对象
     * @param isTempReport     是否实况报表
     * @return -
     */
    private ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery, boolean isTempReport) {
        long start = System.currentTimeMillis();
        ReportTrendResp reportTrend = new ReportTrendResp();
        ReportResult reportResult;
        if (isTempReport) {
            reportResult = reportDao.getTempReportBySceneId(reportTrendQuery.getSceneId());
        } else {
            reportResult = reportDao.selectById(reportTrendQuery.getReportId());
        }
        if (reportResult == null) {
            return new ReportTrendResp();
        }
        String testPlanXpathMD5 = getTestPlanXpathMD5(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMD5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
            : testPlanXpathMD5;
        if (StringUtils.isNotBlank(reportTrendQuery.getXpathMd5())) {
            transaction = reportTrendQuery.getXpathMd5();
        }
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " sum(count) as totalRequest, sum(fail_count) as failRequest, mean(avg_tps) as tps , sum(sum_rt)/sum"
                + "(count) as "
                + "avgRt, sum(sa_count) as saCount, count(avg_rt) as recordCount ,mean(active_threads) as "
                + "avgConcurrenceNum ");
        influxDbSql.append(" from ");
        influxDbSql.append(
            InfluxDBUtil.getMeasurement(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId()));
        influxDbSql.append(" where ");
        influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");

        //按配置中的时间间隔分组
        influxDbSql.append(" group by time(").append(reportAggregationInterval).append(")");

        List<StatReportDTO> list = Lists.newArrayList();

        if (StringUtils.isNotEmpty(transaction)) {
            list = influxWriter.query(influxDbSql.toString(), StatReportDTO.class);
        }

        //influxdb 空数据也会返回,需要过滤空数据
        //前端要求的格式
        List<String> time = Lists.newLinkedList();
        List<String> sa = Lists.newLinkedList();
        List<String> avgRt = Lists.newLinkedList();
        List<String> tps = Lists.newLinkedList();
        List<String> successRate = Lists.newLinkedList();
        List<String> concurrent = Lists.newLinkedList();

        list.stream()
            .filter(Objects::nonNull)
            .filter(data -> data.getTps() != null)
            .filter(data -> StringUtils.isNotBlank(data.getTime()))
            .forEach(data -> {
                time.add(getTime(data.getTime()));
                sa.add(NumberUtil.decimalToString(data.getSa()));
                avgRt.add(NumberUtil.decimalToString(data.getAvgRt()));
                tps.add(NumberUtil.decimalToString(data.getTps()));
                successRate.add(NumberUtil.decimalToString(data.getSuccessRate()));
                concurrent.add(NumberUtil.decimalToString(data.getAvgConcurrenceNum()));
            });
        //链路趋势
        reportTrend.setTps(tps);
        reportTrend.setSa(sa);
        reportTrend.setSuccessRate(successRate);
        reportTrend.setRt(avgRt);
        reportTrend.setTime(time);
        reportTrend.setConcurrent(concurrent);
        log.info("实时监测链路趋势：queryReportTrend-运行时间：{}", System.currentTimeMillis() - start);

        return reportTrend;
    }

    /**
     * 日期格式化ck
     *
     * @return -
     */
    private String getTime(String time) {
        long date = TimeUtil.fromInfluxDBTimeFormat(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        return cn.hutool.core.date.DateUtil.formatTime(calendar.getTime());
    }

    /**
     * 压测时间格式化
     *
     * @return -
     */
    private String getTaskTime(Date startTime, Date endTime, Long totalTestTime) {
        LocalDateTime start = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long seconds = Duration.between(start, end).getSeconds();
        if (seconds > totalTestTime) {
            seconds = totalTestTime;
        }
        long minutes = seconds / 60;
        long second = seconds % 60;
        return String.format("%d'%d\"", minutes, second < 0 ? 0 : second);
    }

    /**
     * 检查场景状态是否超时
     */
    public boolean checkSceneTaskIsTimeOut(ReportResult reportResult, SceneManageWrapperOutput scene) {
        long totalTestTime = scene.getTotalTestTime();
        long runTime = DateUtil.between(reportResult.getStartTime(), new Date(), DateUnit.SECOND);
        if (runTime >= totalTestTime + forceCloseTime) {
            log.info("report = {}，runTime = {} , totalTestTime= {},Timeout check", reportResult.getId(), runTime,
                totalTestTime);
            return true;
        }
        return false;
    }

    /**
     * 通过场景iD和报告ID。获取压测中jmeter上报的数据
     *
     * @return -
     */
    @Override
    public List<Metrices> metric(Long reportId, Long sceneId, Long customerId) {
        List<Metrices> metricList = Lists.newArrayList();
        if (StringUtils.isBlank(String.valueOf(reportId))) {
            return metricList;
        }
        try {
            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
            metricList = influxWriter.query(
                "select time,avg_tps as avgTps from " + measurement + " where transaction='all'", Metrices.class);
        } catch (Throwable e) {
            log.error("异常代码【{}】,异常内容：获取压测中jmeter上报的数据异常 --> influxdb数据查询异常: {}",
                TakinCloudExceptionEnum.REPORT_GET_ERROR, e);
        }
        return metricList;
    }

    private void getReportFeatures(ReportResult reportResult, String errKey, String errMsg) {
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(reportResult.getFeatures())) {
            map = JsonHelper.string2Obj(reportResult.getFeatures(), new TypeReference<Map<String, String>>() {
            });
        }
        if (StringUtils.isNotBlank(errMsg)) {
            if (errKey.equals(ReportConstants.SLA_ERROR_MSG) && map.containsKey(ReportConstants.SLA_ERROR_MSG)) {
                return;
            }
            map.compute(errKey, (k, v) -> StringUtils.isBlank(v) ? errMsg : (v + "、" + errMsg));
            reportResult.setFeatures(GsonUtil.gsonToString(map));
        }
    }

    @Override
    public void updateReportFeatures(Long reportId, Integer status, String errKey, String errMsg) {
        ReportResult reportResult = reportDao.selectById(reportId);
        // 完成状态
        reportResult.setStatus(status);
        getReportFeatures(reportResult, errKey, errMsg);
        ReportUpdateParam param = new ReportUpdateParam();
        BeanUtils.copyProperties(reportResult, param);
        reportDao.updateReport(param);
    }

    @Override
    public void updateReportConclusion(UpdateReportConclusionInput input) {
        ReportResult reportResult = reportDao.selectById(input.getId());
        if (reportResult == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告" + input.getId() + "不存在");
        }
        if (StringUtils.isNotBlank(input.getErrorMessage())) {
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, input.getErrorMessage());
        }
        ReportUpdateConclusionParam param = new ReportUpdateConclusionParam();
        BeanUtils.copyProperties(input, param);
        param.setFeatures(reportResult.getFeatures());
        reportDao.updateReportConclusion(param);
    }

    @Override
    public void updateReportSlaData(UpdateReportSlaDataInput input) {
        ReportResult reportResult = reportDao.selectById(input.getReportId());
        if (reportResult == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告" + input.getReportId() + "不存在");
        }
        // 适配
        getReportFeatures(reportResult, ReportConstants.SLA_ERROR_MSG, JsonHelper.bean2Json(input.getSlaBean()));
        ReportUpdateParam param = new ReportUpdateParam();
        param.setId(input.getReportId());
        param.setFeatures(reportResult.getFeatures());
        param.setGmtUpdate(new Date());
        reportDao.updateReport(param);
    }

    /**
     * 生成报告
     * 报告更新，完成压测后的报告更新
     * 原来在场景调度那边，现在把报告放在这里面
     */
    @IntrestFor(event = "finished")
    public void doReportEvent(Event event) {
        try {
            // 等待时间 influxDB完成
            Thread.sleep(2000);
            long start = System.currentTimeMillis();
            TaskResult taskResult = (TaskResult)event.getExt();
            log.info("通知报告模块，开始生成本次压测{}-{}-{}的报告", taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getCustomerId());
            modifyReport(taskResult);
            log.info("本次压测{}-{}-{}的报告生成时间-{}", taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getCustomerId(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：生成报告异常 --> 【通知报告模块】处理finished事件异常: {}",
                TakinCloudExceptionEnum.TASK_STOP_DEAL_REPORT_ERROR, e);
        }
    }

    @Override
    public ReportOutput selectById(Long id) {
        ReportResult reportResult = reportDao.selectById(id);
        if (reportResult == null) {
            return null;
        }
        ReportOutput output = new ReportOutput();
        BeanUtils.copyProperties(reportResult, output);
        return output;
    }

    @Override
    public void updateReportOnSceneStartFailed(Long sceneId, Long reportId, String errMsg) {
        reportDao.updateReport(new ReportUpdateParam() {{
            setSceneId(sceneId);
            setId(reportId);
            setStatus(ReportConstants.FINISH_STATUS);
            JSONObject errorMsg = new JSONObject();
            errorMsg.put(ReportConstants.FEATURES_ERROR_MSG, errMsg);
            setFeatures(errorMsg.toJSONString());
        }});
    }

    /**
     * 压测完成/结束 更新报告
     * 更新报表数据
     * V4.2.2以前版本的客户端，由cloud更新报告、场景状态
     * V4.2.2及以后版本的客户端，由web调用cloud，来更新报告、场景状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyReport(TaskResult taskResult) {
        //保存报表状态为生成中
        Long reportId = taskResult.getTaskId();
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null) {
            log.error("not find reportId= {}", reportId);
            return;
        }
        Boolean updateVersion = CloudPluginUtils.checkVersion(reportResult);
        log.info("ReportId={}, customerId={}, CompareResult={}", reportId, reportResult.getCustomerId(), updateVersion);

        String testPlanXpathMD5 = getTestPlanXpathMD5(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMD5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
            : testPlanXpathMD5;
        //汇总所有业务活动数据
        StatReportDTO statReport = statReport(taskResult.getSceneId(), reportId, taskResult.getCustomerId(),
            transaction);
        if (statReport == null) {
            log.warn("没有找到报表数据，报表生成失败。报告ID：{}", reportId);
        }

        //更新报表业务活动 isConclusion 指标是否通过
          boolean isConclusion = updateReportBusinessActivity(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());

        //保存报表结果
        saveReportResult(reportResult, taskResult, statReport, isConclusion);

        //先保存报告内容，再更新报告状态，防止报告内容没有填充，就触发finishReport操作
        if (updateVersion) {
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstants.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstants.RUN_STATUS);
            int row = tReportMapper.updateReportStatus(reportStatus);
            // modify by 李鹏
            // 添加TotalRequest不为null 保证报告是有数据的  20210707
            if (row != 1 && reportResult.getTotalRequest() != null) {
                log.error("异常代码【{}】,异常内容：更新报告到生成中状态异常 --> 报告{}状态非0,状态为:{}",
                    TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId, reportResult.getStatus());
                return;
            }
        }

        if (!updateVersion) {
            log.info("old version finish report ={} updateVersion={} ", reportId, updateVersion);
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstants.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstants.FINISH_STATUS);
            tReportMapper.updateReportStatus(reportStatus);
            //更新场景 压测引擎停止压测---> 待启动  版本不一样，关闭不一样
            sceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId())
                    .checkEnum(
                        SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }

    }

    private Date getFinalDateTime(Long sceneId, Long reportId, Long customerId) {
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " max(create_time) as time");
        influxDbSql.append(" from ");
        influxDbSql.append(InfluxDBUtil.getMeasurement(sceneId, reportId, customerId));
        StatReportDTO statReportDTO = influxWriter.querySingle(influxDbSql.toString(), StatReportDTO.class);
        Date date;
        if (Objects.nonNull(statReportDTO) && StringUtils.isNotBlank(statReportDTO.getTime())) {
            try {
                date = new Date(NumberUtil.parseLong(statReportDTO.getTime()));
            } catch (Exception e) {
                log.error("时间转换异常");
                date = new Date(System.currentTimeMillis());
            }
        } else {
            date = new Date(System.currentTimeMillis());
        }
        return date;
    }

    /**
     * 报表数据统计
     *
     * @param sceneId     场景ID
     * @param reportId    报表ID
     * @param customerId  顾客ID
     * @param transaction 业务活动
     * @return -
     */
    private StatReportDTO statReport(Long sceneId, Long reportId, Long customerId, String transaction) {
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " sum(count) as totalRequest, sum(fail_count) as failRequest, mean(avg_tps) as tps ,sum(sum_rt)/sum"
                + "(count) as  "
                + "avgRt, sum(sa_count) as saCount,  max(avg_tps) as maxTps, min(min_rt) as minRt, max(max_rt) as "
                + "maxRt, count(avg_rt) as recordCount ,max(active_threads) as maxConcurrenceNum,round(mean"
                + "(active_threads)) as avgConcurrenceNum");
        influxDbSql.append(" from ");
        influxDbSql.append(InfluxDBUtil.getMeasurement(sceneId, reportId, customerId));
        influxDbSql.append(" where ");
        influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");

        return influxWriter.querySingle(influxDbSql.toString(), StatReportDTO.class);
    }

    /**
     * 更新报表业务活动并且判断是否满足业务指标
     *
     * @return -
     */
    private boolean updateReportBusinessActivity(Long sceneId, Long reportId, Long customerId) {
        //报表活动
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportId);

        //业务活动是否匹配
        boolean totalPassFlag = true;
        boolean passFlag;
        String tableName = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
        for (ReportBusinessActivityDetail reportBusinessActivityDetail : reportBusinessActivityDetails) {
            if (StringUtils.isBlank(reportBusinessActivityDetail.getBindRef())) {
                continue;
            }
            //统计某个业务活动的数据
            StatReportDTO data = statReport(sceneId, reportId, customerId,
                reportBusinessActivityDetail.getBindRef());
            if (data == null) {
                //如果有一个业务活动没有找到对应的数据，则认为压测不通过
                totalPassFlag = false;
                log.warn("没有找到匹配的压测数据：场景ID[{}],报告ID:[{}],业务活动:[{}]", sceneId, reportId,
                    reportBusinessActivityDetail.getBindRef());
                continue;
            }
            //统计RT分布
            Map<String, String> rtMap = reportEventService.queryAndCalcRtDistribute(tableName,
                reportBusinessActivityDetail.getBindRef());
            //匹配报告业务的活动
            reportBusinessActivityDetail.setAvgConcurrenceNum(data.getAvgConcurrenceNum());
            reportBusinessActivityDetail.setMaxRt(data.getMaxRt());
            reportBusinessActivityDetail.setMaxTps(data.getMaxTps());
            reportBusinessActivityDetail.setMinRt(data.getMinRt());
            reportBusinessActivityDetail.setTps(data.getTps());
            reportBusinessActivityDetail.setRt(data.getAvgRt());
            reportBusinessActivityDetail.setSa(data.getSa());
            reportBusinessActivityDetail.setRequest(data.getTotalRequest());
            reportBusinessActivityDetail.setSuccessRate(data.getSuccessRate());
            if (MapUtils.isNotEmpty(rtMap)) {
                reportBusinessActivityDetail.setRtDistribute(JSON.toJSONString(rtMap));
            }
            passFlag = isPass(reportBusinessActivityDetail);
            reportBusinessActivityDetail.setPassFlag(passFlag ? 1 : 0);
            tReportBusinessActivityDetailMapper.updateByPrimaryKeySelective(reportBusinessActivityDetail);
            if (!passFlag) {
                totalPassFlag = false;
            }
        }
        return totalPassFlag;
    }

    /**
     * 活动是否满足预设指标
     * 1.目标成功率 < 实际成功率
     * 2.目标SA > 实际SA
     * 3.目标RT > 实际RT
     * 4.目标TPS < 实际TPS
     *
     * @return -
     */
    private boolean isPass(ReportBusinessActivityDetail detail) {
        if (detail.getTargetSuccessRate().compareTo(detail.getSuccessRate()) > 0) {
            return false;
        } else if (detail.getTargetSa().compareTo(detail.getSa()) > 0) {
            return false;
        } else if (detail.getTargetRt().compareTo(detail.getRt()) < 0) {
            return false;
        } else {
            return detail.getTargetTps().compareTo(detail.getTps()) <= 0;
        }
    }

    private void getRedisInfo(ReportResult reportResult, TaskResult taskResult) {
        // 压力节点 启动情况
        String podName = ScheduleConstants.getPressureNodeName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());
        String podTotalName = ScheduleConstants.getPressureNodeTotalKey(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());
        String podTotal = redisClientUtils.getString(podTotalName);
        podTotal = null == podTotal ? "" : podTotal;
        if (!podTotal.equals(redisClientUtils.getObject(podName))) {
            // 两者不同
            getReportFeatures(reportResult, ReportConstants.PRESSURE_MSG,
                StrUtil.format("pod计划启动{}个，实际启动{}个", podTotal, redisClientUtils.getObject(podName)));
        }
        // 压测引擎
        String engineName = ScheduleConstants.getEngineName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());
        if (redisClientUtils.getObject(engineName) == null || !podTotal.equals(
            redisClientUtils.getString(engineName))) {
            // 两者不同
            getReportFeatures(reportResult, ReportConstants.PRESSURE_MSG,
                StrUtil.format("压测引擎计划运行{}个，实际运行{}个", podTotal, redisClientUtils.getObject(engineName)));
        }

        //        // startTime endTime 补充
        //        long startTime = System.currentTimeMillis();
        //        if (redisClientUtils.hasKey(engineName + ScheduleConstants.FIRST_SIGN)) {
        //            startTime = Long.parseLong(redisClientUtils.getString(engineName + ScheduleConstants.FIRST_SIGN));
        //            reportResult.setStartTime(new Date(startTime));
        //        }
        //
        //        //Long.valueOf(redisClientUtils.getString(engineName + ScheduleConstants.FIRST_SIGN));
        //        long endTime = System.currentTimeMillis();
        //        if (redisClientUtils.hasKey(engineName + ScheduleConstants.LAST_SIGN)) {
        //            endTime = Long.parseLong(redisClientUtils.getString(engineName + ScheduleConstants.LAST_SIGN));
        //        }
        //        // metric 数据是从事件中获取
        //        reportResult.setEndTime(new Date(endTime));
        // 删除缓存
        redisClientUtils.del(podName, podTotalName, ScheduleConstants.TEMP_FAIL_SIGN + engineName,
            engineName + ScheduleConstants.FIRST_SIGN, engineName + ScheduleConstants.LAST_SIGN, engineName);
        //CollectorService.events.remove(engineName);
    }

    /**
     * 保存报表结果
     */
    public void saveReportResult(ReportResult reportResult, TaskResult taskResult, StatReportDTO statReport,
        boolean isConclusion) {
        //SLA规则优先

        if (isSla(reportResult)) {
            reportResult.setConclusion(ReportConstants.FAIL);
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, "触发SLA终止规则");
        } else if (!isConclusion) {
            reportResult.setConclusion(ReportConstants.FAIL);
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, "业务活动指标不达标");
        } else {
            reportResult.setConclusion(ReportConstants.PASS);
        }

        // 这里 要判断下 压力节点 情况，并记录下来 压力节点 最后一位就是 压力节点 数量 开始时间 结束时间更新
        getRedisInfo(reportResult, taskResult);

        //链路通知存在一定耗时，如果大于预设值，则置为预设值
        //SceneManageWrapperOutput sceneManage = sceneManageService.getSceneManage(reportResult.getSceneId(),
        //    new SceneManageQueryOpitons());
        //Long totalTestTime = sceneManage.getTotalTestTime();

        String engineName = ScheduleConstants.getEngineName(reportResult.getSceneId(), reportResult.getId(),
            reportResult.getCustomerId());
        Long eTime = (Long)redisClientUtils.getObject(engineName + ScheduleConstants.LAST_SIGN);
        Date curDate;
        if (eTime != null) {
            curDate = new Date(eTime);
        } else {
            curDate = new Date();
        }
        long testRunTime = DateUtil.between(reportResult.getStartTime(), curDate, DateUnit.SECOND);

        if (null != statReport) {
            //保存报表数据
            reportResult.setTotalRequest(statReport.getTotalRequest());
            // 保留
            reportResult.setAvgRt(statReport.getAvgRt());
            reportResult.setAvgTps(statReport.getTps());
            reportResult.setSuccessRate(statReport.getSuccessRate());
            reportResult.setSa(statReport.getSa());
            reportResult.setId(reportResult.getId());
            reportResult.setAvgConcurrent(statReport.getAvgConcurrenceNum());
            reportResult.setConcurrent(statReport.getMaxConcurrenceNum());
        }
        reportResult.setGmtUpdate(new Date());

        //流量结算
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (null != assetExtApi) {
            AssetInvoiceExt<RealAssectBillExt> invoice = new AssetInvoiceExt<>();
            invoice.setSceneId(reportResult.getSceneId());
            invoice.setTaskId(reportResult.getId());
            invoice.setCustomerId(reportResult.getCustomerId());
            invoice.setResourceId(reportResult.getId());
            invoice.setResourceType(AssetTypeEnum.PRESS_REPORT.getCode());
            invoice.setResourceName(AssetTypeEnum.PRESS_REPORT.getName());
            invoice.setOperateId(reportResult.getOperateId());
            invoice.setOperateName(reportResult.getOperateName());

            RealAssectBillExt bill = new RealAssectBillExt();
            bill.setTime(testRunTime);

            BigDecimal avgThreadNum;
            //如果有平均rt和平均tps，则:threadNum=tps*rt/1000,否则取报告中的平均线程数
            if (null != reportResult.getAvgTps() && null != reportResult.getAvgRt()) {
                avgThreadNum = reportResult.getAvgTps().multiply(reportResult.getAvgRt())
                    .divide(new BigDecimal(1000), 10, RoundingMode.HALF_UP);
            } else {
                avgThreadNum = reportResult.getAvgConcurrent();
            }
            bill.setAvgThreadNum(avgThreadNum);
            invoice.setData(bill);
            Response<BigDecimal> paymentRes = assetExtApi.payment(invoice);
            if (null != paymentRes && paymentRes.isSuccess()) {
                reportResult.setAmount(paymentRes.getData());
            }
        }
        if (Objects.isNull(reportResult.getStartTime())) {
            reportResult.setStartTime(reportResult.getGmtCreate());
        }
        if (Objects.isNull(reportResult.getEndTime())) {
            Date finalDateTime = getFinalDateTime(taskResult.getSceneId(), reportResult.getId(),
                taskResult.getCustomerId());
            if (Objects.isNull(finalDateTime) || finalDateTime.getTime()< reportResult.getStartTime().getTime()) {
                finalDateTime = new Date();
            }
            reportResult.setEndTime(finalDateTime);
        }
        // 更新
        ReportUpdateParam param = new ReportUpdateParam();
        BeanUtils.copyProperties(reportResult, param);
        reportDao.updateReport(param);
    }

    @Override
    public void addWarn(WarnCreateInput input) {
        WarnDetail warnDetail = new WarnDetail();
        BeanUtils.copyProperties(input, warnDetail);
        warnDetail.setWarnTime(DateUtil.parseDateTime(input.getWarnTime()));
        warnDetail.setCreateTime(new Date());
        tWarnDetailMapper.insertSelective(warnDetail);
    }

    private boolean isSla(ReportResult reportResult) {
        if (StringUtils.isBlank(reportResult.getFeatures())) {
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(reportResult.getFeatures());
        // sla熔断数据
        return jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)
            && StringUtils.isNotEmpty(jsonObject.getString(ReportConstants.SLA_ERROR_MSG));
    }

    private Map<String, Object> fillReportMap(ReportBusinessActivityDetail detail) {
        if (Objects.nonNull(detail)) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("avgRt", new DataBean(detail.getRt(), detail.getTargetRt()));
            resultMap.put("sa", new DataBean(detail.getSa(), detail.getTargetSa()));
            resultMap.put("tps", new DataBean(detail.getTps(), detail.getTargetTps()));
            resultMap.put("maxRt", detail.getMaxRt());
            resultMap.put("minRt", detail.getMinRt());
            resultMap.put("maxTps", detail.getMaxTps());
            resultMap.put("activityId", detail.getBusinessActivityId());
            //采样器和设置过目标的控制器视作业务活动，才会对是否达标进行判断，否则按照达标判断
            if (detail.getBusinessActivityId() > -1) {
                resultMap.put("passFlag", Optional.ofNullable(detail.getPassFlag()).orElse(0));
            } else {
                resultMap.put("passFlag", 1);
            }
            resultMap.put("totalRequest", detail.getRequest());
            resultMap.put("successRate", new DataBean(detail.getSuccessRate(), detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", detail.getAvgConcurrenceNum());
            resultMap.put("distribute", getDistributes(detail.getRtDistribute()));
            if (detail.getBusinessActivityId() > -1 && StringUtils.isNotBlank(detail.getApplicationIds())) {
                resultMap.put("applicationIds", detail.getApplicationIds());
            }
            return resultMap;
        }
        return null;

    }

    private List<DistributeBean> getDistributes(String distributes) {
        List<DistributeBean> result;
        if (StringUtils.isNoneBlank(distributes)) {
            Map<String, String> distributeMap = JsonHelper.string2Obj(distributes,
                new TypeReference<Map<String, String>>() {});
            List<DistributeBean> distributeBeans = Lists.newArrayList();
            distributeMap.forEach((key, value) -> {
                DistributeBean distribute = new DistributeBean();
                distribute.setLable(key);
                distribute.setValue(COMPARE + value);
                distributeBeans.add(distribute);
            });
            distributeBeans.sort(((o1, o2) -> -o1.getLable().compareTo(o2.getLable())));
            result = distributeBeans;
        } else {
            result = Lists.newArrayList();
        }
        return result;
    }

    private Map<String, Object> fillTempMap(StatReportDTO statReport, ReportBusinessActivityDetail detail) {
        if (Objects.isNull(detail)) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();
        if (Objects.nonNull(statReport)) {
            resultMap.put("avgRt", new DataBean(statReport.getAvgRt(), detail.getTargetRt()));
            resultMap.put("sa", new DataBean(statReport.getSa(), detail.getTargetSa()));
            resultMap.put("tps", new DataBean(statReport.getTps(), detail.getTargetTps()));
            resultMap.put("successRate", new DataBean(statReport.getSuccessRate(), detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", statReport.getAvgConcurrenceNum().toString());
            resultMap.put("totalRequest", statReport.getTotalRequest());
        } else {
            resultMap.put("avgRt", new DataBean("0", detail.getTargetRt()));
            resultMap.put("sa", new DataBean("0", detail.getTargetSa()));
            resultMap.put("tps", new DataBean("0", detail.getTargetTps()));
            resultMap.put("successRate", new DataBean("0", detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", "0");
            resultMap.put("totalRequest", 0L);
        }
        return resultMap;
    }

    @Override
    public List<ScriptNodeTreeResp> getNodeTree(ScriptNodeTreeQueryReq req) {
        ReportResult reportResult;
        if (Objects.nonNull(req.getReportId())) {
            reportResult = reportDao.selectById(req.getReportId());
        } else {
            reportResult = reportDao.getReportBySceneId(req.getSceneId());
        }

        if (Objects.isNull(reportResult)) {
            if (Objects.isNull(req.getReportId())) {
                log.warn("查询报告节点树--未查询到正在运行的报告，场景ID:{}", req.getSceneId());
            }
            return null;
        }
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportResult.getId());
        if (CollectionUtils.isEmpty(reportBusinessActivityDetails)) {
            return null;
        }
        if (StringUtils.isNotBlank(reportResult.getScriptNodeTree())) {
            //需要把activityId塞到节点树里
            DocumentContext context = JsonPath.parse(reportResult.getScriptNodeTree());
            reportBusinessActivityDetails.stream()
                .filter(Objects::nonNull)
                .forEach(detail -> {
                    Map<String, Object> tmpMap = new HashMap<>();
                    tmpMap.put("businessActivityId", detail.getBusinessActivityId());
                    Map<String, Map<String, Object>> resultMap = new HashMap<>();
                    resultMap.put(detail.getBindRef(), tmpMap);
                    JsonPathUtil.putNodesToJson(context, resultMap);
                });
            return JSONArray.parseArray(context.jsonString(), ScriptNodeTreeResp.class);
        } else {
            return reportBusinessActivityDetails.stream()
                .filter(Objects::nonNull)
                .map(detail -> new ScriptNodeTreeResp() {{
                    setName(detail.getBindRef());
                    setBusinessActivityId(detail.getBusinessActivityId());
                    setTestName(detail.getBusinessActivityName());
                    //兼容老版本，将后续趋势查询条件设置为bindRef
                    setXpathMd5(detail.getBindRef());
                }}).collect(Collectors.toList());
        }
    }
}
