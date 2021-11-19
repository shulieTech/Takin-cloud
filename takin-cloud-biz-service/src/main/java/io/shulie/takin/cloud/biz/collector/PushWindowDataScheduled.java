package io.shulie.takin.cloud.biz.collector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressureOutput;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.biz.utils.Executors;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.influxdb.InfluxDBUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.*;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportDataQueryParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageResult;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.eventcenter.entity.TaskConfig;
import io.shulie.takin.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        String taskKey = getTaskKey(taskBean.getSceneId(), taskBean.getTaskId(), taskBean.getCustomerId());
        /*
         * 压测时长 + 预热时长 + 五分钟 7天
         */
        long taskTimeout = 7L * 24 * 60 * 60;
        Map<String, Object> extMap = taskBean.getExtendMap();
        List<String> refList = Lists.newArrayList();
        if (MapUtils.isNotEmpty(extMap)) {
            refList.addAll((List)extMap.get("businessActivityBindRef"));
        }
        ArrayList<String> transation = new ArrayList<>(refList);
        transation.add("all");
        String redisKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, taskKey);
        redisTemplate.opsForValue().set(redisKey, transation, taskTimeout, TimeUnit.SECONDS);
        log.info("PushWindowDataScheduled Create Redis Key = {}, expireDuration={}min, refList={} Success....",
            redisKey, taskTimeout, refList);
    }

    // todo 没有用到
    @IntrestFor(event = "stop")
    public void doStopTaskEvent(Event event) {
        TaskConfig taskConfig = (TaskConfig)event.getExt();
        delTask(taskConfig.getSceneId(), taskConfig.getTaskId(), taskConfig.getCustomerId());
    }

    /**
     * 删除 拉取数据
     */
    @IntrestFor(event = "finished")
    public void doDeleteTaskEvent(Event event) {
        try {
            log.info("通知PushWindowDataScheduled模块，从调度中心收到压测任务结束事件");
            TaskResult taskResult = (TaskResult)event.getExt();
            delTask(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getCustomerId());
        } catch (Exception e) {
            log.error("【PushWindowDataScheduled】处理finished事件异常={}", e.getMessage(), e);
        }
    }

    private void delTask(Long sceneId, Long reportId, Long customerId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null || reportResult.getStatus() == 0) {
            log.info("删除收集数据key时，报告还未生成，sceneId:{},reportId:{}", sceneId, reportId);
            return;
        }
        if (null != sceneId && null != reportId) {
            String taskKey = getTaskKey(sceneId, reportId, customerId);
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
            String measurement = InfluxDBUtil.getMetricsMeasurement(sceneId, reportId, customerId);
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
            String measurement = InfluxDBUtil.getMetricsMeasurement(sceneId, reportId, customerId);
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
            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
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
            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
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

    private Long reduceMetrics(Long sceneId, Long reportId, Long customerId, Integer podNum, long endTime,
        Long timeWindow, List<ScriptNode> nodes) {
        String logPre = String.format("reduceMetrics %s-%s-%s:%s", sceneId, reportId, customerId,
            DateUtil.showTime(timeWindow));
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
            log.info(logPre + " return 1!timeWindow=" + DateUtil.showTime(timeWindow) + ", endTime=" + DateUtil
                .showTime(endTime));
            return timeWindow;
        }
        //timeWindow如果为空，则获取全部metrics数据，如果不为空则获取该时间窗口的数据
        List<ResponseMetrics> metriceses = queryMetrics(sceneId, reportId, customerId, timeWindow);
        if (CollectionUtils.isNotEmpty(metriceses)) {
            log.info(logPre + " queryMetrics timeWindow=" + DateUtil.showTime(timeWindow) + ", endTime=" + DateUtil
                .showTime(endTime) + ", metricses.size=" + metriceses.size());
            if (null == timeWindow) {
                timeWindow = metriceses.stream().filter(Objects::nonNull)
                    .map(ResponseMetrics::getTime)
                    .filter(l -> l > 0)
                    .findFirst()
                    .orElse(endTime);
            }
            //如果当前处理的时间窗口已经大于结束时间窗口，则退出
            if (timeWindow > endTime) {
                log.info(logPre + " return 3!timeWindow=" + DateUtil.showTime(timeWindow) + ", endTime=" + DateUtil
                    .showTime(endTime));
                return timeWindow;
            }

            List<String> transactions = metriceses.stream().filter(Objects::nonNull)
                .map(ResponseMetrics::getTransaction)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(transactions)) {
                log.info(logPre + " return 4!transactions is empty!");
                return timeWindow;
            }

            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
            long time = timeWindow;

            List<PressureOutput> results = transactions.stream().filter(StringUtils::isNotBlank)
                .map(s -> this.filterByTransactionAndPodNo(metriceses, s))
                .filter(CollectionUtils::isNotEmpty)
                .map(l -> this.toPressureOutput(l, podNum, time))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            //sla处理
            try {
                List<SendMetricsEvent> sendMetricsEventList = getSendMetricsEventList(sceneId, reportId, customerId,
                    timeWindow, results);
                //未finish，发事件
                String existKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY,
                    getTaskKey(sceneId, reportId, customerId));
                if (Boolean.TRUE.equals(redisTemplate.hasKey(existKey))) {
                    sendMetricsEventList.stream().filter(Objects::nonNull)
                        .forEach(this::sendMetrics);
                }
            } catch (Exception e) {
                log.error(
                    "【collector metric】【error-sendMetricsEvents】 write influxDB time : {} sceneId : {}, reportId : "
                        + "{},customerId : {}, error:{}",
                    timeWindow, sceneId, reportId, customerId, e.getMessage());
            }

            //统计没有回传的节点数据
            if (CollectionUtils.isNotEmpty(nodes)) {
                //控制器统计
                List<ScriptNode> controllerNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.CONTROLLER, nodes);
                if (CollectionUtils.isNotEmpty(controllerNodes)) {
                    controllerNodes.stream().filter(Objects::nonNull)
                        //过滤掉已经有数据的控制器
                        .filter(c -> !transactions.contains(c.getXpathMd5()))
                        .forEach(c -> this.summaryNodeMetrics(c, podNum, time, results));
                }
                //线程组统计
                List<ScriptNode> threadGroupNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, nodes);
                if (CollectionUtils.isNotEmpty(threadGroupNodes)) {
                    threadGroupNodes.stream().filter(Objects::nonNull)
                        .filter(n -> !transactions.contains(n.getXpathMd5()))
                        .forEach(n -> this.summaryNodeMetrics(n, podNum, time, results));
                }
                //测试计划统计
                List<ScriptNode> testPlanNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.TEST_PLAN, nodes);
                if (CollectionUtils.isNotEmpty(testPlanNodes)) {
                    testPlanNodes.stream().filter(Objects::nonNull)
                        .filter(t -> !transactions.contains(t.getXpathMd5()))
                        .forEach(t -> this.summaryNodeMetrics(t, podNum, time, results));
                }
            }
            //如果是老版本的，统计ALL
            else {
                int allSaCount = results.stream().filter(Objects::nonNull)
                    //过滤掉all的
                    .filter(p -> !"all".equals(p.getTransaction()))
                    .map(PressureOutput::getSaCount)
                    .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                    .sum();
                //todo 排除事务控制器的数据

                results.stream().filter(Objects::nonNull)
                    .forEach(o -> {
                        if ("all".equalsIgnoreCase(o.getTransaction())) {
                            o.setSaCount(allSaCount);
                        }
                    });
            }
            results.stream().filter(Objects::nonNull)
                .map(p -> InfluxDBUtil.toPoint(measurement, time, p))
                .forEach(influxWriter::insert);

        } else {
            log.info(logPre + ", timeWindow=" + DateUtil.showTime(timeWindow) + "， metrics is empty!");
        }
        log.info(logPre + " finished!timeWindow=" + DateUtil.showTime(timeWindow) + ", endTime=" + DateUtil
            .showTime(endTime));
        return timeWindow;
    }

    /**
     * 单个时间窗口数据，根据transaction过滤，并且每个pod只取1条数据
     */
    private List<ResponseMetrics> filterByTransactionAndPodNo(List<ResponseMetrics> metricses, String transaction) {
        if (CollectionUtils.isEmpty(metricses)) {
            return metricses;
        }
        List<String> pods = Lists.newArrayList();
        return metricses.stream().filter(Objects::nonNull)
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

    /**
     * 统计节点中没有上报的测试计划、线程组、控制器的请求信息
     *
     * @param targetNode 需要统计目标节点
     * @param podNum     podNum
     * @param time       时间窗口
     * @param data       经过统计计算的metrics数据
     */
    private void summaryNodeMetrics(ScriptNode targetNode, int podNum, Long time, List<PressureOutput> data) {
        String transaction = targetNode.getXpathMd5();
        String testName = targetNode.getTestName();
        List<ScriptNode> childSamplers = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, targetNode.getChildren());
        //        List<ScriptNode> childSamplers = JsonPathUtil.getChildSamplers(nodeTree, transaction);
        Map<String, List<PressureOutput>> dataMap = data.stream().collect(
            Collectors.groupingBy(PressureOutput::getTransaction));
        List<PressureOutput> tmpData = new ArrayList<>();
        List<String> samplerTransactions = childSamplers.stream().filter(Objects::nonNull)
            .map(ScriptNode::getXpathMd5)
            .collect(Collectors.toList());
        for (Map.Entry<String, List<PressureOutput>> entry : dataMap.entrySet()) {
            if (samplerTransactions.contains(entry.getKey())) {
                tmpData.addAll(entry.getValue());
            }
        }
        int count = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getCount)
            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            .sum();
        int failCount = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getFailCount)
            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            .sum();
        int saCount = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSaCount)
            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            .sum();
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSentBytes)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        long receiveBytes = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getReceivedBytes)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();

        long sumRt = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSumRt)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();

        double avgRt = NumberUtil.getRate(sumRt, count);

        double maxRt = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getMaxRt)
            .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
            .max()
            .orElse(0);

        double minRt = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getMinRt)
            .mapToDouble(d -> Objects.isNull(d) ? 0 : d)
            .max()
            .orElse(0);

        int activeThreads = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getActiveThreads)
            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            .sum();
        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
        List<String> percentData = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSaPercent)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentData);
        int podNos = tmpData.stream().filter(Objects::nonNull)
            .map(PressureOutput::getDataNum)
            .mapToInt(i -> Objects.isNull(i) ? 0 : i)
            .findFirst()
            .orElse(0);
        double dataRate = NumberUtil.getPercentRate(podNos, podNum, 100d);
        int status = podNos < podNum ? 0 : 1;
        PressureOutput output = new PressureOutput();
        output.setTime(time);
        output.setTransaction(transaction);
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
        output.setDataNum(podNos);
        output.setDataRate(dataRate);
        output.setStatus(status);
        output.setTestName(testName);
        data.add(output);
    }

    /**
     * 实时数据统计
     */
    private PressureOutput toPressureOutput(List<ResponseMetrics> metricses, Integer podNum, long time) {
        if (CollectionUtils.isEmpty(metricses)) {
            return null;
        }
        String transaction = metricses.get(0).getTransaction();
        String testName = metricses.get(0).getTestName();

        int count = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getCount)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        int failCount = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getFailCount)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        int saCount = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getSaCount)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getSentBytes)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        long receivedBytes = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getReceivedBytes)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        long sumRt = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getSumRt)
            .mapToLong(l -> Objects.isNull(l) ? 0 : l)
            .sum();
        double avgRt = NumberUtil.getRate(sumRt, count);
        double maxRt = metricses.stream().filter(Objects::nonNull)
            .mapToDouble(ResponseMetrics::getMaxRt)
            .filter(Objects::nonNull)
            .max()
            .orElse(0);
        double minRt = metricses.stream().filter(Objects::nonNull)
            .mapToDouble(ResponseMetrics::getMinRt)
            .filter(Objects::nonNull)
            .min()
            .orElse(0);
        int activeThreads = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getActiveThreads)
            .mapToInt(i -> Objects.nonNull(i) ? i : 0)
            .sum();
        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
        List<String> percentDatas = metricses.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPercentData)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentDatas);
        List<String> podNos = metricses.stream().filter(Objects::nonNull)
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
        p.setAvgTps(avgTps);
        p.setSaPercent(percentSa);
        p.setDataNum(dataNum);
        p.setDataRate(dataRate);
        p.setStatus(status);
        p.setTestName(testName);
        return p;
    }

    private void finishPushData(Long sceneId, Long reportId, Long customerId, Integer podNum, Long timeWindow,
        long endTime, List<ScriptNode> nodes) {
        String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
        String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
        long nowTimeWindow = CollectorUtil.getTimeWindowTime(System.currentTimeMillis());
        log.info("finishPushData {}-{}-{} last={}, timeWindow={}, endTime={}, now={}", sceneId, reportId, customerId,
            last,
            DateUtil.showTime(timeWindow), DateUtil.showTime(endTime), DateUtil.showTime(nowTimeWindow));

        ReportResult report = reportDao.selectById(reportId);
        if (null != report && null != report.getEndTime()) {
            endTime = Math.min(endTime, report.getEndTime().getTime());
        }

        if (ScheduleConstants.LAST_SIGN.equals(last) || (null != timeWindow && timeWindow > endTime)) {
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
            // 只需触发一次即可
            String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
            Long eTime = (Long)redisTemplate.opsForValue().get(endTimeKey);
            if (null != eTime) {
                log.info("触发手动收尾操作，当前时间窗口：{},结束时间窗口：{},", DateUtil.showTime(timeWindow), DateUtil.showTime(eTime));
                endTime = Math.min(endTime, eTime);
            } else {
                eTime = endTime;
            }
            long endTimeWindow = CollectorUtil.getTimeWindowTime(endTime);
            log.info("触发收尾操作，当前时间窗口：{},结束时间窗口：{},", DateUtil.showTime(timeWindow), DateUtil.showTime(endTimeWindow));
            // 比较 endTime timeWindow
            // 如果结束时间 小于等于当前时间，数据不用补充，
            // 如果结束时间 大于 当前时间，需要补充期间每5秒的数据 延后5s
            while (isSaveLastPoint && timeWindow <= endTimeWindow && timeWindow <= nowTimeWindow) {
                timeWindow = reduceMetrics(sceneId, reportId, customerId, podNum, eTime, timeWindow, nodes);
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
        forceClose(taskKey, nowTimeWindow, sceneId, reportId, customerId);
    }

    /**
     * 实时数据统计
     */
    public void pushData2() {
        ReportDataQueryParam param = new ReportDataQueryParam();
        param.setStatus(0);
        param.setIsDel(0);
        List<ReportResult> results = reportDao.getList(param);
        if (CollectionUtils.isEmpty(results)) {
            log.info("没有需要统计的报告！");
            return;
        }
        List<Long> reportIds = CommonUtil.getList(results, ReportResult::getId);
        log.info("找到需要统计的报告：" + JsonHelper.bean2Json(reportIds));
        results.stream().filter(Objects::nonNull)
            .map(r -> (Runnable)() -> {
                Long sceneId = r.getSceneId();
                Long reportId = r.getId();
                Long customerId = r.getCustomerId();
                String lockKey = String.format("pushData:%s:%s:%s", sceneId, reportId, customerId);
                if (!lock(lockKey, "1")) {
                    return;
                }
                try {
                    List<ScriptNode> nodes = JsonUtil.parseArray(r.getScriptNodeTree(), ScriptNode.class);
                    SceneManageWrapperOutput scene = sceneManageService.getSceneManage(sceneId, null);
                    if (null == scene) {
                        log.info("no such scene manager!sceneId=" + sceneId);
                        return;
                    }
                    if (SceneManageStatusEnum.ifFree(scene.getStatus())) {
                        delTask(sceneId, reportId, customerId);
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
                        //不用递归，而是采用do...while...的方式是防止需要处理的时间段太长引起trackoverflow错误
                        timeWindow = reduceMetrics(sceneId, reportId, customerId, podNum, breakTime, timeWindow, nodes);
                        if (null == timeWindow) {
                            timeWindow = nowTimeWindow;
                            break;
                        }
                        timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
                    } while (timeWindow <= breakTime);
                    finishPushData(sceneId, reportId, customerId, podNum, timeWindow, endTime, nodes);
                } catch (Throwable t) {
                    log.error("pushData2 error!", t);
                } finally {
                    unlock(lockKey, "0");
                }
            })
            .forEach(Executors::execute);
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
        if (StringUtils.isNotBlank(appConfig.getCollector()) && "influxdb".equalsIgnoreCase(appConfig.getCollector())) {
            pushData2();
            return;
        }
        pushData1();
    }

    public void pushData1() {
        try {
            Set<String> keys = this.keys(String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, "*"));
            for (String sceneReportKey : keys) {
                Executors.execute(() -> {
                    try {
                        int lastIndex = sceneReportKey.lastIndexOf(":");
                        if (-1 == lastIndex) {
                            return;
                        }
                        String sceneReportId = sceneReportKey.substring(lastIndex + 1);
                        String[] split = sceneReportId.split("_");
                        Long sceneId = Long.valueOf(split[0]);
                        Long reportId = Long.valueOf(split[1]);
                        Long customerId = null;
                        if (split.length == 3) {
                            customerId = Long.valueOf(split[2]);
                        }
                        ReportResult reportResult = reportDao.selectById(reportId);
                        SceneManageEntity sceneManageEntity = sceneManageDAO.queueSceneById(sceneId);
                        if (SceneManageStatusEnum.ifFree(sceneManageEntity.getStatus())) {
                            delTask(sceneId, reportId, customerId);
                            return;
                        }

                        if (lock(sceneReportKey, "collectorSchedulerPool")) {
                            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);

                            // 记录一个redis 时间计数开始时间的时间窗口开始
                            long timeWindow = refreshTimeWindow(engineName);

                            if (timeWindow == 0) {
                                unlock(sceneReportKey, "collectorSchedulerPool");
                                return;
                            }
                            List<String> transactions = (List<String>)redisTemplate.opsForValue().get(sceneReportKey);
                            if (null == transactions || transactions.size() == 0) {
                                unlock(sceneReportKey, "collectorSchedulerPool");
                                return;
                            }
                            log.info("【collector metric】{}-{}-{}:{}", sceneId, reportId, customerId, timeWindow);
                            String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
                            //判断是否有数据延迟了，需要加快处理流程，否则会丢失数据,保证当前线程刷的数据始终在30s之类
                            if (delayQuick) {
                                while (timeWindow > 0 && System.currentTimeMillis() - timeWindow > delayTimeWindow) {
                                    log.warn("当前push Data延迟时间为{}", timeWindow);
                                    final Long customerIdTmp = customerId;
                                    final long delayTmp = timeWindow;
                                    Executors.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            writeInfluxDB(transactions, taskKey, delayTmp, sceneId, reportId,
                                                customerIdTmp, reportResult.getScriptNodeTree());
                                        }
                                    });
                                    timeWindow = refreshTimeWindow(engineName);
                                }
                            }
                            // 写入数据
                            writeInfluxDB(transactions, taskKey, timeWindow, sceneId, reportId, customerId,
                                reportResult.getScriptNodeTree());
                            // 读取结束标识   手动收尾
                            String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
                            if (ScheduleConstants.LAST_SIGN.equals(last)) {
                                // 只需触发一次即可
                                String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
                                long endTime = CollectorUtil.getEndWindowTime(
                                    (Long)redisTemplate.opsForValue().get(endTimeKey));
                                log.info("触发手动收尾操作，当前时间窗口：{},结束时间窗口：{},", timeWindow, endTime);
                                // 比较 endTime timeWindow
                                // 如果结束时间 小于等于当前时间，数据不用补充，
                                // 如果结束时间 大于 当前时间，需要补充期间每5秒的数据 延后5s
                                endTime = CollectorUtil.addWindowTime(endTime);
                                while (isSaveLastPoint && endTime > timeWindow) {
                                    timeWindow = CollectorUtil.addWindowTime(timeWindow);
                                    // 1、确保 redis->influxDB
                                    log.info("redis->influxDB，当前时间窗口：{},", timeWindow);
                                    writeInfluxDB(transactions, taskKey, timeWindow, sceneId, reportId, customerId,
                                        reportResult.getScriptNodeTree());
                                }
                                log.info("本次压测{}-{}-{},metric数据已经全部上报influxDB", sceneId, reportId, customerId);
                                // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
                                Event event = new Event();
                                event.setEventName("finished");
                                event.setExt(new TaskResult(sceneId, reportId, customerId));
                                eventCenterTemplate.doEvents(event);
                                redisTemplate.delete(last(taskKey));
                                // 删除 timeWindowMap 的key
                                String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
                                timeWindowMap.remove(tempTimestamp);
                            }
                            // 超时自动检修，强行触发关闭
                            forceClose(taskKey, timeWindow, sceneId, reportId, customerId);
                        }
                    } catch (Exception e) {
                        log.error("【collector】Real-time data analysis for anomalies hashkey:{}, error:{}",
                            sceneReportKey, e);
                    } finally {
                        unlock(sceneReportKey, "collectorSchedulerPool");
                    }
                });
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 超时自动检修，强行触发关闭
     *
     * @param taskKey    任务key
     * @param timeWindow 数据窗口
     */
    private void forceClose(String taskKey, Long timeWindow, Long sceneId, Long reportId, Long customerId) {
        Long forceTime = (Long)Optional.ofNullable(redisTemplate.opsForValue().get(forceCloseTime(taskKey))).orElse(0L);
        if (forceTime > 0 && timeWindow >= forceTime) {
            log.info("本次压测{}-{}-{}:触发超时自动检修，强行触发关闭，超时延迟时间-{}，触发时间-{}",
                sceneId, reportId, customerId, forceTime, timeWindow);

            log.info("场景[{}]压测任务已完成,将要开始更新报告{}", sceneId, reportId);
            // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
            SceneManageResult sceneManage = sceneManageDAO.getSceneById(sceneId);
            //如果是强制停止 不需要更新
            log.info("finish scene {}, state :{}", sceneId, Optional.ofNullable(sceneManage)
                .map(SceneManageResult::getType)
                .map(SceneManageStatusEnum::getSceneManageStatusEnum)
                .map(SceneManageStatusEnum::getDesc).orElse("未找到场景"));
            if (sceneManage != null && !sceneManage.getType().equals(SceneManageStatusEnum.FORCE_STOP.getValue())) {
                sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, customerId)
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                    .updateEnum(SceneManageStatusEnum.STOP)
                    .build());
            }
            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, customerId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
            // 删除 timeWindowMap 的key
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
            String tempTimestamp = ScheduleConstants.TEMP_TIMESTAMP_SIGN + engineName;
            timeWindowMap.remove(tempTimestamp);
        }
    }

    private void writeInfluxDB(List<String> transactions, String taskKey, long timeWindow, Long sceneId, Long reportId,
        Long customerId, String nodeTree) {
        long start = System.currentTimeMillis();
        List<PressureOutput> resultList = new ArrayList<>();
        for (String transaction : transactions) {
            Integer count = getIntValue(countKey(taskKey, transaction, timeWindow));
            //todo 判断是否控制器，如果是控制器
            if (null == count || count < 1) {
                log.error(
                    "【collector metric】【null == count || count < 1】 write influxDB time : {},{}-{}-{}-{}, ", timeWindow,
                    sceneId, reportId, customerId, transaction);
                continue;
            }
            Integer failCount = getIntValue(failCountKey(taskKey, transaction, timeWindow));
            Integer saCount = getIntValue(saCountKey(taskKey, transaction, timeWindow));
            Long sumRt = getLongValueFromMap(rtKey(taskKey, transaction, timeWindow));

            Double maxRt = getDoubleValue(maxRtKey(taskKey, transaction, timeWindow));
            Double minRt = getDoubleValue(minRtKey(taskKey, transaction, timeWindow));

            Integer activeThreads = getIntValue(activeThreadsKey(taskKey, transaction, timeWindow));
            Double avgTps = getAvgTps(count);
            // 算平均rt
            Double avgRt = getAvgRt(count, sumRt);
            Double saRate = getSaRate(count, saCount);
            Double successRate = getSuccessRate(count, failCount);

            List<String> percentDatas = getStringValue(percentDataKey(taskKey, transaction, timeWindow));
            String percentSa = calculateSaPercent(percentDatas);

            Map<String, String> tags = new HashMap<>();
            tags.put("transaction", transaction);
            if (StringUtils.isNotBlank(nodeTree)) {
                PressureOutput output = new PressureOutput();
                output.setTransaction(transaction);
                List<String> stringValue = getStringValue(testNameKey(taskKey, transaction, timeWindow));
                if (CollectionUtils.isNotEmpty(stringValue)) {
                    output.setTestName(stringValue.get(0));
                }
                output.setCount(count);
                output.setFailCount(failCount);
                output.setSaCount(saCount);
                output.setSumRt(sumRt);
                output.setMaxRt(maxRt);
                output.setMinRt(minRt);
                output.setAvgRt(avgRt);
                output.setAvgTps(avgTps);
                output.setSa(saRate);
                output.setSuccessRate(successRate);
                output.setSaPercent(percentSa);
                output.setActiveThreads(activeThreads);
                resultList.add(output);
            } else {
                Map<String, Object> fields = getInfluxdbFieldMap(count, failCount,
                    saCount, sumRt, maxRt, minRt, avgTps, avgRt, saRate, successRate, activeThreads, percentSa);
                log.debug("metrics数据入库:时间窗:{},percentSa:{}", timeWindow, percentDatas);
                influxWriter.insert(InfluxDBUtil.getMeasurement(sceneId, reportId, customerId), tags,
                    fields, timeWindow);
            }
            try {
                SendMetricsEvent metrics = getSendMetricsEvent(sceneId, reportId, customerId, timeWindow,
                    transaction, count, failCount, maxRt, minRt, avgTps, avgRt,
                    saRate, successRate);
                //未finish，发事件
                String existKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY,
                    getTaskKey(sceneId, reportId, customerId));
                if (redisTemplate.hasKey(existKey)) {
                    sendMetrics(metrics);
                }
            } catch (Exception e) {
                log.error(
                    "【collector metric】【error】 write influxDB time : {} sceneId : {}, reportId : {},customerId : {}, "
                        + "error:{}",
                    timeWindow, sceneId, reportId, customerId, e.getMessage());
            }
            long end = System.currentTimeMillis();
            log.info(
                "【collector metric】【success】 write influxDB time : {},write time：{} sceneId : {}, reportId : {},"
                    + "customerId : {}",
                timeWindow, (end - start), sceneId, reportId, customerId);

        }
        if (CollectionUtils.isNotEmpty(resultList)) {
            int allSaCount = resultList.stream().filter(Objects::nonNull)
                .map(PressureOutput::getCount)
                .mapToInt(i -> Objects.isNull(i) ? 0 : i)
                .sum();
            List<ScriptNode> childControllers = JsonPathUtil.getChildControllers(nodeTree, null);
            childControllers.stream().filter(Objects::nonNull)
                .filter(c -> !transactions.contains(c.getXpathMd5()))
                .forEach(c -> this.summaryNodeMetrics(c, 0, timeWindow, resultList));

            List<ScriptNode> threadGroupNodes = JsonPathUtil.getChildrenByMd5(nodeTree, null,
                NodeTypeEnum.THREAD_GROUP);
            if (CollectionUtils.isNotEmpty(threadGroupNodes)) {
                threadGroupNodes.stream().filter(Objects::nonNull)
                    .filter(c -> !transactions.contains(c.getXpathMd5()))
                    .forEach(c -> this.summaryNodeMetrics(c, 0, timeWindow, resultList));
            }

            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
            resultList.stream().filter(Objects::nonNull)
                .peek(o -> {
                    if ("all".equalsIgnoreCase(o.getTransaction())) {
                        o.setSaCount(allSaCount);
                        List<ScriptNode> testPlans = JsonPathUtil.getChildrenByMd5(nodeTree, null,
                            NodeTypeEnum.TEST_PLAN);
                        if (CollectionUtils.isNotEmpty(testPlans)) {
                            String testPlanTransaction = testPlans.stream().filter(Objects::nonNull)
                                .map(ScriptNode::getXpathMd5)
                                .filter(StringUtils::isNotBlank)
                                .findFirst()
                                .orElse("all");
                            o.setTransaction(testPlanTransaction);
                        }
                    }
                })
                .map(p -> InfluxDBUtil.toPoint(measurement, timeWindow, p))
                .forEach(influxWriter::insert);
        }
    }

    /**
     * 计算sa
     */
    private String calculateSaPercent(List<String> percentDatas) {
        List<Map<Integer, RtDataOutput>> percentMapList = percentDatas.stream().filter(StringUtils::isNotBlank)
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
        List<RtDataOutput> rtDatas = percentMapList.stream().filter(Objects::nonNull)
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
            for (RtDataOutput d : rtDatas) {
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
        Map<String, Object> fields = new HashMap<>();
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
                metrics.setCustomerId(customerId);
                return metrics;
            }).collect(Collectors.toList());
    }

    private SendMetricsEvent getSendMetricsEvent(Long sceneId, Long reportId, Long customerId, long timeWindow,
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
        metrics.setCustomerId(customerId);
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

}
