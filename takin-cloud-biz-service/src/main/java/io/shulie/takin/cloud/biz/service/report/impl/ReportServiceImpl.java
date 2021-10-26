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
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.impl.TimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DateField;
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
import io.shulie.takin.cloud.common.constants.ReportConstans;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.common.bean.scenemanage.DataBean;
import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import com.pamirs.takin.entity.domain.dto.report.StatReportDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTrendDTO;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import org.springframework.transaction.annotation.Transactional;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.dao.scene.manage.TWarnDetailMapper;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import com.pamirs.takin.entity.dao.scene.manage.TSceneManageMapper;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.service.scene.ReportEventService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskEventServie;
import io.shulie.takin.cloud.common.bean.scenemanage.StopReasonBean;
import io.shulie.takin.cloud.common.bean.scenemanage.DistributeBean;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import com.pamirs.takin.entity.domain.dto.report.StatInspectReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
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
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;

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
    TSceneManageMapper tSceneManageMapper;
    @Resource
    SceneTaskEventServie sceneTaskEventServie;
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
        CloudPluginUtils.fillReportData(report, detail);

        //警告列表
        List<WarnBean> warnList = listWarn(reportId);
        detail.setTaskStatus(report.getStatus());
        if (CollectionUtils.isNotEmpty(warnList)) {
            detail.setWarn(warnList);
            detail.setTotalWarn(warnList.stream().mapToLong(WarnBean::getTotal).sum());
        }
        if (StringUtils.isNotEmpty(report.getFeatures())) {
            detail.setConclusionRemark(
                JSON.parseObject(report.getFeatures()).getString(ReportConstans.FEATURES_ERROR_MSG));
        }
        detail.setTestTotalTime(TestTimeUtil.format(report.getStartTime(), report.getEndTime()));
        detail.setBusinessActivity(getBusinessActivitySummaryList(reportId));
        //任务没有完成，提示用户正在生成中
        if (report.getStatus() != ReportConstans.FINISH_STATUS) {
            detail.setTaskStatus(ReportConstans.RUN_STATUS);
        }
        // sla转换对象
        if (StringUtils.isNotBlank(report.getFeatures())) {
            JSONObject jsonObject = JSON.parseObject(report.getFeatures());
            if (jsonObject.containsKey(ReportConstans.SLA_ERROR_MSG)) {
                detail.setSlaMsg(JsonHelper.json2Bean(jsonObject.getString(ReportConstans.SLA_ERROR_MSG), SlaBean.class));
            }
        }
        return detail;
    }

    @Override
    public ReportTrendDTO queryReportTrend(ReportTrendQueryParam reportTrendQuery) {
        return queryReportTrend(reportTrendQuery, false);
    }

    @Override
    public ReportDetailOutput tempReportDetail(Long sceneId) {
        long start = System.currentTimeMillis();
        ReportDetailOutput reportDetail = new ReportDetailOutput();

        ReportResult reportResult = reportDao.getTempReportBySceneId(sceneId);
        if (reportResult == null) {
            reportDetail.setTaskStatus(ReportConstans.FINISH_STATUS);
            return reportDetail;
        }
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        SceneManageWrapperOutput wrapper = sceneManageService.getSceneManage(sceneId, options);
        reportDetail = ReportConverter.INSTANCE.ofReportDetail(reportResult);
        reportDetail.setUserId(wrapper.getUserId());

        // 补充停止原因
        reportDetail.setStopReasons(getStopReasonBean(sceneId, reportResult.getId()));

        // 查询sla熔断数据
        ReportDetailOutput detailOutput = this.getReportByReportId(reportResult.getId());
        reportDetail.setSlaMsg(detailOutput.getSlaMsg());

        StatReportDTO statReport = statTempReport(sceneId, reportResult.getId(), reportResult.getCustomerId(),
            ReportConstans.ALL_BUSINESS_ACTIVITY);
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
        reportDetail.setConcurrent(wrapper.getConcurrenceNum());
        reportDetail.setTotalWarn(tWarnDetailMapper.countReportTotalWarn(reportResult.getId()));
        reportDetail.setTaskStatus(reportResult.getStatus());
        reportDetail.setTestTime(getTaskTime(reportResult.getStartTime(), new Date(), wrapper.getTotalTestTime()));
        reportDetail.setTestTotalTime(
            String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

        // 补充操作人
        CloudPluginUtils.fillReportData(reportResult, reportDetail);

        List<SceneBusinessActivityRefOutput> refList = wrapper.getBusinessActivityConfig();
        List<BusinessActivitySummaryBean> list = Lists.newArrayList();
        refList.forEach(businessActivityRef -> {
            StatReportDTO data = statTempReport(sceneId, reportResult.getId(), reportResult.getCustomerId(), businessActivityRef.getBindRef());
            BusinessActivitySummaryBean businessActivity = new BusinessActivitySummaryBean();
            businessActivity.setBusinessActivityId(businessActivityRef.getBusinessActivityId());
            businessActivity.setBusinessActivityName(businessActivityRef.getBusinessActivityName());
            if (data != null) {
                businessActivity.setAvgRT(new DataBean(data.getAvgRt(), businessActivityRef.getTargetRT()));
                businessActivity.setSa(new DataBean(data.getSa(), businessActivityRef.getTargetSA()));
                businessActivity.setTps(new DataBean(data.getTps(), businessActivityRef.getTargetTPS()));
                businessActivity.setSucessRate(new DataBean(data.getSuccessRate(), businessActivityRef.getTargetSuccessRate()));
                businessActivity.setAvgConcurrenceNum(data.getAvgConcurrenceNum());
                businessActivity.setTotalRequest(data.getTotalRequest());
            } else {
                businessActivity.setBusinessActivityName(businessActivityRef.getBusinessActivityName());
                businessActivity.setAvgRT(new DataBean("0", businessActivityRef.getTargetRT()));
                businessActivity.setSa(new DataBean("0", businessActivityRef.getTargetSA()));
                businessActivity.setTps(new DataBean("0", businessActivityRef.getTargetTPS()));
                businessActivity.setAvgConcurrenceNum(new BigDecimal(0));
                businessActivity.setSucessRate(new DataBean("0", businessActivityRef.getTargetSuccessRate()));
                businessActivity.setTotalRequest(0L);
            }
            list.add(businessActivity);
        });
        reportDetail.setBusinessActivity(list);

        //检查任务是否超时
        boolean taskIsTimeOut = checkSceneTaskIsTimeOut(reportResult, wrapper);
        if (wrapper.getStatus().intValue() == SceneManageStatusEnum.PTING.getValue().intValue() && taskIsTimeOut) {
            log.info("报表[{}]超时，通知调度马上停止压测", reportResult.getId());
            //报告正在生成中
            reportDetail.setTaskStatus(ReportConstans.RUN_STATUS);
            //重置时间
            reportDetail.setTestTime(
                String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

            //主动通知暂停事件，注意有可能会被多次触发
            sceneTaskEventServie.callStopEvent(reportResult);
        }
        log.info("实时监测metric数据：tempReportDetail-运行时间：{}", System.currentTimeMillis() - start);
        return reportDetail;
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
        String pressureNodeKey = String.format(SceneTaskRedisConstants.PRESSURE_NODE_ERROR_KEY + "%s_%s", sceneId, reportId);
        Object pressureNodeStartError = redisClientUtils.hmget(pressureNodeKey, SceneTaskRedisConstants.PRESSURE_NODE_START_ERROR);
        if (Objects.nonNull(pressureNodeStartError)) {
            // 组装压力节点异常显示数据
            StopReasonBean stopReasonBean = new StopReasonBean();
            stopReasonBean.setType(SceneStopReasonEnum.PRESSURE_NODE.getType());
            stopReasonBean.setDescription(SceneStopReasonEnum.toDesc(pressureNodeStartError.toString()));
            stopReasons.add(stopReasonBean);
            //  持久化
            ReportResult reportResult = reportDao.selectById(reportId);
            getReportFeatures(reportResult, ReportConstans.PRESSURE_MSG, pressureNodeStartError.toString());
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
            getReportFeatures(reportResult, ReportConstans.PRESSURE_MSG, errorObj.toString());
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
    public ReportTrendDTO queryTempReportTrend(ReportTrendQueryParam reportTrendQuery) {
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
    public List<BusinessActivitySummaryBean> getBusinessActivitySummaryList(Long reportId) {
        List<BusinessActivitySummaryBean> list = Lists.newArrayList();
        //查询业务活动的概况
        List<ReportBusinessActivityDetail> reportBusinessActivityDetailList = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportId);
        if (CollectionUtils.isEmpty(reportBusinessActivityDetailList)) {
            return new ArrayList<>(0);
        }
        reportBusinessActivityDetailList.forEach(reportBusinessActivityDetail -> {
            BusinessActivitySummaryBean businessActivity = new BusinessActivitySummaryBean();
            businessActivity.setBusinessActivityId(reportBusinessActivityDetail.getBusinessActivityId());
            businessActivity.setAvgConcurrenceNum(reportBusinessActivityDetail.getAvgConcurrenceNum());
            businessActivity.setBusinessActivityName(reportBusinessActivityDetail.getBusinessActivityName());
            businessActivity.setApplicationIds(reportBusinessActivityDetail.getApplicationIds());
            businessActivity.setBindRef(reportBusinessActivityDetail.getBindRef());
            businessActivity.setTotalRequest(reportBusinessActivityDetail.getRequest());
            businessActivity.setAvgRT(new DataBean(reportBusinessActivityDetail.getRt(),
                reportBusinessActivityDetail.getTargetRt()));
            businessActivity.setSa(new DataBean(reportBusinessActivityDetail.getSa(),
                reportBusinessActivityDetail.getTargetSa()));
            businessActivity.setTps(new DataBean(reportBusinessActivityDetail.getTps(),
                reportBusinessActivityDetail.getTargetTps()));
            businessActivity.setSucessRate(new DataBean(reportBusinessActivityDetail.getSuccessRate(),
                reportBusinessActivityDetail.getTargetSuccessRate()));
            businessActivity.setMaxRt(reportBusinessActivityDetail.getMaxRt());
            businessActivity.setMaxTps(reportBusinessActivityDetail.getMaxTps());
            businessActivity.setMinRt(reportBusinessActivityDetail.getMinRt());
            businessActivity.setPassFlag(Optional.ofNullable(reportBusinessActivityDetail.getPassFlag()).orElse(0));
            if (StringUtils.isNoneBlank(reportBusinessActivityDetail.getRtDistribute())) {
                Map<String, String> distributeMap = JsonHelper.string2Obj(
                    reportBusinessActivityDetail.getRtDistribute(), new TypeReference<Map<String, String>>() {
                    });
                List<DistributeBean> distributes = Lists.newArrayList();
                distributeMap.forEach((key, value) -> {
                    DistributeBean distribute = new DistributeBean();
                    distribute.setLable(key);
                    distribute.setValue(COMPARE + value);
                    distributes.add(distribute);
                });
                distributes.sort(((o1, o2) -> -o1.getLable().compareTo(o2.getLable())));
                businessActivity.setDistribute(distributes);
            } else {
                businessActivity.setDistribute(Lists.newArrayList());
            }
            list.add(businessActivity);
        });
        return list;
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
    public Boolean lockReport(Long reportId) {
        log.info("web -> cloud lock reportId【{}】,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        if (checkReportError(reportResult)) {
            return false;
        }
        if (ReportConstans.LOCK_STATUS == reportResult.getLock()) {
            log.error("异常代码【{}】,异常内容：锁定报告异常 --> 报告{}状态锁定状态，不能再次锁定",
                TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        reportDao.updateReportLock(reportId, ReportConstans.LOCK_STATUS);
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
        if (ReportConstans.LOCK_STATUS != reportResult.getLock()) {
            log.error("异常代码【{}】,异常内容：解锁报告异常 --> 报告{}非锁定状态，不能解锁",
                TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        // 解锁
        reportDao.updateReportLock(reportId, ReportConstans.RUN_STATUS);
        log.info("报告{}解锁成功", reportId);
        return true;
    }

    @Override
    public Boolean finishReport(Long reportId) {
        log.info("web -> cloud finish reportId【{}】,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        if (checkReportError(reportResult)) {
            return false;
        }
        reportDao.finishReport(reportId);
        log.info("报告{} finish done", reportId);

        UpdateStatusBean reportStatus = new UpdateStatusBean();
        reportStatus.setResultId(reportId);
        //完成报告之后锁定报告
        reportStatus.setPreStatus(ReportConstans.RUN_STATUS);
        reportStatus.setAfterStatus(ReportConstans.LOCK_STATUS);
        tReportMapper.updateReportLock(reportStatus);

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
                UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId()).checkEnum(
                    SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }

        return true;
    }

    @Override
    public void forceFinishReport(Long reportId) {
        // 更新场景
        ReportResult reportResult = reportDao.selectById(reportId);
        // 完成报告
        if (reportResult.getStatus() != ReportConstans.FINISH_STATUS) {
            log.info("{}报告触发强制停止", reportId);
            reportDao.finishReport(reportId);
        }

        sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId())
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
     * 巡检报告取值
     */
    private StatInspectReportDTO statInspectReport(Long sceneId, Long reportId, Long customerId, String transaction, String startTime, String endTime) {
        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " sum(count) as totalRequest,mean(avg_tps) as avgTps , sum(sum_rt)/sum(count) as avgRt , mean(success_rate) as avgSuccessRate");
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
    private ReportTrendDTO queryReportTrend(ReportTrendQueryParam reportTrendQuery, boolean isTempReport) {
        long start = System.currentTimeMillis();
        ReportTrendDTO reportTrend = new ReportTrendDTO();
        ReportResult reportResult;
        if (isTempReport) {
            reportResult = reportDao.getTempReportBySceneId(reportTrendQuery.getSceneId());
        } else {
            reportResult = reportDao.selectById(reportTrendQuery.getReportId());
        }
        if (reportResult == null) {
            return new ReportTrendDTO();
        }

        String transaction = ReportConstans.ALL_BUSINESS_ACTIVITY;
        if (reportTrendQuery.getBusinessActivityId() != null && reportTrendQuery.getBusinessActivityId() > 0) {
            List<ReportBusinessActivityDetail> details = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportResult.getId());
            if (CollectionUtils.isEmpty(details)) {
                transaction = null;
            } else {
                transaction = details.stream().filter(
                    data -> reportTrendQuery.getBusinessActivityId().equals(data.getBusinessActivityId())).map(
                    ReportBusinessActivityDetail::getBindRef).findFirst().orElse(null);
            }
        }

        StringBuilder influxDbSql = new StringBuilder();
        influxDbSql.append("select");
        influxDbSql.append(
            " sum(count) as totalRequest, sum(fail_count) as failRequest, mean(avg_tps) as tps , sum(sum_rt)/sum(count) as "
                + "avgRt, sum(sa_count) as saCount, count(avg_rt) as recordCount ,mean(active_threads) as avgConcurrenceNum ");
        influxDbSql.append(" from ");
        influxDbSql.append(InfluxDBUtil.getMeasurement(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId()));
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
            log.info("report = {}，runTime = {} , totalTestTime= {},Timeout check", reportResult.getId(), runTime, totalTestTime);
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
            if (errKey.equals(ReportConstans.SLA_ERROR_MSG) && map.containsKey(ReportConstans.SLA_ERROR_MSG)) {
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
            getReportFeatures(reportResult, ReportConstans.FEATURES_ERROR_MSG, input.getErrorMessage());
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
        getReportFeatures(reportResult, ReportConstans.SLA_ERROR_MSG, JsonHelper.bean2Json(input.getSlaBean()));
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
            setStatus(ReportConstans.FINISH_STATUS);
            JSONObject errorMsg = new JSONObject();
            errorMsg.put(ReportConstans.FEATURES_ERROR_MSG, errMsg);
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
        if (updateVersion) {
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstans.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstans.RUN_STATUS);
            int row = tReportMapper.updateReportStatus(reportStatus);
            // modify by 李鹏
            // 添加TotalRequest不为null 保证报告是有数据的  20210707
            if (row != 1 && reportResult.getTotalRequest() != null) {
                log.error("异常代码【{}】,异常内容：更新报告到生成中状态异常 --> 报告{}状态非0,状态为:{}",
                    TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId, reportResult.getStatus());
                return;
            }
            reportResult.setStatus(ReportConstans.RUN_STATUS);
        }

        //汇总所有业务活动数据
        StatReportDTO statReport = statReport(taskResult.getSceneId(), reportId, taskResult.getCustomerId(),
            ReportConstans.ALL_BUSINESS_ACTIVITY);
        if (statReport == null) {
            log.warn("没有找到报表数据，报表生成失败。报告ID：{}", reportId);
            statReport = new StatReportDTO();
        }

        //更新报表业务活动 isConclusion 指标是否通过
        boolean isConclusion = updateReportBusinessActivity(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());

        //保存报表结果
        saveReportResult(reportResult, taskResult, statReport, isConclusion);

        if (!updateVersion) {
            log.info("old version finish report ={} updateVersion={} ", reportId, updateVersion);
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstans.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstans.FINISH_STATUS);
            tReportMapper.updateReportStatus(reportStatus);
            //更新场景 压测引擎停止压测---> 待启动  版本不一样，关闭不一样
            sceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getCustomerId()).checkEnum(
                    SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }

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
            " sum(count) as totalRequest, sum(fail_count) as failRequest, mean(avg_tps) as tps ,sum(sum_rt)/sum(count) as  "
                + "avgRt, sum(sa_count) as saCount,  max(avg_tps) as maxTps, min(min_rt) as minRt, max(max_rt) as "
                // add by 李鹏
                // 20210621 active_threads有可能出现0的情况，所以这里取平均后可能不为整数，加round取整
                + "maxRt, count(avg_rt) as recordCount ,round(mean(active_threads)) as avgConcurrenceNum");
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
        if (!podTotal.equals(redisClientUtils.getObject(podName))) {
            // 两者不同
            getReportFeatures(reportResult, ReportConstans.PRESSURE_MSG,
                StrUtil.format("pod计划启动{}个，实际启动{}个", podTotal, redisClientUtils.getObject(podName)));
        }
        // 压测引擎
        String engineName = ScheduleConstants.getEngineName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getCustomerId());
        if (redisClientUtils.getObject(engineName) == null || !podTotal.equals(redisClientUtils.getString(engineName))) {
            // 两者不同
            getReportFeatures(reportResult, ReportConstans.PRESSURE_MSG,
                StrUtil.format("压测引擎计划运行{}个，实际运行{}个", podTotal, redisClientUtils.getObject(engineName)));
        }

        // startTime endTime 补充
        long startTime = System.currentTimeMillis();
        if (redisClientUtils.hasKey(engineName + ScheduleConstants.FIRST_SIGN)) {
            startTime = Long.parseLong(redisClientUtils.getString(engineName + ScheduleConstants.FIRST_SIGN));
            reportResult.setStartTime(new Date(startTime));
        }

        //Long.valueOf(redisClientUtils.getString(engineName + ScheduleConstants.FIRST_SIGN));
        long endTime = System.currentTimeMillis();
        if (redisClientUtils.hasKey(engineName + ScheduleConstants.LAST_SIGN)) {
            endTime = Long.parseLong(redisClientUtils.getString(engineName + ScheduleConstants.LAST_SIGN));
        }
        // metric 数据是从事件中获取
        reportResult.setEndTime(new Date(endTime));
        // 删除缓存
        redisClientUtils.del(podName, podTotalName, ScheduleConstants.TEMP_FAIL_SIGN + engineName,
            engineName + ScheduleConstants.FIRST_SIGN, engineName + ScheduleConstants.LAST_SIGN, engineName);
        //CollectorService.events.remove(engineName);
    }

    /**
     * 保存报表结果
     */
    public void saveReportResult(ReportResult reportResult, TaskResult taskResult, StatReportDTO statReport, boolean isConclusion) {
        //SLA规则优先

        if (isSla(reportResult)) {
            reportResult.setConclusion(ReportConstans.FAIL);
            getReportFeatures(reportResult, ReportConstans.FEATURES_ERROR_MSG, "触发SLA终止规则");
        } else if (!isConclusion) {
            reportResult.setConclusion(ReportConstans.FAIL);
            getReportFeatures(reportResult, ReportConstans.FEATURES_ERROR_MSG, "业务活动指标不达标");
        } else {
            reportResult.setConclusion(ReportConstans.PASS);
        }

        // 这里 要判断下 压力节点 情况，并记录下来 压力节点 最后一位就是 压力节点 数量 开始时间 结束时间更新
        getRedisInfo(reportResult, taskResult);

        //链路通知存在一定耗时，如果大于预设值，则置为预设值
        SceneManageWrapperOutput sceneManage = sceneManageService.getSceneManage(reportResult.getSceneId(),
            new SceneManageQueryOpitons());
        Long totalTestTime = sceneManage.getTotalTestTime();
        Date curDate = new Date();
        long testRunTime = DateUtil.between(reportResult.getStartTime(), curDate, DateUnit.SECOND);
        if (testRunTime > totalTestTime) {
            //强制修改结束时间
            curDate = DateUtil.offset(reportResult.getStartTime(), DateField.SECOND, totalTestTime.intValue());
        }

        //保存报表数据
        reportResult.setTotalRequest(statReport.getTotalRequest());
        // 保留
        reportResult.setAvgRt(statReport.getAvgRt());
        reportResult.setAvgTps(statReport.getTps());
        reportResult.setSuccessRate(statReport.getSuccessRate());
        reportResult.setSa(statReport.getSa());
        reportResult.setId(reportResult.getId());
        reportResult.setEndTime(curDate);
        reportResult.setGmtUpdate(new Date());
        reportResult.setAvgConcurrent(statReport.getAvgConcurrenceNum());

        //流量结算
        AssetInvoiceExt accountTradeRequest = new AssetInvoiceExt() {{
            setPressureTotalTime(testRunTime > totalTestTime ? totalTestTime : testRunTime);
            setPressureMode(sceneManage.getPressureMode());
            setIncreasingTime(sceneManage.getIncreasingSecond());
            setPressureType(sceneManage.getPressureType());
            setTaskId(reportResult.getId());
            setSceneId(reportResult.getSceneId());
            setCustomerId(reportResult.getCustomerId());
            setStep(sceneManage.getStep());
            setAvgConcurrent(statReport.getAvgConcurrenceNum());
        }};
        if (statReport.getTps() == null) {
            accountTradeRequest.setExpectThroughput(1);
        } else {
            accountTradeRequest.setExpectThroughput((
                statReport.getTps()
                    .divide(new BigDecimal("1000"), 2, RoundingMode.FLOOR)
                    .multiply(statReport.getAvgRt())).intValue() + 1);
        }
        log.info("流量结算：{}", JSON.toJSONString(accountTradeRequest));

        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            BigDecimal amount = assetExtApi.payment(accountTradeRequest);
            reportResult.setAmount(amount);
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
        return jsonObject.containsKey(ReportConstans.SLA_ERROR_MSG)
            && StringUtils.isNotEmpty(jsonObject.getString(ReportConstans.SLA_ERROR_MSG));
    }

}
