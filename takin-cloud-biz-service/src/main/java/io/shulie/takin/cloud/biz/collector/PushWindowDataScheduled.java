package io.shulie.takin.cloud.biz.collector;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.math.RoundingMode;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;

import cn.hutool.core.date.DateUnit;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.data.redis.core.ScanOptions;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.redis.connection.RedisConnection;

import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.biz.utils.Executors;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.eventcenter.entity.TaskConfig;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.utils.CollectorUtil;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.biz.output.statistics.PressureOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;

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
    @Resource
    private AppConfig appConfig;

    @Value("${scheduling.enabled:true}")
    private Boolean schedulingEnabled;

    @Value("${scheduling.delayTime:30000}")
    private int delayTimeWindow;

    @Value("${scheduling.delayQuick:true}")
    private boolean delayQuick;

    @Value("${report.metric.isSaveLastPoint:true}")
    private boolean isSaveLastPoint;

    /**
     * 用于时间窗口 记忆
     */
    private static final Map<String, Long> timeWindowMap = Maps.newConcurrentMap();

    public void sendMetrics(Metrics metrics) {
        Event event = new Event();
        event.setEventName("metricsData");
        event.setExt(metrics);
        eventCenterTemplate.doEvents(event);
    }

    @IntrestFor(event = "started")
    public void doStartScheduleTaskEvent(Event event) {
        log.info("PushWindowDataScheduled，从调度中心收到压测任务启动成功事件");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        String taskKey = getTaskKey(taskBean.getSceneId(), taskBean.getTaskId(), taskBean.getTenantId());
        /*
         * 压测时长 + 预热时长 + 五分钟 7天
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
     * 没有用到
     *
     * @param event-
     */
    @IntrestFor(event = "stop")
    public void doStopTaskEvent(Event event) {
        TaskConfig taskConfig = (TaskConfig)event.getExt();
        delTask(taskConfig.getSceneId(), taskConfig.getTaskId(), taskConfig.getTenantId());
    }

    /**
     * 删除 拉取数据
     */
    @IntrestFor(event = "finished")
    public void doDeleteTaskEvent(Event event) {
        try {
            log.info("通知PushWindowDataScheduled模块，从调度中心收到压测任务结束事件");
            TaskResult taskResult = (TaskResult)event.getExt();
            delTask(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId());
        } catch (Exception e) {
            log.error("【PushWindowDataScheduled】处理finished事件异常={}", e.getMessage(), e);
        }
    }

    private void delTask(Long sceneId, Long reportId, Long tenantId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null || reportResult.getStatus() == 0) {
            log.info("删除收集数据key时，报告还未生成，sceneId:{},reportId:{}", sceneId, reportId);
            return;
        }
        if (null != sceneId && null != reportId) {
            String taskKey = getTaskKey(sceneId, reportId, tenantId);
            redisTemplate.delete(String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, taskKey));
        }
    }

    /**
     * 计算读取的时间窗口
     *
     * @return -
     */
    private Long refreshTimeWindow(String engineName) {
        long timeWindow = 0L;
        String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
        // 插入成功 进行计数
        if (timeWindowMap.containsKey(tempTimestamp)) {
            // 获取下一个5s,并更新redis
            timeWindow = CollectorUtil.addWindowTime(timeWindowMap.get(tempTimestamp));
            timeWindowMap.put(tempTimestamp, timeWindow);
            return timeWindow;
        }
        String startTimeKey = engineName + ScheduleConstants.FIRST_SIGN;
        if (redisTemplate.hasKey(startTimeKey)) {
            // 延迟5s 获取数据
            timeWindow = CollectorUtil.getPushWindowTime(
                CollectorUtil.getTimeWindow((Long)redisTemplate.opsForValue().get(startTimeKey)).getTimeInMillis());
            timeWindowMap.put(tempTimestamp, timeWindow);
        }

        return timeWindow;
    }

    private Long getMetricsMinTimeWindow(Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxUtil.getMetricsMeasurement(sceneId, reportId, customerId);
            ResponseMetrics metrics = influxWriter.querySingle(
                "select * from " + measurement + " where time>0 order by time asc limit 1", ResponseMetrics.class);
            if (null != metrics) {
                timeWindow = metrics.getTime();
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    private List<ResponseMetrics> queryMetrics(Long sceneId, Long reportId, Long customerId, Long timeWindow) {
        try {
            String measurement = InfluxUtil.getMetricsMeasurement(sceneId, reportId, customerId);
            StringBuilder sql = new StringBuilder("select * from");
            sql.append(" ").append(measurement);
            if (null != timeWindow) {
                long time = TimeUnit.NANOSECONDS.convert(timeWindow, TimeUnit.MILLISECONDS);
                sql.append(" ").append("where").append(" ").append("time=").append(time);
                return influxWriter.query(sql.toString(), ResponseMetrics.class);
            } else {
                timeWindow = getMetricsMinTimeWindow(sceneId, reportId, customerId);
                if (null != timeWindow) {
                    return queryMetrics(sceneId, reportId, customerId, timeWindow);
                }
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return null;
    }

    /**
     * 获取当前未完成统计的最小时间窗口
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
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    /**
     * 获取当前统计的最大时间的下一个窗口窗口
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
            log.error("查询失败", e);
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
        //如果时间窗口为空
        if (null == timeWindow) {
            //则通过当前压测统计表的未完成记录时间进行统计（数据统计有缺失的为未完成）
            timeWindow = getWorkingPressureMinTimeWindow(sceneId, reportId, customerId);
            //如果不存在当前未完成记录时间
            if (null == timeWindow) {
                //则根据最新统计记录时间获取下一个时间窗口
                timeWindow = getPressureMaxTimeNextTimeWindow(sceneId, reportId, customerId);
            }
        }
        //如果当前处理的时间窗口已经大于当结束时间窗口，则退出
        if (null != timeWindow && timeWindow > endTime) {
            log.info("{} return 1!timeWindow={}, endTime={}",
                logPre, showTime(timeWindow), showTime(endTime));
            return timeWindow;
        }
        //timeWindow如果为空，则获取全部metrics数据，如果不为空则获取该时间窗口的数据
        List<ResponseMetrics> metricsList = queryMetrics(sceneId, reportId, customerId, timeWindow);
        if (CollectionUtils.isEmpty(metricsList)) {
            log.info("{}, timeWindow={} ， metrics 是空集合!", logPre, showTime(timeWindow));
            return timeWindow;
        }
        log.info("{} queryMetrics timeWindow={}, endTime={}, metricsList.size={}",
            logPre, showTime(timeWindow), showTime(endTime), metricsList.size());
        if (null == timeWindow) {
            timeWindow = metricsList.stream().filter(Objects::nonNull)
                .map(ResponseMetrics::getTime)
                .filter(l -> l > 0)
                .findFirst()
                .orElse(endTime);
        }
        //如果当前处理的时间窗口已经大于结束时间窗口，则退出
        if (timeWindow > endTime) {
            log.info("{} return 3!timeWindow={}, endTime={}",
                logPre, showTime(timeWindow), showTime(endTime));
            return timeWindow;
        }

        List<String> transactions = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getTransaction)
            .filter(StringUtils::isNotBlank)
            //过滤掉控制器
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
            .map(s -> this.filterByTransactionAndPodNo(metricsList, s))
            .filter(CollectionUtils::isNotEmpty)
            .map(l -> this.toPressureOutput(l, podNum, time))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(results)) {
            log.info("results is empty!");
            return timeWindow;
        }

        List<PressureOutput> slaList = new ArrayList<>(results);
        //统计没有回传的节点数据
        if (CollectionUtils.isNotEmpty(nodes)) {
            Map<String, PressureOutput> pressureMap = results.stream().filter(Objects::nonNull)
                    .collect(Collectors.toMap(PressureOutput::getTransaction, o -> o, (o1, o2) -> o1));
            nodes.stream().filter(Objects::nonNull)
                    .forEach(n -> countPressure(n, pressureMap, results));

//                //控制器统计
//                List<ScriptNode> controllerNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.CONTROLLER, nodes);
//                if (CollectionUtils.isNotEmpty(controllerNodes)) {
//                    controllerNodes.stream().filter(Objects::nonNull)
//                        //过滤掉已经有数据的控制器
//                        .filter(c -> !transactions.contains(c.getXpathMd5()))
//                        .forEach(c -> this.summaryNodeMetrics(c, podNum, time, results, slaList));
//
//                    //事务控制器sa计算
//                    controllerNodes.stream().filter(Objects::nonNull)
//                        .filter(c -> transactions.contains(c.getXpathMd5()))
//                        .forEach(c -> this.calculateControllerSa(results,c));
//                }
//                //线程组统计
//                List<ScriptNode> threadGroupNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, nodes);
//                if (CollectionUtils.isNotEmpty(threadGroupNodes)) {
//                    threadGroupNodes.stream().filter(Objects::nonNull)
//                        .filter(n -> !transactions.contains(n.getXpathMd5()))
//                        .forEach(n -> this.summaryNodeMetrics(n, podNum, time, results, slaList));
//                }
//                //测试计划统计
//                List<ScriptNode> testPlanNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.TEST_PLAN, nodes);
//                if (CollectionUtils.isNotEmpty(testPlanNodes)) {
//                    testPlanNodes.stream().filter(Objects::nonNull)
//                        .filter(t -> !transactions.contains(t.getXpathMd5()))
//                        .forEach(t -> this.summaryNodeMetrics(t, podNum, time, results, slaList));
//                }
        }
        //如果是老版本的，统计ALL
        else {
            //todo 排除事务控制器的数据，如何判断是否为事务控制器
            //int allSaCount = results.stream().filter(Objects::nonNull)
            //    .filter(p -> !ReportConstants.ALL_BUSINESS_ACTIVITY.equals(p.getTransaction()))
            //    .map(PressureOutput::getSaCount)
            //    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            //    .sum();
//            results.add(createPressureOutput(results, time, podNum, ReportConstants.ALL_BUSINESS_ACTIVITY, ReportConstants.ALL_BUSINESS_ACTIVITY, null));

        }
        results.stream().filter(Objects::nonNull)
                .map(p -> InfluxUtil.toPoint(measurement, time, p))
                .forEach(influxWriter::insert);
        //sla处理
        try {
            List<SendMetricsEvent> sendMetricsEventList = getSendMetricsEventList(sceneId, reportId, customerId,
                timeWindow, slaList);
            if (ReportConstants.INIT_STATUS == report.getStatus()) {
                sendMetricsEventList.stream().filter(Objects::nonNull)
                        .forEach(this::sendMetrics);
            }
//            //未finish，发事件
//            String existKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY,
//                getTaskKey(sceneId, reportId, customerId));
//            if (Boolean.TRUE.equals(redisTemplate.hasKey(existKey))) {
//                //排除控制器和理线程组，只处理采样器和测试计划
//                sendMetricsEventList.stream().filter(Objects::nonNull)
//                    .forEach(this::sendMetrics);
//            }
        } catch (Exception e) {
            log.error(
                "【collector metric】【error-sendMetricsEvents】 write influxDB time : {} sceneId : {}, reportId : "
                    + "{},customerId : {}, error:{}",
                timeWindow, sceneId, reportId, customerId, e.getMessage());
        }
        log.info("{} finished!timeWindow={}, endTime={}",
            logPre, showTime(timeWindow), showTime(endTime));
        return timeWindow;
    }

    private boolean isController(String transaction, List<ScriptNode> nodes) {
        List<ScriptNode> nodeList = JmxUtil.getScriptNodeByType(NodeTypeEnum.CONTROLLER, nodes);
        if (CollectionUtils.isNotEmpty(nodeList)) {
            List<String> controllerTransactions = nodeList.stream().filter(Objects::nonNull)
                .map(ScriptNode::getXpathMd5)
                .collect(Collectors.toList());
            return controllerTransactions.contains(transaction);
        } else {
            return false;
        }
    }

//    private PressureOutput createPressureOutput(List<PressureOutput> results, long time, int podNum, String transaction, String testName, NodeTypeEnum nodeType) {
//        int count = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getCount)
//            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//            .sum();
//        int failCount = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getFailCount)
//            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//            .sum();
//        int saCount = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getSaCount)
//            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//            .sum();
//        double sa = NumberUtil.getPercentRate(saCount, count);
//        double successRate = NumberUtil.getPercentRate(count - failCount, count);
//        long sendBytes = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getSentBytes)
//            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
//            .sum();
//        long receiveBytes = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getReceivedBytes)
//            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
//            .sum();
//
//        long sumRt = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getSumRt)
//            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
//            .sum();
//
//        double avgRt = NumberUtil.getRate(sumRt, count);
//
//        double maxRt = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getMaxRt)
//            .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
//            .max()
//            .orElse(0);
//
//        double minRt = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getMinRt)
//            .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
//            .min()
//            .orElse(0);
//
////        int realActiveThreads;
//        int activeThreads;
//        if (NodeTypeEnum.TEST_PLAN == nodeType) {
////            realActiveThreads = results.stream().filter(Objects::nonNull)
////                    .map(PressureOutput::getRealActiveThreads)
////                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
////                    .sum();
//            //TEST_PLAN节点取加总
//            activeThreads = results.stream().filter(Objects::nonNull)
//                    .map(PressureOutput::getActiveThreads)
//                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//                    .sum();
//        } else {
////            realActiveThreads = (int) Math.round(results.stream().filter(Objects::nonNull)
////                    .map(PressureOutput::getRealActiveThreads)
////                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
////                    .average()
////                    .orElse(0d));
//            //其他分组节点（控制器、线程组）：取平均
//            activeThreads = results.stream().filter(Objects::nonNull)
//                    .map(PressureOutput::getActiveThreads)
//                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//                    .max()
//                    .orElse(0);
//        }
//        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
//        List<String> percentData = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getSaPercent)
//            .filter(StringUtils::isNotBlank)
//            .collect(Collectors.toList());
//        String percentSa = calculateSaPercent(percentData);
//        int podNos = results.stream().filter(Objects::nonNull)
//            .map(PressureOutput::getDataNum)
//            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//            .findFirst()
//            .orElse(0);
//        double dataRate = NumberUtil.getPercentRate(podNos, podNum, 100d);
//        int status = podNos < podNum ? 0 : 1;
//        PressureOutput output = new PressureOutput();
//        output.setTime(time);
//        output.setCount(count);
//        output.setTransaction(transaction);
//        output.setFailCount(failCount);
//        output.setSaCount(saCount);
//        output.setSa(sa);
//        output.setSuccessRate(successRate);
//        output.setSentBytes(sendBytes);
//        output.setReceivedBytes(receiveBytes);
//        output.setSumRt(sumRt);
//        output.setAvgRt(avgRt);
//        output.setMaxRt(maxRt);
//        output.setMinRt(minRt);
//        output.setActiveThreads(activeThreads);
////        output.setRealActiveThreads(realActiveThreads);
//        output.setAvgTps(avgTps);
//        output.setSaPercent(percentSa);
//        output.setDataNum(podNos);
//        output.setDataRate(dataRate);
//        output.setStatus(status);
//        output.setTestName(testName);
//        return output;
//    }

    private void calculateControllerSa(List<PressureOutput> results,ScriptNode node){
        List<ScriptNode> samplerNode = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, node.getChildren());
        PressureOutput output = results.stream().filter(r -> r.getTransaction().equals(node.getXpathMd5()))
            .findFirst().orElse(null);
        if (Objects.isNull(output)){
            return;
        }
        if (Objects.nonNull(output.getSaCount()) && output.getSaCount() > 0) {
            return;
        }
        Map<String, List<PressureOutput>> dataMap = results.stream().collect(
            Collectors.groupingBy(PressureOutput::getTransaction));
        List<PressureOutput> tmpData = new ArrayList<>();
        List<String> samplerTransactions = CommonUtil.getList(samplerNode, ScriptNode::getXpathMd5);
        for (Map.Entry<String, List<PressureOutput>> entry : dataMap.entrySet()) {
            if (samplerTransactions.contains(entry.getKey())) {
                tmpData.addAll(entry.getValue());
            }
        }
        double sa = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSa)
            .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
            .min()
            .getAsDouble();
        int saCount = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSaCount)
            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            .min()
            .getAsInt();
        output.setSaCount(saCount);
        output.setSa(sa);
    }

    /**
     * 统计各个节点的数据
     * @param node 节点
     * @param sourceMap 现有的数据
     * @param results   数据结果集合
     * @return          返回当前节点的统计结果
     */
    private PressureOutput countPressure(ScriptNode node, Map<String, PressureOutput> sourceMap, List<PressureOutput> results) {
        if (null == node || StringUtils.isBlank(node.getXpathMd5())) {
            return null;
        }
        PressureOutput result = sourceMap.get(node.getXpathMd5());
        if (null != result) {
            return result;
        }
        if (CollectionUtils.isEmpty(node.getChildren())) {
            return null;
        }
        List<PressureOutput> childPressures = node.getChildren().stream().filter(Objects::nonNull)
                .map(n -> countPressure(n, sourceMap, results))
                .collect(Collectors.toList());
        result = countPressure(node, childPressures);
        if (null != result) {
            results.add(result);
        }
        return result;
    }

    /**
     * 根据子节点统计结果来统计当前节点的数据
     * @param node              当前节点
     * @param childPressures    子节点统计数据结果
     * @return                  返回当前节点统计结果
     */
    private PressureOutput countPressure(ScriptNode node, List<PressureOutput> childPressures) {
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
        int count = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getCount)
                .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                .sum();
        int failCount = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getFailCount)
                .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                .sum();
        int saCount = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getSaCount)
                .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                .sum();
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getSentBytes)
                .mapToLong(l -> Objects.isNull(l) ? 0 : l)
                .sum();
        long receiveBytes = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getReceivedBytes)
                .mapToLong(l -> Objects.isNull(l) ? 0 : l)
                .sum();

        long sumRt = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getSumRt)
                .mapToLong(l -> Objects.isNull(l) ? 0 : l)
                .sum();

        double avgRt = NumberUtil.getRate(sumRt, count);

        double maxRt = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getMaxRt)
                .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
                .max()
                .orElse(0);

        double minRt = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getMinRt)
                .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
                .min()
                .orElse(0);

//        int realActiveThreads;
        int activeThreads;
        double avgTps;
        if (NodeTypeEnum.TEST_PLAN == node.getType()) {
//            realActiveThreads = results.stream().filter(Objects::nonNull)
//                    .map(PressureOutput::getRealActiveThreads)
//                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//                    .sum();
            //TEST_PLAN节点取加总
            activeThreads = childPressures.stream().filter(Objects::nonNull)
                    .map(PressureOutput::getActiveThreads)
                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                    .sum();
            avgTps = childPressures.stream().filter(Objects::nonNull)
                    .map(PressureOutput::getAvgTps)
                    .mapToDouble(d -> Objects.isNull(d) ? 0d : d)
                    .sum();
        } else {
//            realActiveThreads = (int) Math.round(results.stream().filter(Objects::nonNull)
//                    .map(PressureOutput::getRealActiveThreads)
//                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//                    .average()
//                    .orElse(0d));
            //其他分组节点（控制器、线程组）：取平均
            activeThreads = childPressures.stream().filter(Objects::nonNull)
                    .map(PressureOutput::getActiveThreads)
                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                    .max()
                    .orElse(0);
            avgTps = childPressures.stream().filter(Objects::nonNull)
                    .map(PressureOutput::getAvgTps)
                    .mapToDouble(d -> Objects.isNull(d) ? 0d : d)
                    .max()
                    .orElse(0d);
        }
        List<String> percentData = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getSaPercent)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentData);
        int dataNum = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getDataNum)
                .filter(Objects::nonNull)
                .mapToInt(d -> d)
                .min()
                .orElse(1);
        double dataRate = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getDataRate)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d)
                .min()
                .orElse(1d);
        int status = childPressures.stream().filter(Objects::nonNull)
                .map(PressureOutput::getStatus)
                .filter(Objects::nonNull)
                .mapToInt(i -> i)
                .min()
                .orElse(1);
        PressureOutput output = new PressureOutput();
        output.setTime(time);
        output.setCount(count);
        output.setTransaction(node.getXpathMd5());
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
//        output.setRealActiveThreads(realActiveThreads);
        output.setAvgTps(avgTps);
        output.setSaPercent(percentSa);
        output.setDataNum(dataNum);
        output.setDataRate(dataRate);
        output.setStatus(status);
        output.setTestName(node.getTestName());
        return output;
    }

    /**
     * 单个时间窗口数据，根据transaction过滤，并且每个pod只取1条数据
     */
    private List<ResponseMetrics> filterByTransactionAndPodNo(List<ResponseMetrics> metricsList, String transaction) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return metricsList;
        }
        List<String> pods = Lists.newArrayList();
        return metricsList.stream().filter(Objects::nonNull)
            .filter(m -> transaction.equals(m.getTransaction()))
            .filter(m -> !pods.contains(m.getPodNo()))
            .peek(m -> pods.add(m.getPodNo()))
            .collect(Collectors.toList());
    }

    /**
     * 判断回传的transaction中是不否包含目标controller的xpathMD5值
     *
     * @param transactions 回传的节点
     * @param transaction  要验证的controller节点
     * @return 是否为控制器
     */
    private boolean filterScriptNodeController(List<String> transactions, String transaction) {
        if (CollectionUtils.isEmpty(transactions)) {
            return true;
        }
        return !transactions.contains(transaction);
    }

//    /**
//     * 统计节点中没有上报的测试计划、线程组、控制器的请求信息
//     *
//     * @param targetNode 需要统计目标节点
//     * @param podNum     podNum
//     * @param time       时间窗口
//     * @param data       经过统计计算的metrics数据
//     */
//    private void summaryNodeMetrics(ScriptNode targetNode, int podNum, Long time, List<PressureOutput> data, List<PressureOutput> slaList) {
//        String transaction = targetNode.getXpathMd5();
//        String testName = targetNode.getTestName();
//
//        Map<String, PressureOutput> dataMap = data.stream().collect(Collectors.toMap(PressureOutput::getTransaction, o -> o, (o1, o2) -> o1));
//        List<PressureOutput> tmpData = new ArrayList<>();
//        if (NodeTypeEnum.TEST_PLAN == targetNode.getType()) {
//            PressureOutput all = dataMap.get("all");
//            if (null != all) {
//                tmpData.add(all);
//            }
//        }
//        if (CollectionUtils.isEmpty(tmpData)) {
//            List<ScriptNode> childSamplers = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, targetNode.getChildren());
//            List<String> childTransactions = CommonUtil.getList(childSamplers, ScriptNode::getXpathMd5);
//            if (CollectionUtils.isNotEmpty(childTransactions)) {
//                childTransactions.stream().filter(Objects::nonNull)
//                        .map(dataMap::get)
//                        .filter(Objects::nonNull)
//                        .forEach(tmpData::add);
//            }
//        }
//
//        PressureOutput pressureOutput = createPressureOutput(tmpData, time, podNum, transaction, testName, targetNode.getType());
//        data.add(pressureOutput);
//        if (CollectionUtils.isNotEmpty(slaList) && targetNode.getType() == NodeTypeEnum.TEST_PLAN) {
//            slaList.add(pressureOutput);
//        }
//    }

    /**
     * 实时数据统计
     */
    private PressureOutput toPressureOutput(List<ResponseMetrics> metricsList, Integer podNum, long time) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return null;
        }
        String transaction = metricsList.get(0).getTransaction();
        String testName = metricsList.get(0).getTestName();

        int count = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getCount)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        int failCount = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getFailCount)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        int saCount = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getSaCount)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getSentBytes)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        long receivedBytes = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getReceivedBytes)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        long sumRt = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getSumRt)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        double avgRt = NumberUtil.getRate(sumRt, count);
        double maxRt = metricsList.stream().filter(Objects::nonNull)
            .mapToDouble(ResponseMetrics::getMaxRt)
            .filter(Objects::nonNull)
            .max()
            .orElse(0);
        double minRt = metricsList.stream().filter(Objects::nonNull)
            .mapToDouble(ResponseMetrics::getMinRt)
            .filter(Objects::nonNull)
            .min()
            .orElse(0);
        //实际线程数
        int activeThreads = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getActiveThreads)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
        //模型运算修正的线程数(有效线程数)，顺丰需要这个
//        int activeThreads = isRealThread ? realActiveThreads : (int) Math.ceil(avgRt * avgTps / 1000d);
//        int activeThreads = realActiveThreads;
        List<String> percentDataList = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPercentData)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentDataList);
        List<String> podNos = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPodNo)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
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
//        p.setRealActiveThreads(realActiveThreads);
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
            // 只需触发一次即可
            String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
            Long eTime = (Long)redisTemplate.opsForValue().get(endTimeKey);
            if (null != eTime) {
                log.info("触发手动收尾操作，当前时间窗口：{},结束时间窗口：{},", showTime(timeWindow), showTime(eTime));
                endTime = Math.min(endTime, eTime);
            } else {
                eTime = endTime;
            }
            long endTimeWindow = CollectorUtil.getTimeWindowTime(endTime);
            log.info("触发收尾操作，当前时间窗口：{},结束时间窗口：{},", showTime(timeWindow), showTime(endTimeWindow));
            // 比较 endTime timeWindow
            // 如果结束时间 小于等于当前时间，数据不用补充，
            // 如果结束时间 大于 当前时间，需要补充期间每5秒的数据 延后5s
            while (isSaveLastPoint && timeWindow <= endTimeWindow && timeWindow <= nowTimeWindow) {
                timeWindow = reduceMetrics(report, podNum, eTime, timeWindow, nodes);
                timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
            }
            log.info("本次压测{}-{}-{},push data 完成", sceneId, reportId, customerId);
            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, customerId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
            // 删除 timeWindowMap 的key
            String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
            timeWindowMap.remove(tempTimestamp);
            log.info("---> 本次压测{}-{}-{}完成，已发送finished事件！<------", sceneId, reportId, customerId);
        }
        // 超时自动检修，强行触发关闭
        forceClose(nowTimeWindow, sceneId, reportId, customerId);
    }

    /**
     * 实时数据统计
     */
    public void pushData2() {
        ReportQueryParam param = new ReportQueryParam();
        param.setStatus(0);
        param.setIsDel(0);
        List<ReportResult> results = reportDao.queryReportList(param);
        if (CollectionUtils.isEmpty(results)) {
            log.debug("没有需要统计的报告！");
            return;
        }
        List<Long> reportIds = CommonUtil.getList(results, ReportResult::getId);
        log.info("找到需要统计的报告：" + JsonHelper.bean2Json(reportIds));
        results.stream().filter(Objects::nonNull)
            .map(r -> (Runnable)() -> {
                Long sceneId = r.getSceneId();
                Long reportId = r.getId();
                Long customerId = r.getTenantId();
                String lockKey = String.format("pushData:%s:%s:%s", sceneId, reportId, customerId);
                if (!lock(lockKey, "1")) {
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
                    //结束时间取开始压测时间+总测试时间+3分钟， 3分钟富裕时间，给与pod启动和压测引擎启动延时时间
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
                        //获取最后一条数据的时间，如果最后一条回传数据的时间比当前时间少3分钟以上，则认为引擎不会继续回传数据了，结束掉,设置endTime为最后一条数据的时间
                        //if(ifReportOutOfTime(sceneId, reportId, customerId,r)){
                        //    log.error("3分钟未收到压测引擎回传数据或上条数据已超过三分钟，停止数据收集，场景ID:{},报告ID:{}",sceneId,reportId);
                        //    break;
                        //}
                        //不用递归，而是采用do...while...的方式是防止需要处理的时间段太长引起stackOverFlow错误
                        timeWindow = reduceMetrics(r, podNum, breakTime, timeWindow, nodes);
                        if (null == timeWindow) {
                            timeWindow = nowTimeWindow;
                            break;
                        }
                        timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
                    } while (timeWindow <= breakTime);

                    if (null != r.getEndTime() && timeWindow >= r.getEndTime().getTime()) {
                        // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
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
     * 暂时先不用
     *
     * @param sceneId    场景主键
     * @param reportId   报告主键
     * @param customerId 租户主键
     * @param report     报告信息
     * @return 是/否
     */
    private boolean ifReportOutOfTime(Long sceneId, Long reportId, Long customerId, ReportResult report) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ")
            .append(InfluxUtil.getMetricsMeasurement(sceneId, reportId, customerId))
            .append(" order by create_time desc limit 1");
        try {
            PressureOutput pressure = influxWriter.querySingle(sql.toString(), PressureOutput.class);
            if (Objects.nonNull(pressure)) {
                long lastMetricsTime = pressure.getTime();
                if (lastMetricsTime > 0) {
                    long s = (System.currentTimeMillis() - lastMetricsTime) / (1000 * 60);
                    if (s > 3) {
                        report.setEndTime(new Date(lastMetricsTime));
                        return true;
                    }
                }
            } else {
                long s = (System.currentTimeMillis() - report.getStartTime().getTime()) / (1000 * 60);
                if (s > 3) {
                    report.setEndTime(new Date());
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("查询metrics数据异常:{}", e.toString());
        }
        return false;
    }

    /**
     * 每五秒执行一次
     * 每次从redis中取10秒前的数据
     */
    @Async("collectorSchedulerPool")
    @Scheduled(cron = "0/5 * * * * ? ")
    public void pushData() {
        if (!schedulingEnabled) {
            return;
        }
        pushData2();
//        if (StringUtils.isNotBlank(appConfig.getCollector()) && "influxdb".equalsIgnoreCase(appConfig.getCollector())) {
//            pushData2();
//            return;
//        }
//        pushData1();
    }

//    public void pushData1() {
//        try {
//            Set<String> keys = this.keys(String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, "*"));
//            for (String sceneReportKey : keys) {
//                Executors.execute(() -> {
//                    try {
//                        int lastIndex = sceneReportKey.lastIndexOf(":");
//                        if (-1 == lastIndex) {
//                            return;
//                        }
//                        String sceneReportId = sceneReportKey.substring(lastIndex + 1);
//                        String[] split = sceneReportId.split("_");
//                        Long sceneId = Long.valueOf(split[0]);
//                        Long reportId = Long.valueOf(split[1]);
//                        Long customerId = null;
//                        if (split.length == 3) {
//                            customerId = Long.valueOf(split[2]);
//                        }
//                        ReportResult reportResult = reportDao.selectById(reportId);
//                        SceneManageEntity sceneManageEntity = sceneManageDAO.queueSceneById(sceneId);
//                        if (SceneManageStatusEnum.ifFree(sceneManageEntity.getStatus())) {
//                            delTask(sceneId, reportId, customerId);
//                            return;
//                        }
//
//                        if (lock(sceneReportKey, "collectorSchedulerPool")) {
//                            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
//
//                            // 记录一个redis 时间计数开始时间的时间窗口开始
//                            long timeWindow = refreshTimeWindow(engineName);
//
//                            if (timeWindow == 0) {
//                                unlock(sceneReportKey, "collectorSchedulerPool");
//                                return;
//                            }
//                            List<String> transactions = (List<String>)redisTemplate.opsForValue().get(sceneReportKey);
//                            if (null == transactions || transactions.size() == 0) {
//                                unlock(sceneReportKey, "collectorSchedulerPool");
//                                return;
//                            }
//                            log.info("【collector metric】{}-{}-{}:{}", sceneId, reportId, customerId, timeWindow);
//                            String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
//                            //判断是否有数据延迟了，需要加快处理流程，否则会丢失数据,保证当前线程刷的数据始终在30s之类
//                            if (delayQuick) {
//                                while (timeWindow > 0 && System.currentTimeMillis() - timeWindow > delayTimeWindow) {
//                                    log.warn("当前push Data延迟时间为{}", timeWindow);
//                                    final Long customerIdTmp = customerId;
//                                    final long delayTmp = timeWindow;
//                                    Executors.execute(() -> writeInfluxDatabase(transactions, taskKey, delayTmp, sceneId, reportId,
//                                        customerIdTmp, reportResult.getScriptNodeTree()));
//                                    timeWindow = refreshTimeWindow(engineName);
//                                }
//                            }
//                            // 写入数据
//                            writeInfluxDatabase(transactions, taskKey, timeWindow, sceneId, reportId, customerId,
//                                reportResult.getScriptNodeTree());
//                            // 读取结束标识   手动收尾
//                            String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
//                            if (ScheduleConstants.LAST_SIGN.equals(last)) {
//                                // 只需触发一次即可
//                                String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
//                                long endTime = CollectorUtil.getEndWindowTime(
//                                    (Long)redisTemplate.opsForValue().get(endTimeKey));
//                                log.info("触发手动收尾操作，当前时间窗口：{},结束时间窗口：{},", timeWindow, endTime);
//                                // 比较 endTime timeWindow
//                                // 如果结束时间 小于等于当前时间，数据不用补充，
//                                // 如果结束时间 大于 当前时间，需要补充期间每5秒的数据 延后5s
//                                endTime = CollectorUtil.addWindowTime(endTime);
//                                while (isSaveLastPoint && endTime > timeWindow) {
//                                    timeWindow = CollectorUtil.addWindowTime(timeWindow);
//                                    // 1、确保 redis->influxDB
//                                    log.info("redis->influxDB，当前时间窗口：{},", timeWindow);
//                                    writeInfluxDatabase(transactions, taskKey, timeWindow, sceneId, reportId, customerId,
//                                        reportResult.getScriptNodeTree());
//                                }
//                                log.info("本次压测{}-{}-{},metric数据已经全部上报influxDB", sceneId, reportId, customerId);
//                                // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
//                                Event event = new Event();
//                                event.setEventName("finished");
//                                event.setExt(new TaskResult(sceneId, reportId, customerId));
//                                eventCenterTemplate.doEvents(event);
//                                redisTemplate.delete(last(taskKey));
//                                // 删除 timeWindowMap 的key
//                                String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
//                                timeWindowMap.remove(tempTimestamp);
//                            }
//
//                            if (reportResult.getEndTime() != null) {
//                                // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
//                                sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, customerId)
//                                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
//                                    .updateEnum(SceneManageStatusEnum.STOP)
//                                    .build());
//                            }
//                            // 超时自动检修，强行触发关闭
//                            forceClose(taskKey, timeWindow, sceneId, reportId, customerId);
//                        }
//                    } catch (Exception e) {
//                        log.error("【collector】Real-time data analysis for anomalies hashKey:{}, error:{}",
//                            sceneReportKey, e);
//                    } finally {
//                        unlock(sceneReportKey, "collectorSchedulerPool");
//                    }
//                });
//            }
//        } catch (Throwable e) {
//            log.error(e.getMessage(), e);
//        }
//    }

    /**
     * 超时自动检修，强行触发关闭
     *
     * @param taskKey    任务key
     * @param timeWindow 数据窗口
     */
    private void forceClose(Long timeWindow, Long sceneId, Long reportId, Long tenantId) {
        String taskKey = getPressureTaskKey(sceneId, reportId, tenantId);
        Long forceTime = (Long)Optional.ofNullable(redisTemplate.opsForValue().get(forceCloseTime(taskKey))).orElse(0L);
        if (forceTime > 0 && timeWindow >= forceTime) {
            log.info("本次压测{}-{}-{}:触发超时自动检修，强行触发关闭，超时延迟时间-{}，触发时间-{}",
                sceneId, reportId, tenantId, forceTime, timeWindow);

            log.info("场景[{}]压测任务已完成,将要开始更新报告{}", sceneId, reportId);
            // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
            SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
            //如果是强制停止 不需要更新
            log.info("finish scene {}, state :{}", sceneId, Optional.ofNullable(sceneManage)
                .map(SceneManageEntity::getType)
                .map(SceneManageStatusEnum::getSceneManageStatusEnum)
                .map(SceneManageStatusEnum::getDesc).orElse("未找到场景"));
            if (sceneManage != null && !sceneManage.getType().equals(SceneManageStatusEnum.FORCE_STOP.getValue())) {
                sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                    .updateEnum(SceneManageStatusEnum.STOP)
                    .build());
            }
            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, tenantId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
            // 删除 timeWindowMap 的key
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, tenantId);
            String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
            timeWindowMap.remove(tempTimestamp);
        }
    }

//    private void writeInfluxDatabase(List<String> transactions, String taskKey, long timeWindow, Long sceneId, Long reportId,
//        Long tenantId, String nodeTree) {
//        long start = System.currentTimeMillis();
//        List<PressureOutput> resultList = new ArrayList<>();
//        for (String transaction : transactions) {
//            Integer count = getIntValue(countKey(taskKey, transaction, timeWindow));
//            //todo 判断是否控制器，如果是控制器
//            if (null == count || count < 1) {
//                log.error(
//                    "【collector metric】【null == count || count < 1】 write influxDB time : {},{}-{}-{}-{}, ", timeWindow,
//                    sceneId, reportId, tenantId, transaction);
//                continue;
//            }
//            Integer failCount = getIntValue(failCountKey(taskKey, transaction, timeWindow));
//            Integer saCount = getIntValue(saCountKey(taskKey, transaction, timeWindow));
//            Long sumRt = getLongValueFromMap(rtKey(taskKey, transaction, timeWindow));
//
//            Double maxRt = getDoubleValue(maxRtKey(taskKey, transaction, timeWindow));
//            Double minRt = getDoubleValue(minRtKey(taskKey, transaction, timeWindow));
//
//            Integer activeThreads = getIntValue(activeThreadsKey(taskKey, transaction, timeWindow));
//            Double avgTps = getAvgTps(count);
//            // 算平均rt
//            Double avgRt = getAvgRt(count, sumRt);
//            Double saRate = getSaRate(count, saCount);
//            Double successRate = getSuccessRate(count, failCount);
//
//            List<String> percentDataList = getStringValue(percentDataKey(taskKey, transaction, timeWindow));
//            String percentSa = calculateSaPercent(percentDataList);
//
//            Map<String, String> tags = new HashMap<>(0);
//            tags.put("transaction", transaction);
//            if (StringUtils.isNotBlank(nodeTree)) {
//                PressureOutput output = new PressureOutput();
//                output.setTransaction(transaction);
//                List<String> stringValue = getStringValue(testNameKey(taskKey, transaction, timeWindow));
//                if (CollectionUtils.isNotEmpty(stringValue)) {
//                    output.setTestName(stringValue.get(0));
//                }
//                output.setCount(count);
//                output.setFailCount(failCount);
//                output.setSaCount(saCount);
//                output.setSumRt(sumRt);
//                output.setMaxRt(maxRt);
//                output.setMinRt(minRt);
//                output.setAvgRt(avgRt);
//                output.setAvgTps(avgTps);
//                output.setSa(saRate);
//                output.setSuccessRate(successRate);
//                output.setSaPercent(percentSa);
//                output.setActiveThreads(activeThreads);
//                resultList.add(output);
//            } else {
//                Map<String, Object> fields = getInfluxdbFieldMap(count, failCount,
//                    saCount, sumRt, maxRt, minRt, avgTps, avgRt, saRate, successRate, activeThreads, percentSa);
//                log.debug("metrics数据入库:时间窗:{},percentSa:{}", timeWindow, percentDataList);
//                influxWriter.insert(InfluxUtil.getMeasurement(sceneId, reportId, tenantId), tags,
//                    fields, timeWindow);
//            }
//            try {
//                SendMetricsEvent metrics = getSendMetricsEvent(sceneId, reportId, tenantId, timeWindow,
//                    transaction, count, failCount, maxRt, minRt, avgTps, avgRt,
//                    saRate, successRate);
//                //未finish，发事件
//                String existKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY,
//                    getTaskKey(sceneId, reportId, tenantId));
//                if (redisTemplate.hasKey(existKey)) {
//                    sendMetrics(metrics);
//                }
//            } catch (Exception e) {
//                log.error(
//                    "【collector metric】【error】 write influxDB time : {} sceneId : {}, reportId : {},tenantId : {}, "
//                        + "error:{}",
//                    timeWindow, sceneId, reportId, tenantId, e.getMessage());
//            }
//            long end = System.currentTimeMillis();
//            log.info(
//                "【collector metric】【success】 write influxDB time : {},write time：{} sceneId : {}, reportId : {},"
//                    + "tenantId : {}",
//                timeWindow, (end - start), sceneId, reportId, tenantId);
//
//        }
//        if (CollectionUtils.isNotEmpty(resultList)) {
//            int allSaCount = resultList.stream().filter(Objects::nonNull)
//                .map(PressureOutput::getCount)
//                .mapToInt(i -> Objects.isNull(i) ? 0 : i)
//                .sum();
//            List<ScriptNode> childControllers = JsonPathUtil.getChildControllers(nodeTree, null);
//            childControllers.stream().filter(Objects::nonNull)
//                .filter(c -> !transactions.contains(c.getXpathMd5()))
//                .forEach(c -> this.summaryNodeMetrics(c, 0, timeWindow, resultList, null));
//
//            List<ScriptNode> threadGroupNodes = JsonPathUtil.getChildrenByMd5(nodeTree, null,
//                NodeTypeEnum.THREAD_GROUP);
//            if (CollectionUtils.isNotEmpty(threadGroupNodes)) {
//                threadGroupNodes.stream().filter(Objects::nonNull)
//                    .filter(c -> !transactions.contains(c.getXpathMd5()))
//                    .forEach(c -> this.summaryNodeMetrics(c, 0, timeWindow, resultList, null));
//            }
//
//            String measurement = InfluxUtil.getMeasurement(sceneId, reportId, tenantId);
//            resultList.stream().filter(Objects::nonNull)
//                .peek(o -> {
//                    if ("all".equalsIgnoreCase(o.getTransaction())) {
//                        o.setSaCount(allSaCount);
//                        List<ScriptNode> testPlans = JsonPathUtil.getChildrenByMd5(nodeTree, null,
//                            NodeTypeEnum.TEST_PLAN);
//                        if (CollectionUtils.isNotEmpty(testPlans)) {
//                            String testPlanTransaction = testPlans.stream().filter(Objects::nonNull)
//                                .map(ScriptNode::getXpathMd5)
//                                .filter(StringUtils::isNotBlank)
//                                .findFirst()
//                                .orElse("all");
//                            o.setTransaction(testPlanTransaction);
//                        }
//                    }
//                })
//                .map(p -> InfluxUtil.toPoint(measurement, timeWindow, p))
//                .forEach(influxWriter::insert);
//        }
//    }

    /**
     * 计算sa
     */
    private String calculateSaPercent(List<String> percentDataList) {
        List<Map<Integer, RtDataOutput>> percentMapList = percentDataList.stream().filter(StringUtils::isNotBlank)
            .map(DataUtils::parseToPercentMap)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(percentMapList)) {
            return null;
        }
        //请求总数
        int total = percentMapList.stream().filter(Objects::nonNull)
            .map(m -> m.get(100))
            .filter(Objects::nonNull)
            .mapToInt(RtDataOutput::getHits)
            .sum();

        //所有rt按耗时排序
        List<RtDataOutput> rtDataList = percentMapList.stream().filter(Objects::nonNull)
            .peek(DataUtils::percentMapRemoveDuplicateHits)
            .map(Map::values)
            .filter(CollectionUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(RtDataOutput::getTime))
            .collect(Collectors.toList());

        Map<Integer, RtDataOutput> result = new HashMap<>(100);
        //计算逻辑
        //每个百分点的目标请求数，如果统计达标，进行下一个百分点的统计，如果tong ji
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

    private Map<String, Object> getInfluxdbFieldMap(Integer count, Integer failCount, Integer saCount, Long sumRt,
        Double maxRt, Double minRt, Double avgTps, Double avgRt, Double saRate, Double successRate,
        Integer activeThreads, String saPercent) {
        Map<String, Object> fields = new HashMap<>(0);
        fields.put("count", count);
        fields.put("fail_count", failCount);
        fields.put("sa_count", saCount);
        fields.put("sum_rt", sumRt);
        fields.put("max_rt", maxRt);
        fields.put("min_rt", minRt);

        fields.put("avg_tps", avgTps);
        fields.put("avg_rt", avgRt);
        fields.put("sa", saRate);
        fields.put("sa_percent", saPercent);
        fields.put("success_rate", successRate);
        fields.put("active_threads", activeThreads);
        fields.put("write_time", System.currentTimeMillis());
        return fields;
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

    private SendMetricsEvent getSendMetricsEvent(Long sceneId, Long reportId, Long tenantId, long timeWindow,
        String transaction,
        Integer count, Integer failCount, Double maxRt, Double minRt, Double avgTps, Double avgRt, Double saRate,
        Double successRate) {
        SendMetricsEvent metrics = new SendMetricsEvent();
        metrics.setTransaction(transaction);
        metrics.setCount(count);
        metrics.setFailCount(failCount);
        metrics.setAvgTps(avgTps);
        metrics.setAvgRt(avgRt);
        metrics.setSa(saRate);
        metrics.setMaxRt(maxRt);
        metrics.setMinRt(minRt);
        metrics.setSuccessRate(successRate);
        metrics.setTimestamp(timeWindow);
        metrics.setReportId(reportId);
        metrics.setSceneId(sceneId);
        metrics.setTenantId(tenantId);
        return metrics;
    }

    private void scan(String pattern, Consumer<byte[]> consumer) {
        this.redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern)
                .build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (Exception e) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_RUNNING_GET_RUNNING_JOB_KEY, "获取运行中的job失败！",
                    e);
            }
        });
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return -
     */
    public Set<String> keys(String pattern) {
        Set<String> keys = new HashSet<>();
        this.scan(pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }

    /**
     * 平均TPS计算
     *
     * @return -
     */
    private Double getAvgTps(Integer count) {
        if (null == count || 0 == count) {
            return 0d;
        }
        BigDecimal countDecimal = BigDecimal.valueOf(count);
        return countDecimal.divide(BigDecimal.valueOf(CollectorConstants.SEND_TIME), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    /**
     * 平均RT 计算
     *
     * @return -
     */
    private Double getAvgRt(Integer count, Long sumRt) {
        if (null == count || 0 == count) {
            return 0d;
        }
        BigDecimal countDecimal = BigDecimal.valueOf(count);
        BigDecimal rtDecimal = BigDecimal.valueOf(sumRt);
        return rtDecimal.divide(countDecimal, 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 事物成功率
     *
     * @return -
     */
    private Double getSaRate(Integer count, Integer saCount) {
        if (null == count || 0 == count) {
            return 0d;
        }
        BigDecimal countDecimal = BigDecimal.valueOf(count);
        BigDecimal saCountDecimal = BigDecimal.valueOf(saCount);
        return saCountDecimal.divide(countDecimal, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            .doubleValue();
    }

    /**
     * 请求成功率
     *
     * @return -
     */
    private Double getSuccessRate(Integer count, Integer failCount) {
        if (null == count || 0 == count) {
            return 0d;
        }
        BigDecimal countDecimal = BigDecimal.valueOf(count);
        BigDecimal failCountDecimal = BigDecimal.valueOf(failCount);
        return countDecimal.subtract(failCountDecimal).multiply(BigDecimal.valueOf(100)).divide(countDecimal, 2,
            RoundingMode.HALF_UP).doubleValue();
    }

    private String showTime(Long timestamp) {
        if (null == timestamp) {return "";}
        // 忽略时间精度到天
        long d1 = timestamp / DateUnit.DAY.getMillis();
        long d2 = System.currentTimeMillis() / DateUnit.DAY.getMillis();
        // 转换时间
        cn.hutool.core.date.DateTime timestampDate = cn.hutool.core.date.DateUtil.date(timestamp);
        String timeString = d1 == d2 ?
            // 同一日则显示时间 HH:mm:ss
            timestampDate.toTimeStr() :
            // 不同日则显示日期时间 yyyy-MM-dd HH:mm:ss
            timestampDate.toString();
        // 返回
        return timestamp + "(" + timeString + ")";
    }
}
