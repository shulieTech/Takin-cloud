package io.shulie.takin.cloud.biz.collector;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateUnit;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.annotation.Async;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.biz.utils.Executors;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.eventcenter.entity.TaskConfig;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.utils.CollectorUtil;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.biz.output.statistics.PressureOutput;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam.PressureTypeRelation;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 22:13
 */
@Slf4j
@Component
public class PushWindowDataScheduled extends AbstractIndicators {
    @Resource
    private ReportDao reportDao;
    @Resource
    private InfluxWriter influxWriter;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private EventCenterTemplate eventCenterTemplate;

    @Value("${scheduling.enabled:true}")
    private Boolean schedulingEnabled;

    @Value("${report.metric.isSaveLastPoint:true}")
    private boolean isSaveLastPoint;

    /**
     * ?????????????????? ??????
     */
    private static final Map<String, Long> TIME_WINDOW_MAP = Maps.newConcurrentMap();

    public void sendMetrics(Metrics metrics) {
        Event event = new Event();
        event.setEventName("metricsData");
        event.setExt(metrics);
        eventCenterTemplate.doEvents(event);
    }

    @IntrestFor(event = "started")
    public void doStartScheduleTaskEvent(Event event) {
        log.info("PushWindowDataScheduled??????????????????????????????????????????????????????");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        String taskKey = getTaskKey(taskBean.getSceneId(), taskBean.getTaskId(), taskBean.getTenantId());
        /*
         * ???????????? + ???????????? + ????????? 7???
         */
        long taskTimeout = 7L * 24 * 60 * 60;
        Map<String, Object> extMap = taskBean.getExtendMap();
        List<String> refList = Lists.newArrayList();
        if (MapUtils.isNotEmpty(extMap)) {
            refList.addAll((List)extMap.get("businessActivityBindRef"));
        }
        ArrayList<String> transition = new ArrayList<>(refList);
        transition.add("all");
        String redisKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, taskKey);
        redisTemplate.opsForValue().set(redisKey, transition, taskTimeout, TimeUnit.SECONDS);
        log.info("PushWindowDataScheduled Create Redis Key = {}, expireDuration={}min, refList={} Success....",
            redisKey, taskTimeout, refList);
    }

    /**
     * ????????????
     *
     * @param event-
     */
    @IntrestFor(event = "stop")
    public void doStopTaskEvent(Event event) {
        TaskConfig taskConfig = (TaskConfig)event.getExt();
        delTask(taskConfig.getSceneId(), taskConfig.getTaskId(), taskConfig.getTenantId());
    }

    /**
     * ?????? ????????????
     */
    @IntrestFor(event = "finished")
    public void doDeleteTaskEvent(Event event) {
        try {
            log.info("??????PushWindowDataScheduled??????????????????????????????????????????????????????");
            TaskResult taskResult = (TaskResult)event.getExt();
            delTask(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId());
        } catch (Exception e) {
            log.error("???PushWindowDataScheduled?????????finished????????????={}", e.getMessage(), e);
        }
    }

    private void delTask(Long sceneId, Long reportId, Long tenantId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null || reportResult.getStatus() == 0) {
            log.info("??????????????????key???????????????????????????sceneId:{},reportId:{}", sceneId, reportId);
            return;
        }
        if (null != sceneId && null != reportId) {
            String taskKey = getTaskKey(sceneId, reportId, tenantId);
            redisTemplate.delete(String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, taskKey));
        }
    }

    private Long getMetricsMinTimeWindow(Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMetricsMeasurement(sceneId, reportId, customerId);
            ResponseMetrics metrics = influxWriter.querySingle(
                "select * from " + measurement + " where time>0 order by time asc limit 1", ResponseMetrics.class);
            if (null != metrics) {
                timeWindow = CollectorUtil.getTimeWindowTime(metrics.getTime());
            }
        } catch (Throwable e) {
            log.error("????????????", e);
        }
        return timeWindow;
    }

    private List<ResponseMetrics> queryMetrics(Long sceneId, Long reportId, Long customerId, Long timeWindow) {
        try {
            //???????????????????????????????????????????????????5s????????????(0,5]
            String measurement = InfluxUtil.getMetricsMeasurement(sceneId, reportId, customerId);
            StringBuilder sql = new StringBuilder("select * from");
            sql.append(" ").append(measurement);
            if (null != timeWindow) {
                long time = TimeUnit.NANOSECONDS.convert(timeWindow, TimeUnit.MILLISECONDS);
                sql.append(" ").append("where").append(" ").append("time<=").append(time).append(" and ")
                    .append("time>").append(time - TimeUnit.NANOSECONDS.convert(CollectorConstants.SEND_TIME, TimeUnit.SECONDS));
                List<ResponseMetrics> query = influxWriter.query(sql.toString(), ResponseMetrics.class);
                log.info("?????????????????????sceneId:{},sql:{},??????????????????:{}", sceneId, sql, query == null ? "null" : query.size());
                return query;
            } else {
                timeWindow = getMetricsMinTimeWindow(sceneId, reportId, customerId);
                if (null != timeWindow) {
                    return queryMetrics(sceneId, reportId, customerId, timeWindow);
                }
            }
        } catch (Throwable e) {
            log.error("????????????", e);
        }
        return null;
    }

    /**
     * ????????????????????????????????????????????????
     */
    private Long getWorkingPressureMinTimeWindow(Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMeasurement(sceneId, reportId, customerId);
            PressureOutput pressure = influxWriter.querySingle(
                "select * from " + measurement + " where status=0 order by time asc limit 1", PressureOutput.class);
            if (null != pressure) {
                timeWindow = pressure.getTime();
            }
        } catch (Throwable e) {
            log.error("????????????", e);
        }
        return timeWindow;
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private Long getPressureMaxTimeNextTimeWindow(Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMeasurement(sceneId, reportId, customerId);
            PressureOutput pressure = influxWriter.querySingle(
                "select * from " + measurement + " where status=1 order by time desc limit 1", PressureOutput.class);
            if (null != pressure) {
                timeWindow = CollectorUtil.getNextTimeWindow(pressure.getTime());
            }
        } catch (Throwable e) {
            log.error("????????????", e);
        }
        return timeWindow;
    }

    private Long reduceMetrics(ReportResult report, Integer podNum, long endTime, Long timeWindow, List<ScriptNode> nodes) {
        if (null == report) {
            return null;
        }
        Long sceneId = report.getSceneId();
        Long reportId = report.getId();
        Long customerId = report.getTenantId();
        String logPre = String.format("reduceMetrics %s-%s-%s:%s",
            sceneId, reportId, customerId, showTime(timeWindow));
        log.info(logPre + " start!");
        //????????????????????????
        if (null == timeWindow) {
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????
            timeWindow = getWorkingPressureMinTimeWindow(sceneId, reportId, customerId);
            //??????????????????????????????????????????
            if (null == timeWindow) {
                //????????????????????????????????????????????????????????????
                timeWindow = getPressureMaxTimeNextTimeWindow(sceneId, reportId, customerId);
            }
        }
        //??????????????????????????????????????????????????????????????????????????????
        if (null != timeWindow && timeWindow > endTime) {
            log.info("{} return 1!timeWindow={}, endTime={}",
                logPre, showTime(timeWindow), showTime(endTime));
            return timeWindow;
        }
        //timeWindow??????????????????????????????metrics?????????????????????????????????????????????????????????
        List<ResponseMetrics> metricsList = queryMetrics(sceneId, reportId, customerId, timeWindow);
        if (CollectionUtils.isEmpty(metricsList)) {
            log.info("{}, timeWindow={} ??? metrics ????????????!", logPre, showTime(timeWindow));
            return timeWindow;
        }
        log.info("{} queryMetrics timeWindow={}, endTime={}, metricsList.size={}",
            logPre, showTime(timeWindow), showTime(endTime), metricsList.size());
        if (null == timeWindow) {
            timeWindow = metricsList.stream().filter(Objects::nonNull)
                .map(t -> CollectorUtil.getTimeWindowTime(t.getTime()))
                .filter(l -> l > 0)
                .findFirst()
                .orElse(endTime);
        }
        //???????????????????????????????????????????????????????????????????????????
        if (timeWindow > endTime) {
            log.info("{} return 3!timeWindow={}, endTime={}",
                logPre, showTime(timeWindow), showTime(endTime));
            return timeWindow;
        }

        List<String> transactions = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getTransaction)
            .filter(StringUtils::isNotBlank)
            //??????????????????
            //.filter(t -> !this.isController(t, nodes))
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(transactions)) {
            log.info("{} return 4!transactions is empty!", logPre);
            return timeWindow;
        }

        String measurement = InfluxUtil.getMeasurement(sceneId, reportId, customerId);
        long time = timeWindow;

        List<PressureOutput> results = transactions.stream().filter(StringUtils::isNotBlank)
            .map(s -> this.filterByTransaction(metricsList, s))
            .filter(CollectionUtils::isNotEmpty)
            .map(l -> this.toPressureOutput(l, podNum, time))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(results)) {
            log.info("results is empty!");
            return timeWindow;
        }

        List<PressureOutput> slaList = new ArrayList<>(results);
        //?????????????????????????????????
        if (CollectionUtils.isNotEmpty(nodes)) {
            Map<String, PressureOutput> pressureMap = results.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(PressureOutput::getTransaction, o -> o, (o1, o2) -> o1));
            nodes.stream().filter(Objects::nonNull)
                .forEach(n -> countPressure(n, pressureMap, results));
        }
        //??????????????????????????????ALL???saCount
        else {
            int allSaCount = results.stream().filter(Objects::nonNull)
                .filter(item -> !ReportConstants.ALL_BUSINESS_ACTIVITY.equalsIgnoreCase(item.getTransaction()))
                .map(PressureOutput::getSaCount)
                .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                .sum();
            results.stream().filter(Objects::nonNull)
                .filter(item -> ReportConstants.ALL_BUSINESS_ACTIVITY.equalsIgnoreCase(item.getTransaction()))
                .forEach(item ->{
                    item.setSaCount(allSaCount);
                });
        }
        results.stream().filter(Objects::nonNull)
            .map(p -> InfluxUtil.toPoint(measurement, time, p))
            .forEach(influxWriter::insert);
        //sla??????
        try {
            List<SendMetricsEvent> sendMetricsEventList = getSendMetricsEventList(sceneId, reportId, customerId,
                timeWindow, slaList);
            if (ReportConstants.INIT_STATUS == report.getStatus()) {
                sendMetricsEventList.stream().filter(Objects::nonNull)
                    .forEach(this::sendMetrics);
            }
        } catch (Exception e) {
            log.error(
                "???collector metric??????error-sendMetricsEvents??? write influxDB time : {} sceneId : {}, reportId : "
                    + "{},customerId : {}, error:{}",
                timeWindow, sceneId, reportId, customerId, e.getMessage());
        }
        log.info("{} finished!timeWindow={}, endTime={}",
            logPre, showTime(timeWindow), showTime(endTime));
        return timeWindow;
    }

    /**
     * ???????????????????????????
     *
     * @param node      ??????
     * @param sourceMap ?????????????????????????????????jmeter??????????????????????????????
     * @param results   ??????????????????
     * @return ?????????????????????????????????
     */
    private PressureOutput countPressure(ScriptNode node, Map<String, PressureOutput> sourceMap, List<PressureOutput> results) {
        if (null == node || StringUtils.isBlank(node.getXpathMd5()) || null == sourceMap) {
            return null;
        }
        //sourceMap??????key??????jmeter?????????
        PressureOutput result = sourceMap.get(node.getXpathMd5());
        if (null != result) {
            return result;
        }
        if (CollectionUtils.isEmpty(node.getChildren())) {
            return null;
        }
        List<PressureOutput> childPressures = node.getChildren().stream().filter(Objects::nonNull)
            .map(n -> countPressure(n, sourceMap, results))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        result = countPressure(node, childPressures, sourceMap);
        if (null != result) {
            results.add(result);
        }
        return result;
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param node           ????????????
     * @param childPressures ???????????????????????????(1????????????)
     * @return ??????????????????????????????
     */
    private PressureOutput countPressure(ScriptNode node, List<PressureOutput> childPressures, Map<String, PressureOutput> sourceMap) {
        if (CollectionUtils.isEmpty(childPressures)) {
            return null;
        }
        long time = childPressures.stream().filter(Objects::nonNull)
            .mapToLong(PressureOutput::getTime)
            .findAny()
            .orElse(0L);
        if (0 == time) {
            return null;
        }
        /*================== childPressures??????1???????????? ???????????? =====================================================*/
        int activeThreads;
        //TEST_PLAN???????????????
        if (NodeTypeEnum.TEST_PLAN == node.getType()) {
            activeThreads = NumberUtil.sum(childPressures, PressureOutput::getActiveThreads);
        } else {//?????????????????????????????????????????????????????????
            activeThreads = NumberUtil.maxInt(childPressures, PressureOutput::getActiveThreads);
        }
        long sendBytes = NumberUtil.sumLong(childPressures, PressureOutput::getSentBytes);
        long receiveBytes = NumberUtil.sumLong(childPressures, PressureOutput::getReceivedBytes);
        int dataNum = NumberUtil.minInt(childPressures, PressureOutput::getDataNum, 1);
        double dataRate = NumberUtil.minDouble(childPressures, PressureOutput::getDataRate, 1d);
        int status = NumberUtil.minInt(childPressures, PressureOutput::getStatus, 1);
        /*=================== childPressures??????1???????????? ???????????? =====================================================/

        /*=================== subPressures??????1????????????????????????????????????????????????????????? ????????? ===============================*/
        /*
         * ???????????????,????????????????????????????????????:????????????????????????????????????????????????????????????????????????????????????????????????
         * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         */
        List<ScriptNode> childNodes = JmxUtil.getChildNodesByFilterFunc(node, n -> sourceMap.containsKey(n.getXpathMd5()));
        final List<PressureOutput> subPressures = Lists.newArrayList(childPressures);
        if (CollectionUtils.isNotEmpty(childNodes)) {
            childNodes.stream().filter(Objects::nonNull)
                .map(ScriptNode::getXpathMd5)
                .filter(StringUtils::isNotBlank)
                .map(sourceMap::get)
                .filter(Objects::nonNull)
                .filter(d -> !childPressures.contains(d))
                .forEach(subPressures::add);
        }
        int count = NumberUtil.sum(subPressures, PressureOutput::getCount);
        int failCount = NumberUtil.sum(subPressures, PressureOutput::getFailCount);
        int saCount = NumberUtil.sum(subPressures, PressureOutput::getSaCount);
        long sumRt = NumberUtil.sumLong(subPressures, PressureOutput::getSumRt);
        double maxRt = NumberUtil.maxDouble(subPressures, PressureOutput::getMaxRt);
        double minRt = NumberUtil.minDouble(subPressures, PressureOutput::getMinRt);
        double avgTps = NumberUtil.sumDouble(subPressures, PressureOutput::getAvgTps);
        String percentSa = calculateSaPercent(CommonUtil.getList(subPressures, PressureOutput::getSaPercent));
        /*=================== subPressures??????1????????????????????????????????????????????????????????? ????????? ===============================*/

        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(Math.max(0, count - failCount), count);
        double avgRt = NumberUtil.getRate(sumRt, count);
        PressureOutput output = new PressureOutput();
        output.setTime(time);
        output.setTransaction(node.getXpathMd5());
        output.setCount(count);
        output.setFailCount(failCount);
        output.setSaCount(saCount);
        output.setSa(sa);
        output.setSuccessRate(successRate);
        output.setSentBytes(sendBytes);
        output.setReceivedBytes(receiveBytes);
        output.setSumRt(sumRt);
        output.setAvgRt(avgRt);
        output.setMaxRt(maxRt);
        output.setMinRt(minRt);
        output.setActiveThreads(activeThreads);
        output.setAvgTps(avgTps);
        output.setSaPercent(percentSa);
        output.setDataNum(dataNum);
        output.setDataRate(dataRate);
        output.setStatus(status);
        output.setTestName(node.getTestName());
        return output;
    }

    /**
     * ?????????????????????????????????transaction??????
     */
    private List<ResponseMetrics> filterByTransaction(List<ResponseMetrics> metricsList, String transaction) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return metricsList;
        }
        return metricsList.stream().filter(Objects::nonNull)
            .filter(m -> transaction.equals(m.getTransaction()))
            .collect(Collectors.toList());
    }

    /**
     * ??????????????????
     */
    private PressureOutput toPressureOutput(List<ResponseMetrics> metricsList, Integer podNum, long time) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return null;
        }
        String transaction = metricsList.get(0).getTransaction();
        String testName = metricsList.get(0).getTestName();

        int count = NumberUtil.sum(metricsList, ResponseMetrics::getCount);
        int failCount = NumberUtil.sum(metricsList, ResponseMetrics::getFailCount);
        int saCount = NumberUtil.sum(metricsList, ResponseMetrics::getSaCount);
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = NumberUtil.sumLong(metricsList, ResponseMetrics::getSentBytes);
        long receivedBytes = NumberUtil.sumLong(metricsList, ResponseMetrics::getReceivedBytes);
        long sumRt = NumberUtil.sumLong(metricsList, ResponseMetrics::getSumRt);
        double avgRt = NumberUtil.getRate(sumRt, count);
        double maxRt = NumberUtil.maxDouble(metricsList, ResponseMetrics::getMaxRt);
        double minRt = NumberUtil.maxDouble(metricsList, ResponseMetrics::getMinRt);
        int activeThreads = NumberUtil.sum(metricsList, ResponseMetrics::getActiveThreads);
        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
        //??????????????????????????????(???????????????)?????????????????????
        //        int activeThreads = isRealThread ? realActiveThreads : (int) Math.ceil(avgRt * avgTps / 1000d);
        //        int activeThreads = realActiveThreads;
        List<String> percentDataList = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPercentData)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentDataList);
        Set<String> podNos = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPodNo)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet());

        int dataNum = CollectionUtils.isEmpty(podNos) ? 0 : podNos.size();
        double dataRate = NumberUtil.getPercentRate(dataNum, podNum, 100d);
        int status = dataNum < podNum ? 0 : 1;
        PressureOutput p = new PressureOutput();
        p.setTime(time);
        p.setTransaction(transaction);
        p.setCount(count);
        p.setFailCount(failCount);
        p.setSaCount(saCount);
        p.setSa(sa);
        p.setSuccessRate(successRate);
        p.setSentBytes(sendBytes);
        p.setReceivedBytes(receivedBytes);
        p.setSumRt(sumRt);
        p.setAvgRt(avgRt);
        p.setMaxRt(maxRt);
        p.setMinRt(minRt);
        p.setActiveThreads(activeThreads);
        p.setAvgTps(avgTps);
        p.setSaPercent(percentSa);
        p.setDataNum(dataNum);
        p.setDataRate(dataRate);
        p.setStatus(status);
        p.setTestName(testName);
        return p;
    }

    private void finishPushData(ReportResult report, Integer podNum, Long timeWindow,
        long endTime, List<ScriptNode> nodes) {
        if (null == report) {
            return;
        }
        Long sceneId = report.getSceneId();
        Long reportId = report.getId();
        Long customerId = report.getTenantId();
        String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
        String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
        long nowTimeWindow = CollectorUtil.getTimeWindowTime(System.currentTimeMillis());
        log.info("finishPushData {}-{}-{} last={}, timeWindow={}, endTime={}, now={}", sceneId, reportId, customerId,
            last,
            showTime(timeWindow), showTime(endTime), showTime(nowTimeWindow));

        if (null != report.getEndTime()) {
            endTime = Math.min(endTime, report.getEndTime().getTime());
        }

        if (ScheduleConstants.LAST_SIGN.equals(last) || (null != timeWindow && timeWindow > endTime)) {
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
            // ????????????????????????
            String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
            Long eTime = (Long)redisTemplate.opsForValue().get(endTimeKey);
            if (null != eTime) {
                log.info("????????????????????????????????????????????????{},?????????????????????{},", showTime(timeWindow), showTime(eTime));
                endTime = Math.min(endTime, eTime);
            } else {
                eTime = endTime;
            }
            long endTimeWindow = CollectorUtil.getTimeWindowTime(endTime);
            log.info("??????????????????????????????????????????{},?????????????????????{},", showTime(timeWindow), showTime(endTimeWindow));
            // ?????? endTime timeWindow
            // ?????????????????? ????????????????????????????????????????????????
            // ?????????????????? ?????? ????????????????????????????????????5???????????? ??????5s
            while (isSaveLastPoint && timeWindow <= endTimeWindow && timeWindow <= nowTimeWindow) {
                timeWindow = reduceMetrics(report, podNum, eTime, timeWindow, nodes);
                timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
            }
            log.info("????????????{}-{}-{},push data ??????", sceneId, reportId, customerId);
            // ?????? SLA?????? ??????PushWindowDataScheduled ??????pod job configMap  ????????????
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, customerId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
            // ?????? timeWindowMap ???key
            String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
            TIME_WINDOW_MAP.remove(tempTimestamp);
            log.info("---> ????????????{}-{}-{}??????????????????finished?????????<------", sceneId, reportId, customerId);
        }
        // ???????????????????????????????????????
        forceClose(nowTimeWindow, sceneId, reportId, customerId);
    }

    /**
     * ??????????????????
     */
    public void pushData2() {
        ReportQueryParam param = new ReportQueryParam();
        param.setStatus(0);
        param.setIsDel(0);
        param.setPressureTypeRelation(new PressureTypeRelation(PressureSceneEnum.INSPECTION_MODE.getCode(), false));
        List<ReportResult> results = reportDao.queryReportList(param);
        if (CollectionUtils.isEmpty(results)) {
            log.debug("??????????????????????????????");
            return;
        }
        List<Long> reportIds = CommonUtil.getList(results, ReportResult::getId);
        log.info("??????????????????????????????" + JsonHelper.bean2Json(reportIds));
        results.stream().filter(Objects::nonNull)
            .map(r -> (Runnable)() -> {
                Long sceneId = r.getSceneId();
                Long reportId = r.getId();
                Long customerId = r.getTenantId();
                String lockKey = String.format("pushData:%s:%s:%s", sceneId, reportId, customerId);
                if (!Boolean.TRUE.equals(lock(lockKey, "1"))) {
                    return;
                }
                try {
                    List<ScriptNode> nodes = JsonUtil.parseArray(r.getScriptNodeTree(), ScriptNode.class);
                    SceneManageWrapperOutput scene = sceneManageService.getSceneManage(sceneId, null);
                    if (null == scene) {
                        log.info("no such scene manager!sceneId=" + sceneId);
                        forceClose(CollectorUtil.getNowTimeWindow(), sceneId, reportId, customerId);
                        return;
                    }
                    //?????????????????????????????????+???????????????+3????????? 3???????????????????????????pod???????????????????????????????????????
                    long endTime = TimeUnit.MINUTES.toMillis(3L);
                    if (null != r.getStartTime()) {
                        endTime += r.getStartTime().getTime();
                    } else if (null != r.getGmtCreate()) {
                        endTime += r.getGmtCreate().getTime();
                    }
                    if (null != scene.getTotalTestTime()) {
                        endTime += TimeUnit.SECONDS.toMillis(scene.getTotalTestTime());
                    } else if (null != scene.getPressureTestSecond()) {
                        endTime += TimeUnit.SECONDS.toMillis(scene.getPressureTestSecond());
                    }
                    int podNum = scene.getIpNum();
                    long nowTimeWindow = CollectorUtil.getNowTimeWindow();
                    long breakTime = Math.min(endTime, nowTimeWindow);
                    Long timeWindow = null;
                    do {
                        //?????????????????????????????????????????????????????????????????????????????????????????????3?????????????????????????????????????????????????????????????????????,??????endTime??????????????????????????????
                        //if(ifReportOutOfTime(sceneId, reportId, customerId,r)){
                        //    log.error("3??????????????????????????????????????????????????????????????????????????????????????????????????????ID:{},??????ID:{}",sceneId,reportId);
                        //    break;
                        //}
                        //???????????????????????????do...while...??????????????????????????????????????????????????????stackOverFlow??????
                        timeWindow = reduceMetrics(r, podNum, breakTime, timeWindow, nodes);
                        if (null == timeWindow) {
                            timeWindow = nowTimeWindow;
                            break;
                        }
                        timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
                    } while (timeWindow <= breakTime);

                    if (null != r.getEndTime() && timeWindow >= r.getEndTime().getTime()) {
                        // ????????????????????????  ?????????????????????,???????????????????????? ---->????????????????????????
                        sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, customerId)
                            .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                            .updateEnum(SceneManageStatusEnum.STOP)
                            .build());
                    }
                    finishPushData(r, podNum, timeWindow, endTime, nodes);
                } catch (Throwable t) {
                    log.error("pushData2 error!", t);
                } finally {
                    unlock(lockKey, "0");
                }
            })
            .forEach(Executors::execute);
    }

    /**
     * ?????????????????????
     * ?????????redis??????10???????????????
     */
    @Async("collectorSchedulerPool")
    @Scheduled(cron = "0/5 * * * * ? ")
    public void pushData() {
        if (!schedulingEnabled) {
            return;
        }
        pushData2();
    }

    /**
     * ???????????????????????????????????????
     *
     * @param timeWindow ??????????????????
     */
    private void forceClose(Long timeWindow, Long sceneId, Long reportId, Long tenantId) {
        String taskKey = getPressureTaskKey(sceneId, reportId, tenantId);
        Long forceTime = (Long)Optional.ofNullable(redisTemplate.opsForValue().get(forceCloseTime(taskKey))).orElse(0L);
        if (forceTime > 0 && timeWindow >= forceTime) {
            log.info("????????????{}-{}-{}:??????????????????????????????????????????????????????????????????-{}???????????????-{}",
                sceneId, reportId, tenantId, forceTime, timeWindow);

            log.info("??????[{}]?????????????????????,????????????????????????{}", sceneId, reportId);
            // ????????????????????????  ?????????????????????,???????????????????????? ---->????????????????????????
            SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
            //????????????????????? ???????????????
            log.info("finish scene {}, state :{}", sceneId, Optional.ofNullable(sceneManage)
                .map(SceneManageEntity::getType)
                .map(SceneManageStatusEnum::getSceneManageStatusEnum)
                .map(SceneManageStatusEnum::getDesc).orElse("???????????????"));
            if (sceneManage != null && !sceneManage.getType().equals(SceneManageStatusEnum.FORCE_STOP.getValue())) {
                sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                    .updateEnum(SceneManageStatusEnum.STOP)
                    .build());
            }
            // ?????? SLA?????? ??????PushWindowDataScheduled ??????pod job configMap  ????????????
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, tenantId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
            // ?????? timeWindowMap ???key
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, tenantId);
            String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
            TIME_WINDOW_MAP.remove(tempTimestamp);
        }
    }

    /**
     * ??????sa
     */
    private String calculateSaPercent(List<String> percentDataList) {
        if (CollectionUtils.isEmpty(percentDataList)) {
            return null;
        }
        List<Map<Integer, RtDataOutput>> percentMapList = percentDataList.stream().filter(StringUtils::isNotBlank)
            .map(DataUtils::parseToPercentMap)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(percentMapList)) {
            return null;
        }
        //????????????
        int total = percentMapList.stream().filter(Objects::nonNull)
            .map(m -> m.get(100))
            .filter(Objects::nonNull)
            .mapToInt(RtDataOutput::getHits)
            .sum();

        //??????rt???????????????
        List<RtDataOutput> rtDataList = percentMapList.stream().filter(Objects::nonNull)
            .peek(DataUtils::percentMapRemoveDuplicateHits)
            .map(Map::values)
            .filter(CollectionUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(RtDataOutput::getTime))
            .collect(Collectors.toList());

        Map<Integer, RtDataOutput> result = new HashMap<>(100);
        //????????????
        //???????????????????????????????????????????????????????????????????????????????????????????????????tong ji
        for (int i = 1; i <= 100; i++) {
            int hits = 0;
            int time = 0;
            double need = total * i / 100d;
            for (RtDataOutput d : rtDataList) {
                if (hits < need || d.getTime() <= time) {
                    hits += d.getHits();
                    if (d.getTime() > time) {
                        time = d.getTime();
                    }
                }
            }
            result.put(i, new RtDataOutput(hits, time));
        }
        return DataUtils.percentMapToString(result);
    }

    private List<SendMetricsEvent> getSendMetricsEventList(Long sceneId, Long reportId, Long customerId,
        long timeWindow, List<PressureOutput> pressureOutputs) {
        return pressureOutputs.stream().filter(Objects::nonNull)
            .map(output -> {
                SendMetricsEvent metrics = new SendMetricsEvent();
                metrics.setTransaction(output.getTransaction());
                metrics.setCount(output.getCount());
                metrics.setFailCount(output.getFailCount());
                metrics.setAvgTps(output.getAvgTps());
                metrics.setAvgRt(output.getAvgRt());
                metrics.setSa(output.getSa());
                metrics.setMaxRt(output.getMaxRt());
                metrics.setMinRt(output.getMinRt());
                metrics.setSuccessRate(output.getSuccessRate());
                metrics.setTimestamp(timeWindow);
                metrics.setReportId(reportId);
                metrics.setSceneId(sceneId);
                metrics.setTenantId(customerId);
                return metrics;
            }).collect(Collectors.toList());
    }

    private String showTime(Long timestamp) {
        if (null == timestamp) {return "";}
        // ????????????????????????
        long d1 = timestamp / DateUnit.DAY.getMillis();
        long d2 = System.currentTimeMillis() / DateUnit.DAY.getMillis();
        // ????????????
        cn.hutool.core.date.DateTime timestampDate = cn.hutool.core.date.DateUtil.date(timestamp);
        String timeString = d1 == d2 ?
            // ???????????????????????? HH:mm:ss
            timestampDate.toTimeStr() :
            // ?????????????????????????????? yyyy-MM-dd HH:mm:ss
            timestampDate.toString();
        // ??????
        return timestamp + "(" + timeString + ")";
    }
}
